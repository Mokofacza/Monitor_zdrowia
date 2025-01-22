package temu.monitorzdrowia.ui.build

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import temu.monitorzdrowia.BuildConfig
import temu.monitorzdrowia.SortType
import temu.monitorzdrowia.data.local.MoodDao
import temu.monitorzdrowia.data.models.Mood

@OptIn(ExperimentalCoroutinesApi::class) // Używamy eksperymentalnych funkcji korutyn
class MoodViewModel(
    private val dao: MoodDao // DAO do interakcji z bazą danych
) : ViewModel() {

    // Prywatny stan przechowujący bieżące dane UI
    private val _state = MutableStateFlow(MoodState())
    // Prywatny stan przechowujący aktualny typ sortowania
    private val _sortType = MutableStateFlow(SortType.TIME)

    // Flow nastrojów sortowanych według wybranego typu
    private val _mood = _sortType
        .flatMapLatest { sortType ->
            when (sortType) {
                SortType.TIME -> dao.orderByDateDescending()
                SortType.RATING -> dao.orderByRatingDescending()
                SortType.TIME1 -> dao.orderByDateAscending()
                SortType.RATING1 -> dao.orderByRatingAscending()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    // Publiczny stan, łączący _state, _sortType i _mood
    val state = combine(_state, _sortType, _mood) { state, sortType, mood ->
        state.copy(
            mood = mood,
            sortType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MoodState())

    // Funkcja obsługująca różne zdarzenia aplikacji
    fun onEvent(event: MoodEvent) {
        when (event) {
            is MoodEvent.DeleteMood -> {
                // Usuwanie nastroju z bazy danych
                viewModelScope.launch {
                    dao.deleteMood(event.mood)
                }
            }

            MoodEvent.HideDialog -> {
                // Ukrywanie dialogu dodawania nastroju
                _state.update { it.copy(isAddingMood = false) }
            }

            MoodEvent.SaveRating -> {
                // Zapisywanie nowego nastroju
                val note = state.value.note
                val moodRating = state.value.moodRating
                if (note.isBlank() || moodRating < 1) { // Sprawdzenie poprawności danych
                    return
                }
                val mood = Mood(
                    note = note,
                    moodRating = moodRating
                )
                viewModelScope.launch {
                    dao.insertMood(mood) // Wstawianie nastroju do bazy danych
                }
                // Resetowanie stanu po zapisie
                _state.update {
                    it.copy(
                        isAddingMood = false,
                        moodRating = 5,
                        note = ""
                    )
                }
            }

            is MoodEvent.SetNote -> {
                // Aktualizacja notatki w stanie
                _state.update { it.copy(note = event.note) }
            }

            is MoodEvent.SetRating -> {
                // Aktualizacja oceny nastroju w stanie
                _state.update { it.copy(moodRating = event.moodRating) }
            }

            MoodEvent.ShowDialog -> {
                // Pokazywanie dialogu dodawania nastroju
                _state.update { it.copy(isAddingMood = true) }
            }

            is MoodEvent.SortMood -> {
                // Aktualizacja typu sortowania
                _sortType.value = event.sortType
            }

            // Poniższe zdarzenia dotyczą obsługi dialogu analizy nastroju:
            MoodEvent.ShowAnalysisDialog -> {
                _state.update { it.copy(isAnalyzingMood = true) }
            }

            MoodEvent.HideAnalysisDialog -> {
                _state.update { it.copy(isAnalyzingMood = false) }
            }

            is MoodEvent.AnalyzeMood -> {
                viewModelScope.launch {
                    // Łączymy opisy z przekazanej listy Mood – każdy wpis oddzielony jest znakiem nowej linii
                    val combinedDescriptions = event.moods.joinToString(separator = "\n") { it.note }

                    // Tworzymy prompt zawierający opisy
                    val prompt = "Przeanalizuj poniższe opisy nastroju, są one podane od najnowszych i daj mi radę." +
                            " są to wpisy odnośnie nastroju pacjenta.chce byś odpowiedział w maksymalnie 10 zdaniach." +
                            " wciel sie w role jego lekarza:\n$combinedDescriptions"

                    // Wywołanie modelu generatywnego z utworzonym promptem
                    val response = generativeModel.generateContent(prompt)

                    // Możesz wypisać wynik do logów, aby zweryfikować odpowiedź
                    print(response.text)

                    // Aktualizujemy stan z wynikiem analizy – wynik pochodzi z wygenerowanego tekstu modelu
                    _state.update { it.copy(analysisResult = response.text) }
                }
            }


        }
    }
    val generativeModel = GenerativeModel(
        // The Gemini 1.5 models are versatile and work with most use cases
        modelName = "gemini-1.5-flash",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
        apiKey = BuildConfig.apikey
    )

}

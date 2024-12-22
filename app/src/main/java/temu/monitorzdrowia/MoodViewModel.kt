package temu.monitorzdrowia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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
                SortType.TIME -> dao.orderByDateAndTime()
                SortType.RATING -> dao.orderByRating()
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
        }
    }
}

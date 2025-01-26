package temu.monitorzdrowia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import temu.monitorzdrowia.BuildConfig
import temu.monitorzdrowia.model.SortType
import temu.monitorzdrowia.model.data.local.MoodDao
import temu.monitorzdrowia.model.entities.Mood
import com.github.mikephil.charting.data.Entry

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
}

@OptIn(ExperimentalCoroutinesApi::class)
class MoodViewModel(
    private val dao: MoodDao
) : ViewModel() {

    private val _state = MutableStateFlow(MoodState())
    private val _sortType = MutableStateFlow(SortType.TIME)

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

    // Funkcja do generowania moodEntries
    private fun generateMoodEntries(moods: List<Mood>): List<Entry> {
        return moods.sortedBy { it.timestamp }
            .mapIndexed { index, mood ->
                Entry(index.toFloat(), mood.moodRating.toFloat())
            }
    }

    val state = combine(_state, _sortType, _mood) { state, sortType, mood ->
        val entries = generateMoodEntries(mood)
        state.copy(
            mood = mood,
            sortType = sortType,
            moodEntries = entries
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MoodState())

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: MoodEvent) {
        when (event) {
            is MoodEvent.DeleteMood -> {
                viewModelScope.launch {
                    dao.deleteMood(event.mood)
                    _uiEvent.send(UiEvent.ShowToast("Nastrój usunięty pomyślnie"))
                }
            }

            MoodEvent.HideDialog -> {
                _state.update { it.copy(isAddingMood = false) }
            }

            MoodEvent.ShowDialog -> {
                _state.update { it.copy(isAddingMood = true) }
            }

            MoodEvent.HideChart -> {
                _state.update { it.copy(isChartVisible = false) }
            }

            MoodEvent.ShowChart -> {
                _state.update { it.copy(isChartVisible = true) }
            }

            MoodEvent.SaveRating -> {
                val note = state.value.note
                val moodRating = state.value.moodRating
                if (note.isBlank() || moodRating < 1) {
                    return
                }
                val mood = Mood(
                    note = note,
                    moodRating = moodRating
                )
                viewModelScope.launch {
                    dao.insertMood(mood)
                    _uiEvent.send(UiEvent.ShowToast("Nastrój dodany pomyślnie"))
                }
                _state.update {
                    it.copy(
                        isAddingMood = false,
                        moodRating = 5,
                        note = ""
                    )
                }
            }

            is MoodEvent.SetNote -> {
                _state.update { it.copy(note = event.note) }
            }

            is MoodEvent.SetRating -> {
                _state.update { it.copy(moodRating = event.moodRating) }
            }

            is MoodEvent.SortMood -> {
                _sortType.value = event.sortType
            }

            MoodEvent.ShowAnalysisDialog -> {
                _state.update { it.copy(isAnalyzingMood = true) }
            }

            MoodEvent.HideAnalysisDialog -> {
                _state.update { it.copy(isAnalyzingMood = false) }
            }

            is MoodEvent.ResetAnalysisResult -> {
                _state.value = state.value.copy(analysisResult = null)
            }

            is MoodEvent.AnalyzeMood -> {
                viewModelScope.launch {
                    val combinedDescriptions = event.moods.joinToString(separator = "\n") { mood ->
                        "Ocena: ${mood.moodRating}, Opis: ${mood.note}, Data wpisue: ${mood.timestamp}"
                    }

                    val userData = """
                        Data urodzenia: ${event.userProfile.birthDate}
                        Płeć: ${event.userProfile.sex}
                        Wielkość miasta: ${event.userProfile.citySize}
                    """.trimIndent()

                    val prompt = """
                        Przeanalizuj poniższe opisy nastroju, są one podane od najnowszych i daj mi radę.
                        są to wpisy odnośnie nastroju pacjenta. chce byś odpowiedział w maksymalnie 10 zdaniach.
                        wciel sie w role jego lekarza:
                        
                        Dane użytkownika:
                        $userData

                        Wpisy nastroju:
                        $combinedDescriptions
                    """.trimIndent()

                    val response = generativeModel.generateContent(prompt)
                    _state.update { it.copy(analysisResult = response.text) }
                }
            }
        }
    }

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.API_KEY
    )
}

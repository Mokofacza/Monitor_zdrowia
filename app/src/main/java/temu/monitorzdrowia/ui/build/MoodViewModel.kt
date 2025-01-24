package temu.monitorzdrowia.ui.build

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import temu.monitorzdrowia.BuildConfig
import temu.monitorzdrowia.SortType
import temu.monitorzdrowia.data.local.MoodDao
import temu.monitorzdrowia.data.models.Mood

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

    val state = combine(_state, _sortType, _mood) { state, sortType, mood ->
        state.copy(
            mood = mood,
            sortType = sortType
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

            MoodEvent.ShowDialog -> {
                _state.update { it.copy(isAddingMood = true) }
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
                    val combinedDescriptions = event.moods.joinToString(separator = "\n") { it.note }

                    val prompt = "Przeanalizuj poniższe opisy nastroju, są one podane od najnowszych i daj mi radę." +
                            " są to wpisy odnośnie nastroju pacjenta.chce byś odpowiedział w maksymalnie 10 zdaniach." +
                            " wciel sie w role jego lekarza:\n$combinedDescriptions"

                    val response = generativeModel.generateContent(prompt)

                    print(response.text)

                    _state.update { it.copy(analysisResult = response.text) }
                }
            }
        }
    }

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apikey
    )
}

package temu.monitorzdrowia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MoodViewModel(
    private val dao: MoodDao
): ViewModel() {
    private val _state = MutableStateFlow(MoodState())
    private val _sortType = MutableStateFlow((SortType.TIME))
    private val _mood = _sortType
        .flatMapLatest { sortType ->
            when(sortType) {
                SortType.TIME -> dao.orderByDateAndTime()
                SortType.RATING -> dao.orderByRating()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList() )
    val state = combine(_state, _sortType, _mood) {
        state, sortType, mood ->
        state.copy(
            mood = mood,
            sortType = sortType
                )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MoodState() )
    fun onEvent(event: MoodEvent){
        when (event) {
            is MoodEvent.DeleteMood -> {
                viewModelScope.launch {
                    dao.deleteMood(event.mood)
                }
            }

            MoodEvent.HideDialog -> {
                _state.update{ it.copy(
                    isAddingMood = false
                )
                }
            }

            MoodEvent.SaveRating -> {
                val note = state.value.note
                val moodRating = state.value.moodRating
                if(note.isBlank() || moodRating <= 0 ){
                    return
                }
                val mood = Mood(
                    note = note,
                    moodRating = moodRating
                )
                viewModelScope.launch {
                    dao.insertMood(mood)
                }
                _state.update { it.copy(
                    isAddingMood = false,
                    moodRating = 0,
                    note = ""
                ) }
            }
            is MoodEvent.SetNote -> {
                _state.update { it.copy(
                    note = event.note
                ) }
            }
            is MoodEvent.SetRating -> {
                _state.update { it.copy(
                    moodRating = event.moodRating
                ) }
            }
            MoodEvent.ShowDialog -> {
                _state.update { it.copy(
                    isAddingMood = true
                ) }
            }

            is MoodEvent.SortMood -> {
                _sortType.value = event.sortType
            }
        }
    }

}
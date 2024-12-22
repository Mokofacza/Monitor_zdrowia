package temu.monitorzdrowia

import java.time.LocalDateTime

data class MoodState(
    val mood: List<Mood> = emptyList(),
    val moodRating: Int = 5,
    val note: String = "",
    val isAddingMood: Boolean = false,
    val sortType: SortType = SortType.TIME
)

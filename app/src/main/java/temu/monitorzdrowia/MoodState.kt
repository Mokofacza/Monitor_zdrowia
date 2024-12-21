package temu.monitorzdrowia

import java.time.LocalDateTime

data class MoodState(
    val mood: List<Mood> = emptyList(),
    val moodRating: Int = 0,
    val note: String = "",
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val isAddingMood: Boolean = false,
    val sortType: SortType = SortType.TIME
)

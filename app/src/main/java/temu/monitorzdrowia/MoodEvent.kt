package temu.monitorzdrowia

sealed interface MoodEvent {
    object SaveRating: MoodEvent
    data class SetNote(val note: String): MoodEvent
    data class SetRating(val moodRating: Int): MoodEvent
    object ShowDialog: MoodEvent
    object HideDialog: MoodEvent
    data class DeleteMood(val mood: Mood): MoodEvent
    data class SortMood(val sortType: SortType): MoodEvent

}
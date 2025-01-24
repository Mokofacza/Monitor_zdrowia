package temu.monitorzdrowia.ui.build

import temu.monitorzdrowia.SortType
import temu.monitorzdrowia.data.models.Mood
import temu.monitorzdrowia.data.models.User

// Definiuje różne zdarzenia związane z zarządzaniem nastrojami w aplikacji.
// Te zdarzenia są obsługiwane przez ViewModel do aktualizacji stanu UI.

sealed class MoodEvent {
    object ShowDialog : MoodEvent()
    object HideDialog : MoodEvent()
    object ShowAnalysisDialog : MoodEvent()
    object HideAnalysisDialog : MoodEvent()
    data class SortMood(val sortType: SortType) : MoodEvent()
    data class DeleteMood(val mood: Mood) : MoodEvent()
    data class SetNote(val note: String) : MoodEvent()
    data class SetRating(val moodRating: Int) : MoodEvent()
    object SaveRating : MoodEvent()
    object ResetAnalysisResult : MoodEvent()
    data class AnalyzeMood(
        val moods: List<Mood>,
        val userProfile: User,
    ) : MoodEvent()
}


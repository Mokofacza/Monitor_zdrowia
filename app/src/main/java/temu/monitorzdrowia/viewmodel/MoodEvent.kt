package temu.monitorzdrowia.viewmodel

import temu.monitorzdrowia.model.SortType
import temu.monitorzdrowia.model.entities.Mood
import temu.monitorzdrowia.model.entities.User

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
    object ShowChart : MoodEvent()
    object HideChart : MoodEvent()
    data class AnalyzeMood(
        val moods: List<Mood>,
        val userProfile: User,
    ) : MoodEvent()
}


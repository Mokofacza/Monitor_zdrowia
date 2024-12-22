package temu.monitorzdrowia

// Definiuje różne zdarzenia związane z zarządzaniem nastrojami w aplikacji.
// Te zdarzenia są obsługiwane przez ViewModel do aktualizacji stanu UI.

sealed interface MoodEvent {

    // Zdarzenie zapisu nowego nastroju
    object SaveRating : MoodEvent

    // Zdarzenie ustawienia notatki do nastroju
    data class SetNote(val note: String) : MoodEvent

    // Zdarzenie ustawienia oceny nastroju
    data class SetRating(val moodRating: Int) : MoodEvent

    // Zdarzenie wyświetlenia dialogu dodawania nastroju
    object ShowDialog : MoodEvent

    // Zdarzenie ukrycia dialogu dodawania nastroju
    object HideDialog : MoodEvent

    // Zdarzenie usunięcia istniejącego nastroju
    data class DeleteMood(val mood: Mood) : MoodEvent

    // Zdarzenie sortowania listy nastrojów według określonego typu
    data class SortMood(val sortType: SortType) : MoodEvent

}

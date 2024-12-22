package temu.monitorzdrowia.ui.build

import temu.monitorzdrowia.SortType
import temu.monitorzdrowia.data.models.Mood

// Definiuje stan UI dla zarządzania nastrojami w aplikacji.

data class MoodState(
    val mood: List<Mood> = emptyList(), // Lista wszystkich zapisanych nastrojów
    val moodRating: Int = 5, // Aktualna ocena nastroju (domyślnie 5)
    val note: String = "", // Aktualna notatka do nastroju
    val isAddingMood: Boolean = false, // Flaga określająca, czy dialog dodawania nastroju jest widoczny
    val sortType: SortType = SortType.TIME // Aktualny typ sortowania nastrojów
)

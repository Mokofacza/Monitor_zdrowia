package temu.monitorzdrowia.ui.build

import temu.monitorzdrowia.SortType
import temu.monitorzdrowia.data.models.Mood
import temu.monitorzdrowia.data.models.User

// Stan UI dla zarządzania nastrojami w aplikacji
data class MoodState(
    val mood: List<Mood> = emptyList(),        // Lista wszystkich zapisanych nastrojów
    val moodRating: Int = 5,                     // Aktualna ocena nastroju (domyślnie 5)
    val note: String = "",                       // Aktualna notatka do nastroju
    val isAddingMood: Boolean = false,           // Flaga określająca, czy dialog dodawania nastroju jest widoczny
    val isAnalyzingMood: Boolean = false,        // Flaga określająca, czy dialog analizy nastroju jest widoczny
    val analysisResult: String? = null,          // Opcjonalny wynik analizy nastroju
    val sortType: SortType = SortType.TIME,       // Aktualny typ sortowania nastrojów
    val user: User? = null // Pole przechowujące dane użytkownika

)

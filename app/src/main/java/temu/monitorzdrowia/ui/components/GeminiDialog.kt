package temu.monitorzdrowia.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import temu.monitorzdrowia.model.entities.Mood
import temu.monitorzdrowia.model.entities.User
import temu.monitorzdrowia.utils.ShakeDetector
import kotlin.math.roundToInt

/**
 * Dialog analizujący nastrój przy użyciu ostatnich wpisów (rating + opis).
 *
 * @param moodHistory Lista wpisów nastroju.
 * @param analysisResult (Opcjonalnie) Wynik analizy, który może być wyświetlony.
 * @param onAnalyze Funkcja uruchamiana po kliknięciu przycisku "Analizuj".
 *                  Wybrana lista wpisów oraz dane użytkownika zostaną przekazane do logiki analizy.
 * @param onDismiss Funkcja wywoływana przy zamknięciu dialogu.
 * @param onShake Funkcja wywoływana przy wykryciu zatrzęsienia telefonu.
 * @param userProfile Dane profilu użytkownika.
 * @param modifier Opcjonalne modyfikatory.
 */
@Composable
fun GeminiDialog(
    moodHistory: List<Mood>,
    analysisResult: String? = null,
    onAnalyze: (List<Mood>, User) -> Unit,
    onDismiss: () -> Unit,
    onShake: () -> Unit,
    userProfile: User,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Inicjalizujemy ShakeDetector i przekazujemy callback onShake
    val shakeDetector = remember { ShakeDetector(onShake) }

    DisposableEffect(Unit) {
        shakeDetector.start(context)
        onDispose {
            shakeDetector.stop()
        }
    }

    if (moodHistory.isEmpty()) {
        // Wyświetlamy dialog informujący o braku wpisów do analizy
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Brak wpisów") },
            text = { Text(text = "Brak wpisów do analizy.") },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text("OK")
                }
            },
            modifier = modifier
        )
    } else {
        // Jeśli są wpisy, wyświetlamy standardowy dialog analizy nastroju
        // najnowsze wpisy na początku
        val newestFirst = moodHistory.sortedByDescending { it.timestamp }

        val sliderMax = newestFirst.size.coerceAtMost(5)

        // Domyślnie wybieramy np. 3 wpisy lub liczbę wpisów, jeśli jest ich mniej
        var selectedCount by remember { mutableStateOf(if (sliderMax >= 3) 3f else sliderMax.toFloat()) }
        // Pobierz najnowsze wpisy na podstawie wybranej liczby
        val selectedMoods = newestFirst.take(selectedCount.toInt())

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Analiza Nastroju") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    if (analysisResult == null) {
                        if (sliderMax > 1) {
                            // Wyświetlamy suwak tylko jeśli jest więcej niż jeden wpis
                            Text(
                                text = "Liczba wpisów do analizy: ${selectedCount.toInt()}",
                                fontSize = 16.sp
                            )
                            Slider(
                                value = selectedCount,
                                onValueChange = { selectedCount = it.roundToInt().toFloat() },
                                valueRange = 1f..sliderMax.toFloat(),
                                steps = (sliderMax - 2).coerceAtLeast(0),
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        } else {
                            // Jeśli tylko jeden wpis, wyświetlamy statyczny tekst bez suwaka
                            Text(
                                text = "Liczba wpisów do analizy: ${selectedCount.toInt()}",
                                fontSize = 16.sp
                            )
                        }
                        // Wyświetlenie krótkiego podglądu wybranych wpisów
                        selectedMoods.forEach { mood ->
                            Text(
                                text = "Ocena: ${mood.moodRating} - Opis: ${
                                    if (mood.note.length > 50) mood.note.take(50) + "..." else mood.note
                                }",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    } else {
                        // Gdy jest wynik analizy, ukrywamy suwak oraz podgląd wpisów i wyświetlamy tylko wynik
                        Text(
                            text = "Wynik analizy: $analysisResult",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                // Przycisk "Analizuj" powinien być aktywny tylko wtedy, gdy nie ma wyniku analizy
                if (analysisResult == null) {
                    Button(
                        onClick = { onAnalyze(selectedMoods, userProfile) }
                    ) {
                        Text(text = "Analizuj")
                    }
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss
                ) {
                    Text(text = "Anuluj")
                }
            },
            modifier = modifier
        )
    }
}

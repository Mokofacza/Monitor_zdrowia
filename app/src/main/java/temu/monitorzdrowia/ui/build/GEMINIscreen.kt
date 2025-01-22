package temu.monitorzdrowia.ui.build

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import temu.monitorzdrowia.data.models.Mood

/**
 * Dialog analizujący nastrój przy użyciu ostatnich wpisów (rating + opis).
 *
 * @param moodHistory Lista wpisów nastroju.
 * @param analysisResult (Opcjonalnie) Wynik analizy, który może być wyświetlony.
 * @param onAnalyze Funkcja uruchamiana po kliknięciu przycisku "Analizuj".
 *                  Wybrana lista wpisów zostanie przekazana do logiki analizy.
 * @param onDismiss Funkcja wywoływana przy zamknięciu dialogu.
 * @param modifier Opcjonalne modyfikatory.
 */
@Composable
fun GeminiDialog(
    moodHistory: List<Mood>,
    analysisResult: String? = null,
    onAnalyze: (List<Mood>) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Upewnij się, że mamy co najmniej jeden wpis do analizy.
    val maxEntries = moodHistory.size.coerceAtLeast(1)
    // Posortuj listę tak, aby najnowsze wpisy były na początku
    val newestFirst = moodHistory.sortedByDescending { it.timestamp }
    // Upewnij się, że suwak nie przekracza 5, nawet jeśli lista ma więcej wpisów
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
                modifier = Modifier.fillMaxWidth()
            ) {
                if (analysisResult == null) {
                    // Wyświetlamy suwak oraz podgląd wybranych wpisów tylko gdy nie ma wyniku analizy
                    Text(
                        text = "Liczba wpisów do analizy: ${selectedCount.toInt()}",
                        fontSize = 16.sp
                    )
                    Slider(
                        value = selectedCount,
                        onValueChange = { selectedCount = it },
                        valueRange = 1f..sliderMax.toFloat(),
                        steps = (sliderMax - 1).coerceAtLeast(0),
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
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
                    onClick = { onAnalyze(selectedMoods) }
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

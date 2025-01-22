package temu.monitorzdrowia.ui.build

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Dialog analizujący nastrój przy użyciu ostatnich ocen z historii.
 *
 * @param moodHistory Lista ostatnich ocen nastroju (np. 5–10 wartości).
 * @param analysisResult (Opcjonalnie) Wynik analizy, który może być wyświetlony.
 * @param onAnalyze Funkcja uruchamiana po kliknięciu przycisku "Analizuj".
 *                  Przekazywana lista ocen zostanie wysłana do API.
 * @param onDismiss Funkcja wywoływana przy zamknięciu dialogu.
 * @param modifier Opcjonalne modyfikatory.
 */
@Composable
fun GeminiDialog(
    moodHistory: List<Int>,
    analysisResult: String? = null,
    onAnalyze: (List<Int>) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Analiza Nastroju") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Wyświetlenie ostatnich ocen nastroju
                Text(
                    text = "Ostatnie nastroje: ${moodHistory.joinToString(separator = ", ")}",
                    fontSize = 16.sp
                )

                // Jeśli wynik analizy został już pobrany, wyświetl go
                analysisResult?.let {
                    Text(
                        text = "Wynik analizy: $it",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Opcjonalnie – możesz dodać suwak do zmiany dodatkowego parametru analizy:
                /*Slider(
                    value = someValue,
                    onValueChange = { newValue ->
                        // Przykładowa zmiana jakiegoś parametru analizy
                    },
                    valueRange = 0f..100f,
                    steps = 4,
                    modifier = Modifier.padding(vertical = 16.dp)
                )*/
            }
        },
        confirmButton = {
            Button(
                onClick = { onAnalyze(moodHistory) }
            ) {
                Text(text = "Analizuj")
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

package temu.monitorzdrowia.ui.components

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.Entry

@Composable
fun MoodChartDialog(
    moodEntries: List<Entry>,
    labels: List<String>,
    onDismiss: () -> Unit
) {
    // Obsługa pustej listy nastrojów
    if (moodEntries.isEmpty()) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Brak danych") },
            text = { Text(text = "Brak dostępnych wpisów nastroju do wyświetlenia wykresu.") },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text("Zamknij")
                }
            }
        )
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Wykres nastroju") },
        text = {
            AndroidView(
                factory = { ctx ->
                    LineChart(ctx).apply {
                        // Konfiguracja datasetu
                        val dataSet = LineDataSet(moodEntries, "Nastrój").apply {
                            color = Color.BLUE
                            setCircleColor(Color.RED)
                            lineWidth = 2f
                            circleRadius = 4f
                            setDrawValues(false)
                            setDrawFilled(false)
                            mode = LineDataSet.Mode.LINEAR
                        }

                        // Ustawienie danych do wykresu
                        data = LineData(dataSet)

                        // Konfiguracja osi X
                        xAxis.apply {
                            position = XAxis.XAxisPosition.BOTTOM
                            setDrawGridLines(false)
                            granularity = 1f
                            labelCount = labels.size
                            valueFormatter = IndexAxisValueFormatter(labels)
                            textColor = Color.BLACK
                        }

                        // Konfiguracja osi Y
                        axisLeft.apply {
                            axisMinimum = 0f
                            axisMaximum = 10f
                            granularity = 1f
                            setDrawGridLines(true)
                            textColor = Color.BLACK
                        }
                        axisRight.isEnabled = false

                        // Konfiguracja opisu wykresu
                        description = Description().apply {
                            text = ""
                        }

                        // Wyłączenie legendy
                        legend.isEnabled = false

                        // Inne konfiguracje wyglądu
                        setTouchEnabled(false) // Wyłączenie interakcji
                        setScaleEnabled(false) // Wyłączenie skalowania
                        setPinchZoom(false)
                        setBackgroundColor(Color.TRANSPARENT)
                        invalidate() // Odświeżenie wykresu

                        // Opcjonalnie: Dodanie animacji
                        animateX(1000) // Animacja w osi X trwająca 1 sekundę
                        animateY(1000) // Animacja w osi Y trwająca 1 sekundę
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Zamknij")
            }
        }
    )
}

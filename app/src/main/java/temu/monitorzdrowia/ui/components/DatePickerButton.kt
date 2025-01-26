package temu.monitorzdrowia.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.util.Calendar

@Composable
fun DatePickerButton(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    selectedDate?.let {
        calendar.set(it.year, it.monthValue - 1, it.dayOfMonth)
    }
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    Button(
        onClick = {
            DatePickerDialog(
                context,
                { _, selectedYear, selectedMonth, selectedDay ->
                    onDateSelected(LocalDate.of(selectedYear, selectedMonth + 1, selectedDay))
                },
                year,
                month,
                day
            ).apply {
                // Ustawienie maksymalnej daty na bieżący czas
                datePicker.maxDate = Calendar.getInstance().timeInMillis
            }.show()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(text = selectedDate?.toString() ?: "Wybierz datę urodzenia")
    }
}

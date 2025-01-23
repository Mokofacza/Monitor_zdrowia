package temu.monitorzdrowia.ui.build

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import android.app.DatePickerDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

@Composable
fun DatePickerButton(
    label: String,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    val context = LocalContext.current

    // Przygotowanie kalendarza do wyświetlenia aktualnie wybranej daty
    val calendar = Calendar.getInstance()
    selectedDate?.let {
        // Miesiące w klasie Calendar są liczone od 0,
        // a w LocalDate od 1, stąd "it.monthValue - 1"
        calendar.set(it.year, it.monthValue - 1, it.dayOfMonth)
    }

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    // Tworzymy DatePickerDialog, który wywoła onDateSelected z wybraną datą
    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            val newDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDayOfMonth)
            onDateSelected(newDate)
        },
        year,
        month,
        dayOfMonth
    )

    // Przycisk – po kliknięciu pokażemy dialog
    Button(onClick = {
        datePickerDialog.show()
    }) {
        Text(label)
    }
}
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel
) {
    val state = viewModel.state.collectAsState().value

    // Jeśli user istnieje, wyświetlamy informacje
    // (lub cokolwiek innego chcesz pokazać)
    if (state.user != null) {
        ProfileContent(state.user)
    } else {
        // Możesz wyświetlić pusty ekran, tło, cokolwiek
        Text(
            text = "Brak użytkownika w bazie",
            fontSize = 24.sp
        )
    }

    // Jeżeli flaga isDialogVisible jest true => pokaż AlertDialog
    if (state.isDialogVisible) {
        AlertDialog(
            onDismissRequest = {
                // ...
            },
            title = { Text("Uzupełnij dane użytkownika") },
            text = {
                Column {
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { viewModel.onEvent(ProfileEvent.SetName(it)) },
                        label = { Text("Imię") }
                    )
                    OutlinedTextField(
                        value = state.subname,
                        onValueChange = { viewModel.onEvent(ProfileEvent.SetSubName(it)) },
                        label = { Text("Nazwisko") }
                    )

                    // Komponent do wybierania daty z kalendarza
                    DatePickerButton(
                        label = "Wybierz datę urodzenia",
                        selectedDate = state.birthDate,
                        onDateSelected = { newDate ->
                            viewModel.onEvent(ProfileEvent.SetBirthDate(newDate))
                        }
                    )

                    // Pokazujemy aktualnie wybraną datę (jeśli jest)
                    Text("Wybrana data: ${state.birthDate ?: "Brak"}")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onEvent(ProfileEvent.SaveUser)
                    }
                ) {
                    Text("Zapisz")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(ProfileEvent.HideFillDataDialog) }) {
                    Text("Anuluj")
                }
            }
        )
    }
}

@Composable
fun ProfileContent(user: temu.monitorzdrowia.data.models.User) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Imię: ${user.name}")
        Text(text = "Nazwisko: ${user.subname}")
        Text(text = "Data urodzenia: ${user.birthDate}")
    }
}

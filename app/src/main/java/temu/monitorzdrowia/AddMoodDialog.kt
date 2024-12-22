package temu.monitorzdrowia

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Ten plik zawiera funkcję, która wyświetla dialog umożliwiający użytkownikowi dodanie nowego nastroju.
// Użytkownik może wpisać notatkę i wybrać ocenę nastroju za pomocą suwaka.

@Composable
fun AddMoodDialog(
    state: MoodState, // Aktualny stan UI aplikacji
    onEvent: (MoodEvent) -> Unit, // Funkcja do obsługi zdarzeń z dialogu
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = {
            onEvent(MoodEvent.HideDialog) // Ukryj dialog, gdy użytkownik go odrzuci
        },
        title = { Text(text = "Dodaj Nastrój") }, // Tytuł dialogu
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth() // Rozciągnięcie kolumny na pełną szerokość
            ) {
                // Pole tekstowe do wpisania opisu nastroju
                TextField(
                    value = state.note,
                    onValueChange = {
                        onEvent(MoodEvent.SetNote(it)) // Aktualizuj notatkę w stanie
                    },
                    placeholder = {
                        Text(text = "Opis nastroju")
                    },
                    modifier = Modifier.fillMaxWidth() // Rozciągnięcie pola na pełną szerokość
                )

                // Suwak do wyboru oceny nastroju
                Slider(
                    value = state.moodRating.toFloat(),
                    onValueChange = { newValue ->
                        onEvent(MoodEvent.SetRating(newValue.toInt())) // Aktualizuj ocenę w stanie
                    },
                    valueRange = 1f..10f, // Zakres wartości suwaka od 1 do 10
                    steps = 8,
                    modifier = Modifier.padding(vertical = 16.dp) // Dodanie odstępów pionowych
                )

                // Wyświetlanie aktualnej wybranej oceny
                Row(
                    verticalAlignment = Alignment.CenterVertically, // Wyrównanie pionowe elementów
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Ocena: ${state.moodRating}", // Tekst pokazujący aktualną ocenę
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onEvent(MoodEvent.SaveRating) // Zapisz nowy nastrój
                }
            ) {
                Text(text = "Zapisz")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onEvent(MoodEvent.HideDialog) // Anuluj dodawanie nastroju
                }
            ) {
                Text(text = "Anuluj")
            }
        },
        modifier = modifier // Dodatkowe modyfikatory, jeśli są przekazane
    )
}

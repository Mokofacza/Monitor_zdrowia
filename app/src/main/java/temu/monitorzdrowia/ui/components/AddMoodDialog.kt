package temu.monitorzdrowia.ui.components

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
import temu.monitorzdrowia.viewmodel.MoodEvent
import temu.monitorzdrowia.viewmodel.MoodState

/**
 * Dialog umożliwiający użytkownikowi dodanie nowego nastroju.
 *
 * @param state Aktualny stan UI związany z nastrojami.
 * @param onEvent Funkcja do obsługi zdarzeń generowanych przez dialog (np. zapisywanie nastroju, ukrywanie dialogu).
 * @param modifier Opcjonalny modyfikator do dostosowania wyglądu dialogu.
 */

@Composable
fun AddMoodDialog(
    state: MoodState,
    onEvent: (MoodEvent) -> Unit, // Funkcja do obsługi zdarzeń z dialogu
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = {
            onEvent(MoodEvent.HideDialog) // Ukryj dialog
        },
        title = { Text(text = "Dodaj Nastrój") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = state.note,
                    onValueChange = {
                        onEvent(MoodEvent.SetNote(it))
                    },
                    placeholder = {
                        Text(text = "Opis nastroju")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Suwak do wyboru oceny nastroju
                Slider(
                    value = state.moodRating.toFloat(),
                    onValueChange = { newValue ->
                        onEvent(MoodEvent.SetRating(newValue.toInt()))
                    },
                    valueRange = 1f..10f, // Zakres
                    steps = 8,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Ocena: ${state.moodRating}",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onEvent(MoodEvent.SaveRating)
                }
            ) {
                Text(text = "Zapisz")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onEvent(MoodEvent.HideDialog)
                }
            ) {
                Text(text = "Anuluj")
            }
        },
        modifier = modifier
    )
}

package temu.monitorzdrowia

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun AddMoodDialog(
    state: MoodState,
    onEvent: (MoodEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = {
            onEvent(MoodEvent.HideDialog)
        },
        title = { Text(text = "Dodaj Nastrój") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Pole tekstowe dla opisu nastroju
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

                // Pole tekstowe dla oceny nastroju
                TextField(
                    value = state.moodRating.toString(),
                    onValueChange = { newValue ->
                        val rating = newValue.toIntOrNull() ?: 0
                        onEvent(MoodEvent.SetRating(rating))
                    },
                    label = { Text(text = "Ocena nastroju") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
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

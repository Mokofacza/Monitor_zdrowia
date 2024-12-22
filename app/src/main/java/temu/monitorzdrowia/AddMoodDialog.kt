package temu.monitorzdrowia

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

                // slider do wyboru oceny
                Slider(
                    value = state.moodRating.toFloat(),
                    onValueChange = { newValue ->
                        onEvent(MoodEvent.SetRating(newValue.toInt()))
                    },
                    valueRange = 1f..10f,
                    steps = 8, // Tworzy kroki dla wartości całkowitych 1-10
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ){
                // Wyświetlanie aktualnie wybranej wartości
                Text(
                    text = "Ocena: ${state.moodRating}",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )}
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

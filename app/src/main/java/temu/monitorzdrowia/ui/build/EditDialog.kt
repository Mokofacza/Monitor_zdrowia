package temu.monitorzdrowia.ui.build

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun EditDialog(
    state: ProfileState,
    onEvent: (ProfileEvent) -> Unit
) {
    val field = state.fieldBeingEdited ?: return

    AlertDialog(
        onDismissRequest = { onEvent(ProfileEvent.CancelEdit) },
        title = { Text("Edycja ${field.name}") },
        text = {
            when (field) {
                ProfileField.BirthDate -> {
                    DatePickerButton(
                        selectedDate = state.tempDate,
                        onDateSelected = { onEvent(ProfileEvent.ChangeEditDate(it)) }
                    )
                }
                else -> {
                    OutlinedTextField(
                        value = state.tempValue,
                        onValueChange = { onEvent(ProfileEvent.ChangeEditValue(it)) },
                        label = { Text(field.name) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onEvent(ProfileEvent.ConfirmEdit) },
                modifier = Modifier
                    .padding(end = 8.dp)
                    .defaultMinSize(minWidth = 80.dp)
            ) {
                Text("Zapisz")
            }
        },
        dismissButton = {
            Button(
                onClick = { onEvent(ProfileEvent.CancelEdit) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                modifier = Modifier
                    .padding(start = 8.dp)
                    .defaultMinSize(minWidth = 80.dp)
            ) {
                Text("Anuluj")
            }
        }
    )
}
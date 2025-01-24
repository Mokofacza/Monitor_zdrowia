package temu.monitorzdrowia.ui.build

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
            Column {
                when (field) {
                    ProfileField.BirthDate -> {
                        DatePickerButton(
                            selectedDate = state.tempDate,
                            onDateSelected = { onEvent(ProfileEvent.ChangeEditDate(it)) }
                        )
                    }
                    ProfileField.Sex -> {
                        SexDropdown(
                            selectedSex = state.tempValue,
                            onSexSelected = { selectedSex ->
                                onEvent(ProfileEvent.ChangeEditValue(selectedSex))
                            }
                        )
                    }
                    ProfileField.CitySize -> {
                        CitySizeDropdown(
                            selectedCitySize = state.tempValue,
                            onCitySizeSelected = { selectedCitySize ->
                                onEvent(ProfileEvent.ChangeEditValue(selectedCitySize))
                            }
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

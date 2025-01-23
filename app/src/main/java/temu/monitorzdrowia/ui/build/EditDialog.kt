package temu.monitorzdrowia.ui.build

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier


@Composable
fun EditDialog(
    state: ProfileState,
    onEvent: (ProfileEvent) -> Unit
) {
    val field = state.fieldBeingEdited ?: return

    AlertDialog(
        onDismissRequest = { onEvent(ProfileEvent.CancelEdit) },
        title = { Text("Edycja: ${fieldToString(field)}") },
        text = {
            when (field) {
                ProfileField.BirthDate -> {
                    // Wybór daty
                    DatePickerButton(
                        selectedDate = state.tempDate,
                        onDateSelected = { newDate ->
                            onEvent(ProfileEvent.ChangeEditDate(newDate))
                        }
                    )
                }
                ProfileField.Sex -> {
                    // Dropdown z płcią
                    SexDropdown(
                        selectedSex = state.tempValue,
                        onSexSelected = { sex ->
                            onEvent(ProfileEvent.ChangeEditValue(sex))
                        }
                    )
                }
                ProfileField.CitySize -> {
                    // Dropdown z wielkością aglomeracji
                    CitySizeDropdown(
                        selectedCitySize = state.tempValue,
                        onCitySizeSelected = { cs ->
                            onEvent(ProfileEvent.ChangeEditValue(cs))
                        }
                    )
                }
                else -> {
                    // Pozostałe pola – zwykły TextField
                    OutlinedTextField(
                        value = state.tempValue,
                        onValueChange = { newValue ->
                            onEvent(ProfileEvent.ChangeEditValue(newValue))
                        },
                        label = { Text("Nowa wartość") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onEvent(ProfileEvent.ConfirmEdit) }) {
                Text("Zapisz")
            }
        },
        dismissButton = {
            TextButton(onClick = { onEvent(ProfileEvent.CancelEdit) }) {
                Text("Anuluj")
            }
        }
    )
}

/**
 * Pomocnicza funkcja do konwersji ProfileField na string.
 */
fun fieldToString(field: ProfileField): String {
    return when (field) {
        ProfileField.Name -> "Imię"
        ProfileField.Subname -> "Nazwisko"
        ProfileField.BirthDate -> "Data Urodzenia"
        ProfileField.Sex -> "Płeć"
        ProfileField.Address -> "Adres"
        ProfileField.CitySize -> "Wielkość Aglomeracji"
    }
}
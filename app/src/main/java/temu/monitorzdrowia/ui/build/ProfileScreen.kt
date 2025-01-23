package temu.monitorzdrowia.ui.build

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    // 1. Dialog do tworzenia profilu (jeżeli user == null)
    if (state.isDialogVisible) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(ProfileEvent.HideFillDataDialog) },
            title = { Text("Uzupełnij dane użytkownika") },
            text = {
                Column {
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { viewModel.onEvent(ProfileEvent.SetName(it)) },
                        label = { Text("Imię") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.subname,
                        onValueChange = { viewModel.onEvent(ProfileEvent.SetSubName(it)) },
                        label = { Text("Nazwisko") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))

                    DatePickerButton(
                        selectedDate = state.birthDate,
                        onDateSelected = {
                            viewModel.onEvent(ProfileEvent.SetBirthDate(it))
                        }
                    )
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.address,
                        onValueChange = { viewModel.onEvent(ProfileEvent.SetAddress(it)) },
                        label = { Text("Adres") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))

                    SexDropdown(
                        selectedSex = state.sex,
                        onSexSelected = { sex ->
                            viewModel.onEvent(ProfileEvent.SetSex(sex))
                        }
                    )
                    Spacer(Modifier.height(8.dp))

                    CitySizeDropdown(
                        selectedCitySize = state.citySize,
                        onCitySizeSelected = { cs ->
                            viewModel.onEvent(ProfileEvent.SetCitySize(cs))
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(ProfileEvent.SaveUser) }) {
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

    // 2. Drugi dialog – edycja pojedynczego pola (StartEdit -> fieldBeingEdited)
    if (state.isEditDialogVisible) {
        EditDialog(
            state = state,
            onEvent = { viewModel.onEvent(it) }
        )
    }

    // 3. Profil – wyświetlamy, jeśli user != null, w Box z centrowaniem
    val user = state.user
    if (user != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            ProfileContent(
                user = user,
                age = state.age,
                onPickPhoto = { bytes -> viewModel.onEvent(ProfileEvent.UpdatePhoto(bytes)) },
                onEditField = { field -> viewModel.onEvent(ProfileEvent.StartEdit(field)) }
            )
        }
    } else {
        Text(
            text = "Brak użytkownika. Wypełnij dane w wyświetlonym oknie dialogowym.",
            modifier = Modifier.padding(16.dp)
        )
    }
}

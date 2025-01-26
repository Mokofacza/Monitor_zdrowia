package temu.monitorzdrowia.ui.build

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel
) {
    val state by profileViewModel.state.collectAsState()
    val context = LocalContext.current

    // Obserwowanie zdarzeń UI
    LaunchedEffect(Unit) {
        profileViewModel.uiEvent.collectLatest { event ->
            when (event) {
                is ProfileUiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Debugging: Log when dialog visibility changes
    LaunchedEffect(state.isDialogVisible) {
        Log.d("ProfileScreen", "Dialog is visible: ${state.isDialogVisible}")
    }

    // 1. Dialog do tworzenia profilu (jeżeli user == null i nie anulowano)
    if (state.isDialogVisible) {
        AlertDialog(
            onDismissRequest = { profileViewModel.onEvent(ProfileEvent.HideFillDataDialog) },
            title = { Text("Uzupełnij dane użytkownika") },
            text = {
                Column {
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { profileViewModel.onEvent(ProfileEvent.SetName(it)) },
                        label = { Text("Imię") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.subname,
                        onValueChange = { profileViewModel.onEvent(ProfileEvent.SetSubName(it)) },
                        label = { Text("Nazwisko") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))

                    DatePickerButton(
                        selectedDate = state.birthDate,
                        onDateSelected = { profileViewModel.onEvent(ProfileEvent.SetBirthDate(it)) }
                    )
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.address,
                        onValueChange = { profileViewModel.onEvent(ProfileEvent.SetAddress(it)) },
                        label = { Text("Adres") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))

                    SexDropdown(
                        selectedSex = state.sex,
                        onSexSelected = { sex -> profileViewModel.onEvent(ProfileEvent.SetSex(sex)) }
                    )
                    Spacer(Modifier.height(8.dp))

                    CitySizeDropdown(
                        selectedCitySize = state.citySize,
                        onCitySizeSelected = { cs -> profileViewModel.onEvent(ProfileEvent.SetCitySize(cs)) }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { profileViewModel.onEvent(ProfileEvent.SaveUser) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .defaultMinSize(minWidth = 80.dp)
                ) {
                    Text("Zapisz")
                }
            },
            dismissButton = {
                Button(
                    onClick = { profileViewModel.onEvent(ProfileEvent.HideFillDataDialog) },
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

    // 2. Drugi dialog – edycja pojedynczego pola (StartEdit -> fieldBeingEdited)
    if (state.isEditDialogVisible) {
        EditDialog(
            state = state,
            onEvent = { profileViewModel.onEvent(it) }
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
                onPickPhoto = { bytes -> profileViewModel.onEvent(ProfileEvent.UpdatePhoto(bytes)) },
                onEditField = { field -> profileViewModel.onEvent(ProfileEvent.StartEdit(field)) }
            )
        }
    } else if (!state.hasCancelled) {
        Text(
            text = "Brak użytkownika. Wypełnij dane w wyświetlonym oknie dialogowym.",
            modifier = Modifier.padding(16.dp)
        )
    }else if(state.hasCancelled) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Anulowano dodawanie użytkownika. Naciśnij przycisk by uruchomić okno dodawania użytkownika.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
            Button(onClick = { profileViewModel.onEvent(ProfileEvent.ReopenDialog) }) {
                Text("Dodaj profil")
            }
        }
    }
}

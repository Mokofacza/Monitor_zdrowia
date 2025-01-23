package temu.monitorzdrowia.ui.build

import android.app.DatePickerDialog
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import temu.monitorzdrowia.data.models.User
import java.time.LocalDate
import java.util.Calendar

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel()
) {
    val state = viewModel.state.collectAsState().value

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
    if (state.user != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            ProfileContent(
                user = state.user,
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

/**
 * Dialog do edycji wybranego pola (np. Imię, Nazwisko, Data, Płeć, Adres, CitySize).
 */
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
                        label = { Text("Nowa wartość") }
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
 * Przycisk do wybierania daty.
 */
@Composable
fun DatePickerButton(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    val context = LocalContext.current
    val label = selectedDate?.toString() ?: "Wybierz datę"

    val calendar = Calendar.getInstance()
    selectedDate?.let {
        calendar.set(it.year, it.monthValue - 1, it.dayOfMonth)
    }
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            val newDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
            onDateSelected(newDate)
        },
        year,
        month,
        dayOfMonth
    )

    Button(onClick = { datePickerDialog.show() }) {
        Text(label)
    }
}

/**
 * Dropdown do wyboru płci.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SexDropdown(
    selectedSex: String,
    onSexSelected: (String) -> Unit
) {
    val options = listOf("Mężczyzna", "Kobieta", "Inna")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = if (selectedSex.isBlank()) "Wybierz płeć" else selectedSex,
            onValueChange = {},
            readOnly = true,
            label = { Text("Płeć") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { sex ->
                DropdownMenuItem(
                    text = { Text(sex) },
                    onClick = {
                        onSexSelected(sex)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Dropdown do wyboru wielkości aglomeracji.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySizeDropdown(
    selectedCitySize: String,
    onCitySizeSelected: (String) -> Unit
) {
    val options = listOf("Wieś", "Małe miasto", "Średnie miasto", "Duże miasto", "Metropolia")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = if (selectedCitySize.isBlank()) "Wybierz aglomerację" else selectedCitySize,
            onValueChange = {},
            readOnly = true,
            label = { Text("Wielkość aglomeracji") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { cityOption ->
                DropdownMenuItem(
                    text = { Text(cityOption) },
                    onClick = {
                        onCitySizeSelected(cityOption)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Jedna linijka "tabeli" w profilu, z możliwością edycji (3 kropki) lub bez.
 */
@Composable
fun DataRow(
    label: String,
    value: String,
    onEditClick: (() -> Unit)? = null
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        // Etykieta pogrubiona
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(100.dp)
        )
        // Wartość
        Text(value)

        // Rozszerzający się odstęp – pcha kropki w prawo
        Spacer(modifier = Modifier.weight(1f))

        // 3 kropki – tylko jeśli onEditClick != null
        if (onEditClick != null) {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Edytuj") },
                    onClick = {
                        menuExpanded = false
                        onEditClick()
                    }
                )
            }
        }
    }
}

/**
 * Karta z danymi użytkownika (i ewentualnie wiekiem),
 * wyśrodkowana w Box (w ProfileScreen).
 */
@Composable
fun ProfileContent(
    user: User,
    age: Int?,
    onPickPhoto: (ByteArray) -> Unit,
    onEditField: (ProfileField) -> Unit
) {
    val context = LocalContext.current

    // Launcher do wybrania zdjęcia z galerii
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val bytes = inputStream.readBytes()
                onPickPhoto(bytes)
            }
        }
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            // Dodatkowe marginesy w Box
            .wrapContentSize()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally // Wyśrodkowanie zawartości
        ) {
            Text(
                text = "Profil Użytkownika",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Zdjęcie/Ikona
            if (user.photo != null) {
                val bitmap = remember(user.photo) {
                    BitmapFactory.decodeByteArray(user.photo, 0, user.photo.size)
                }
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Zdjęcie profilowe",
                        modifier = Modifier
                            .size(120.dp)
                            .clickable { pickImageLauncher.launch("image/*") }
                    )
                }
            } else {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = "Brak zdjęcia",
                    modifier = Modifier
                        .size(120.dp)
                        .clickable { pickImageLauncher.launch("image/*") }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Dane w "wierszach"
            DataRow(
                label = "Imię:",
                value = user.name,
                onEditClick = { onEditField(ProfileField.Name) }
            )
            DataRow(
                label = "Nazwisko:",
                value = user.subname,
                onEditClick = { onEditField(ProfileField.Subname) }
            )
            DataRow(
                label = "Data ur.:",
                value = user.birthDate.toString(),
                onEditClick = { onEditField(ProfileField.BirthDate) }
            )

            // Wiek – brak edycji
            if (age != null) {
                DataRow(
                    label = "Wiek:",
                    value = "$age lat"
                )
            }

            DataRow(
                label = "Adres:",
                value = user.address ?: "-",
                onEditClick = { onEditField(ProfileField.Address) }
            )
            DataRow(
                label = "Płeć:",
                value = user.sex ?: "-",
                onEditClick = { onEditField(ProfileField.Sex) }
            )
            DataRow(
                label = "Aglomeracja:",
                value = user.citySize ?: "-",
                onEditClick = { onEditField(ProfileField.CitySize) }
            )
        }
    }
}

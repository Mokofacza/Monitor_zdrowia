@file:Suppress("DEPRECATION")

package temu.monitorzdrowia.ui.build

import android.app.DatePickerDialog
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import temu.monitorzdrowia.data.models.User
import java.time.LocalDate
import java.util.Calendar

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel()
) {
    val state = viewModel.state.collectAsState().value

    // 1. Dialog do pierwszego tworzenia profilu
    if (state.isDialogVisible) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(ProfileEvent.HideFillDataDialog) },
            title = { Text("Uzupełnij dane użytkownika") },
            text = {
                Column {
                    // Imię
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { viewModel.onEvent(ProfileEvent.SetName(it)) },
                        label = { Text("Imię") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))

                    // Nazwisko
                    OutlinedTextField(
                        value = state.subname,
                        onValueChange = { viewModel.onEvent(ProfileEvent.SetSubName(it)) },
                        label = { Text("Nazwisko") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))

                    // Data urodzenia
                    DatePickerButton(
                        selectedDate = state.birthDate,
                        onDateSelected = {
                            viewModel.onEvent(ProfileEvent.SetBirthDate(it))
                        }
                    )
                    Spacer(Modifier.height(8.dp))

                    // Adres
                    OutlinedTextField(
                        value = state.address,
                        onValueChange = { viewModel.onEvent(ProfileEvent.SetAddress(it)) },
                        label = { Text("Adres") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))

                    // Płeć
                    SexDropdown(
                        selectedSex = state.sex,
                        onSexSelected = { sex ->
                            viewModel.onEvent(ProfileEvent.SetSex(sex))
                        }
                    )
                    Spacer(Modifier.height(8.dp))

                    // Wielkość aglomeracji
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

    // 2. Dialog – edycja pojedynczego pola
    if (state.isEditDialogVisible) {
        EditDialog(
            state = state,
            onEvent = { viewModel.onEvent(it) }
        )
    }

    // 3. Profil – jeśli user != null, centrowany w Box
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
        // Gdy brak usera
        Text(
            text = "Brak użytkownika. Wypełnij dane w wyświetlonym oknie dialogowym.",
            modifier = Modifier.padding(16.dp)
        )
    }
}

/**
 * Drugi dialog: Edycja wybranego pola (np. Imię, Nazwisko, Data, Płeć).
 */
@Composable
fun EditDialog(
    state: ProfileState,
    onEvent: (ProfileEvent) -> Unit
) {
    val field = state.fieldBeingEdited ?: return // Bez tego nie ma co edytować

    AlertDialog(
        onDismissRequest = { onEvent(ProfileEvent.CancelEdit) },
        title = { Text("Edycja: ${fieldToString(field)}") },
        text = {
            when (field) {
                ProfileField.BirthDate -> {
                    DatePickerButton(
                        selectedDate = state.tempDate,
                        onDateSelected = { date -> onEvent(ProfileEvent.ChangeEditDate(date)) }
                    )
                }
                ProfileField.Sex -> {
                    SexDropdown(
                        selectedSex = state.tempValue,
                        onSexSelected = { sex -> onEvent(ProfileEvent.ChangeEditValue(sex)) }
                    )
                }
                ProfileField.CitySize -> {
                    CitySizeDropdown(
                        selectedCitySize = state.tempValue,
                        onCitySizeSelected = { cs -> onEvent(ProfileEvent.ChangeEditValue(cs)) }
                    )
                }
                else -> {
                    OutlinedTextField(
                        value = state.tempValue,
                        onValueChange = { newVal -> onEvent(ProfileEvent.ChangeEditValue(newVal)) },
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
 * Przycisk – otwiera natywny DatePickerDialog do wybrania daty.
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
        { _, y, m, d ->
            val newDate = LocalDate.of(y, m + 1, d)
            onDateSelected(newDate)
        },
        year, month, dayOfMonth
    )

    Button(onClick = { datePickerDialog.show() }) {
        Text(label)
    }
}

/**
 * Dropdown do wyboru płci – używamy nowszej wersji menuAnchor(...).
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
            value = selectedSex.ifBlank { "Wybierz płeć" },
            onValueChange = {},
            readOnly = true,
            label = { Text("Płeć") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                // UŻYWAMY NOWEGO PRZECIĄŻENIA:
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
 * Dropdown do wyboru wielkości aglomeracji – też z menuAnchor(...).
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
            value = selectedCitySize.ifBlank { "Wybierz aglomerację" },
            onValueChange = {},
            readOnly = true,
            label = { Text("Wielkość aglomeracji") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier.run {
                        menuAnchor()
                        .fillMaxWidth()
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
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
 * Wiersz z danymi w profilu.
 * Jeżeli onEditClick != null, pokazujemy 3 kropki -> "Edytuj".
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
        // Etykieta (pogrubiona)
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(110.dp)
        )
        // Wartość
        Text(value)

        Spacer(modifier = Modifier.weight(1f))

        // 3 kropki
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
 * Wyświetlenie profilu – karta w centrum ekranu (Box).
 * Kliknięcie w zdjęcie -> galeria.
 * onEditField(...) -> "Edytuj" jednego pola.
 */
@Composable
fun ProfileContent(
    user: User,
    age: Int?,
    onPickPhoto: (ByteArray) -> Unit,
    onEditField: (ProfileField) -> Unit
) {
    val context = LocalContext.current

    // Launcher do wybierania zdjęcia
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
        modifier = Modifier.wrapContentSize() // w Box jest centrowane
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally // wyśrodkuj wewnątrz karty
        ) {
            Text(
                text = "Profil Użytkownika",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Zdjęcie / Ikona
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
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Brak zdjęcia",
                    modifier = Modifier
                        .size(120.dp)
                        .clickable { pickImageLauncher.launch("image/*") }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Imię
            DataRow(
                label = "Imię:",
                value = user.name,
                onEditClick = { onEditField(ProfileField.Name) }
            )
            // Nazwisko
            DataRow(
                label = "Nazwisko:",
                value = user.subname,
                onEditClick = { onEditField(ProfileField.Subname) }
            )
            // Data
            DataRow(
                label = "Data ur.:",
                value = user.birthDate.toString(),
                onEditClick = { onEditField(ProfileField.BirthDate) }
            )
            // Wiek (bez edycji)
            if (age != null) {
                DataRow(
                    label = "Wiek:",
                    value = "$age lat"
                )
            }
            // Adres
            DataRow(
                label = "Adres:",
                value = user.address ?: "-",
                onEditClick = { onEditField(ProfileField.Address) }
            )
            // Płeć
            DataRow(
                label = "Płeć:",
                value = user.sex ?: "-",
                onEditClick = { onEditField(ProfileField.Sex) }
            )
            // Aglomeracja
            DataRow(
                label = "Aglomeracja:",
                value = user.citySize ?: "-",
                onEditClick = { onEditField(ProfileField.CitySize) }
            )
        }
    }
}

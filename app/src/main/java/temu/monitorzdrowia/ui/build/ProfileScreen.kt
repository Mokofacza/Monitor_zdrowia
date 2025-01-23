package temu.monitorzdrowia.ui.build

import android.app.DatePickerDialog
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.ai.client.generativeai.GenerativeModel
import java.time.LocalDate
import java.util.Calendar
import temu.monitorzdrowia.data.models.User
import androidx.compose.runtime.getValue
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel
) {
    val state = viewModel.state.collectAsState().value

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
                    Spacer(modifier = Modifier.height(8.dp))

                    // Nazwisko
                    OutlinedTextField(
                        value = state.subname,
                        onValueChange = { viewModel.onEvent(ProfileEvent.SetSubName(it)) },
                        label = { Text("Nazwisko") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Data urodzenia (dynamiczny przycisk)
                    DatePickerButton(
                        selectedDate = state.birthDate,
                        onDateSelected = {
                            viewModel.onEvent(ProfileEvent.SetBirthDate(it))
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Adres
                    OutlinedTextField(
                        value = state.address,
                        onValueChange = { viewModel.onEvent(ProfileEvent.SetAddress(it)) },
                        label = { Text("Adres") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Dropdown dla Płci
                    SexDropdown(
                        selectedSex = state.sex,
                        onSexSelected = { viewModel.onEvent(ProfileEvent.SetSex(it)) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Dropdown dla Wielkości aglomeracji
                    CitySizeDropdown(
                        selectedCitySize = state.citySize,
                        onCitySizeSelected = { viewModel.onEvent(ProfileEvent.SetCitySize(it)) }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.onEvent(ProfileEvent.SaveUser) }
                ) {
                    Text("Zapisz")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.onEvent(ProfileEvent.HideFillDataDialog)
                    }
                ) {
                    Text("Anuluj")
                }
            }
        )
    }

    // Gdy user istnieje, wyświetlamy główny profil
    if (state.user != null) {
        // Całość wyśrodkowana pionowo i poziomo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            ProfileContent(
                user = state.user,
                age = state.age,
                onPickPhoto = { bytes ->
                    viewModel.onEvent(ProfileEvent.UpdatePhoto(bytes))
                }
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
 * Przycisk do wybierania daty (DatePickerDialog).
 * Po wybraniu, napis przycisku zmienia się na `YYYY-MM-DD`.
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
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            val newDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDayOfMonth)
            onDateSelected(newDate)
        },
        year,
        month,
        day
    )

    Button(onClick = { datePickerDialog.show() }) {
        Text(label)
    }
}

/**
 * Dropdown do wyboru płci (jednokrotnego wyboru).
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
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { sexOption ->
                DropdownMenuItem(
                    text = { Text(sexOption) },
                    onClick = {
                        onSexSelected(sexOption)
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
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { citySizeOption ->
                DropdownMenuItem(
                    text = { Text(citySizeOption) },
                    onClick = {
                        onCitySizeSelected(citySizeOption)
                        expanded = false
                    }
                )
            }
        }
    }
}


/**
 * Główna zawartość profilu:
 * - Zdjęcie/Ikona (klikane do zmiany)
 * - Tabelaryczne wyświetlenie danych (pogrubiona etykieta, zwykły tekst dla wartości)
 */
@Composable
fun ProfileContent(
    user: User,
    age: Int?,
    onPickPhoto: (ByteArray) -> Unit
) {
    val context = LocalContext.current
    // Launcher do wyboru zdjęcia z galerii
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val bytes = inputStream.readBytes()
                    onPickPhoto(bytes)
                }
            }
        }
    )

    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .wrapContentSize()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .widthIn(min = 300.dp), // minimalna szerokość
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Profil Użytkownika",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Klikalne zdjęcie lub ikona
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

            // TABELARYCZNA PREZENTACJA – etykieta pogrubiona, wartość normalna
            DataRow(label = "Imię:", value = user.name)
            DataRow(label = "Nazwisko:", value = user.subname)
            DataRow(label = "Data ur.:", value = user.birthDate.toString())

            if (age != null) {
                DataRow(label = "Wiek:", value = "$age lat")
            }
            DataRow(label = "Adres:", value = user.address ?: "-")
            DataRow(label = "Płeć:", value = user.sex ?: "-")
            DataRow(label = "Aglomeracja:", value = user.citySize ?: "-")
        }
    }
}

/**
 * Pojedynczy wiersz "tabeli": etykieta (pogrubiona) i wartość (zwykła).
 */
@Composable
fun DataRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(110.dp) // stała szerokość etykiety
        )
        Text(text = value)
    }
}
package temu.monitorzdrowia.ui.build

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier


@Composable
fun SexDropdown(
    selectedSex: String,
    onSexSelected: (String) -> Unit
) {
    val options = listOf("Mężczyzna", "Kobieta", "Inna")
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(if (selectedSex.isBlank()) "Wybierz płeć" else selectedSex) }

    Column {
        OutlinedTextField(
            value = selectedOptionText,
            onValueChange = {},
            readOnly = true,
            label = { Text("Płeć") },
            trailingIcon = {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu", modifier = Modifier.clickable { expanded = true })
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { sex ->
                DropdownMenuItem(
                    text = { Text(sex) },
                    onClick = {
                        selectedOptionText = sex
                        onSexSelected(sex)
                        expanded = false
                    }
                )
            }
        }
    }
}
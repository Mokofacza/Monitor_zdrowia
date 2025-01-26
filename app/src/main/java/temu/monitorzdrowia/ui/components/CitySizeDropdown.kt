package temu.monitorzdrowia.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun CitySizeDropdown(
    selectedCitySize: String,
    onCitySizeSelected: (String) -> Unit
) {
    val options = listOf("Wieś", "Małe miasto", "Średnie miasto", "Duże miasto", "Metropolia")
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(if (selectedCitySize.isBlank()) "Wybierz aglomerację" else selectedCitySize) }

    Column {
        OutlinedTextField(
            value = selectedOptionText,
            onValueChange = {},
            readOnly = true,
            label = { Text("Wielkość Aglomeracji") },
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { cityOption ->
                DropdownMenuItem(
                    text = { Text(cityOption) },
                    onClick = {
                        selectedOptionText = cityOption
                        onCitySizeSelected(cityOption)
                        expanded = false
                    }
                )
            }
        }
    }
}
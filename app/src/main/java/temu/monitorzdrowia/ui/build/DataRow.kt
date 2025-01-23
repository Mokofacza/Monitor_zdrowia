package temu.monitorzdrowia.ui.build

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DataRow(
    label: String,
    value: String,
    onEditClick: (() -> Unit)? = null
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
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

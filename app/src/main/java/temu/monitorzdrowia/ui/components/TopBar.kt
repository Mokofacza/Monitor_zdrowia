// TopBar.kt
package temu.monitorzdrowia.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import temu.monitorzdrowia.navigation.NavRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController) {
    TopAppBar(
        title = { Text("Monitor Zdrowia") },
        actions = {
            IconButton(onClick = { navController.navigate(NavRoutes.Mood.route) }) {
                Icon(imageVector = Icons.Default.Home, contentDescription = "Mood")
            }
            IconButton(onClick = { navController.navigate(NavRoutes.Profile.route) }) {
                Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Profile")
            }
        }
    )
}

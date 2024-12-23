// TopBar.kt
package temu.monitorzdrowia.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import temu.monitorzdrowia.navigation.NavRoutes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController) {
    TopAppBar(
        title = { Text("Monitor Zdrowia") },
        actions = {
            IconButton(onClick = { navController.navigate(NavRoutes.Mood.route) }) {
                Icon(imageVector = Icons.Filled.Home, contentDescription = "Mood")
            }
            IconButton(onClick = { navController.navigate(NavRoutes.Profile.route) }) {
                Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = "Profile")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

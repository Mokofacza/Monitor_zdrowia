// TopBar.kt
package temu.monitorzdrowia.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import temu.monitorzdrowia.navigation.NavRoutes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController) {
    // Obserwujemy bieżącą trasę
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    TopAppBar(
        title = { Text("Monitor Zdrowia") },
        actions = {
            // Ikona Home
            IconButton(
                onClick = {
                    navController.navigate(NavRoutes.Mood.route) {
                        // Unikaj duplikowania trasy w back stack
                        launchSingleTop = true
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        restoreState = true
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Mood",
                    tint = if (currentRoute == NavRoutes.Mood.route) MaterialTheme.colorScheme.onPrimary else Color.Gray
                )
            }

            // Ikona Profile
            IconButton(
                onClick = {
                    navController.navigate(NavRoutes.Profile.route) {
                        // Unikaj duplikowania trasy w back stack
                        launchSingleTop = true
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        restoreState = true
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Profile",
                    tint = if (currentRoute == NavRoutes.Profile.route) MaterialTheme.colorScheme.onPrimary else Color.Gray
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

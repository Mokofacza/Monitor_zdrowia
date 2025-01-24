// NavGraph.kt
package temu.monitorzdrowia.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import temu.monitorzdrowia.ui.build.MoodScreen
import temu.monitorzdrowia.ui.build.MoodViewModel
import temu.monitorzdrowia.ui.build.ProfileScreen
import temu.monitorzdrowia.ui.build.ProfileViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: MoodViewModel,   // Mood
    profileViewModel: ProfileViewModel // Profile
) {
    NavHost(navController = navController, startDestination = NavRoutes.Mood.route) {
        composable(NavRoutes.Mood.route) {
            MoodScreen(
                state = viewModel.state.collectAsState().value,
                onEvent = viewModel::onEvent ,
                viewModel = viewModel
            )
        }
        composable(NavRoutes.Profile.route) {
            ProfileScreen(profileViewModel)
        }
    }
}

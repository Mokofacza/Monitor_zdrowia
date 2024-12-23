package temu.monitorzdrowia.navigation

sealed class NavRoutes(val route: String) {
    object Mood : NavRoutes("mood")
    object Profile : NavRoutes("profile")
}

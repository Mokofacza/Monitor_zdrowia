package temu.monitorzdrowia.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Dgreen1, //Topbar -- nie zmieniać
    onPrimary = White,
    primaryContainer = Dgreen1, // guzik dodawania  -- nie zmieniać
    onPrimaryContainer = White,
    secondary = Dgreen2, //tło paska do sortowania
    onSecondary = Black,
    secondaryContainer = Dgrey, //od okienek z zapisanymi moodami  -- nie zmieniać
    onSecondaryContainer = White,
    background = Black,
    onBackground = White,
    surface = Black,
    onSurface = White,
    error = Error,
    onError = White,
)

private val LightColorScheme = lightColorScheme(
    primary = Green1, //Topbar -- nie zmieniać
    onPrimary = White,
    primaryContainer = Green1, // guzik dodawania  -- nie zmieniać
    onPrimaryContainer = White,
    secondary = Green2, //tło paska do sortowania
    onSecondary = Black,
    secondaryContainer = Offwhite, //od okienek z zapisanymi moodami  -- nie zmieniać
    onSecondaryContainer = White,
    background = White,
    onBackground = White,
    surface = White,
    onSurface = Black,
    error = Error,
    onError = White
)

@Composable
fun MonitorZdrowiaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Upewnij się, że Typography jest z Material3 (czyli najczęściej również z temu samym pakietem)
        content = content
    )
}

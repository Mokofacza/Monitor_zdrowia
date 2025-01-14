package temu.monitorzdrowia.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Navy, //Topbar -- nie zmieniać
    onPrimary = White,
    primaryContainer = Navy, // guzik dodawania  -- nie zmieniać
    onPrimaryContainer = White,
    secondary = Puple,
    onSecondary = Black,
    secondaryContainer = Puple, //od okienek z zapisanymi moodami  -- nie zmieniać
    onSecondaryContainer = White,
    background = Black,
    onBackground = White,
    surface = Black,
    onSurface = White,
    error = Color(0xFFB00020),
    onError = White
)

private val LightColorScheme = lightColorScheme(
    primary = Purple500, //Topbar -- nie zmieniać
    onPrimary = White,
    primaryContainer = Purple500, // guzik dodawania  -- nie zmieniać
    onPrimaryContainer = White,
    secondary = Teal200,
    onSecondary = Black,
    secondaryContainer = Purple200, //od okienek z zapisanymi moodami  -- nie zmieniać
    onSecondaryContainer = White,
    background = White,
    onBackground = Black,
    surface = White,
    onSurface = Black,
    error = Color(0xFFB00020),
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

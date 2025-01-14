// temu/monitorzdrowia/ui/theme/Theme.kt
package temu.monitorzdrowia.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Upewnij się, że kolory z Color.kt są zaimportowane
// import temu.monitorzdrowia.ui.theme.*

private val DarkColorScheme = darkColorScheme(
    primary = Navy,
    onPrimary = White,
    primaryContainer = Puple,
    onPrimaryContainer = White,
    secondary = KijWie,
    onSecondary = Black,
    secondaryContainer = KijWie, // lub inny odpowiedni kolor
    onSecondaryContainer = White,
    background = Black,
    onBackground = White,
    surface = Black,
    onSurface = White,
    error = Color(0xFFB00020),
    onError = White
)

private val LightColorScheme = lightColorScheme(
    primary = Purple500,
    onPrimary = White,
    primaryContainer = Purple700,
    onPrimaryContainer = White,
    secondary = Teal200,
    onSecondary = Black,
    secondaryContainer = Teal200, // dostosuj według potrzeb
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

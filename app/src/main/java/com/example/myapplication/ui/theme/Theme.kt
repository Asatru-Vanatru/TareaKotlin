package com.example.myapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Tema de la aplicacion.
 *
 * Los colores se definen de forma explicita en lugar de usar color dinamico,
 * para que el contraste entre el fondo y el texto sea siempre el mismo, en
 * cualquier telefono y tanto en modo claro como oscuro.
 *
 * En Material 3 cada color de fondo tiene su pareja "on-": lo que se dibuja
 * encima usa ese color. Por ejemplo, el texto sobre "surface" se pinta con
 * "onSurface". Si esa pareja no esta bien definida, el texto puede terminar
 * del mismo color que el fondo y volverse invisible.
 */

private val ColoresClaros = lightColorScheme(
    primary = Color(0xFF00629E),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFCFE5FF),
    onPrimaryContainer = Color(0xFF001D33),

    secondary = Color(0xFF51606F),
    onSecondary = Color(0xFFFFFFFF),

    background = Color(0xFFFCFCFF),
    onBackground = Color(0xFF1A1C1E),

    surface = Color(0xFFFCFCFF),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFDEE3EB),
    onSurfaceVariant = Color(0xFF42474E),

    outline = Color(0xFF72777F),

    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF)
)

private val ColoresOscuros = darkColorScheme(
    primary = Color(0xFF9CCAFF),
    onPrimary = Color(0xFF003355),
    primaryContainer = Color(0xFF004A79),
    onPrimaryContainer = Color(0xFFCFE5FF),

    secondary = Color(0xFFB9C8DA),
    onSecondary = Color(0xFF233240),

    background = Color(0xFF1A1C1E),
    onBackground = Color(0xFFE2E2E6),

    surface = Color(0xFF1A1C1E),
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = Color(0xFF42474E),
    onSurfaceVariant = Color(0xFFC2C7CF),

    outline = Color(0xFF8C9199),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) ColoresOscuros else ColoresClaros,
        typography = Typography,
        content = content
    )
}

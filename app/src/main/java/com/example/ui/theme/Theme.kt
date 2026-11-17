package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = HealthGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = HealthGreenContainer,
    onPrimaryContainer = HealthGreenDark,
    secondary = GeruaPrimary,
    onSecondary = Color.White,
    secondaryContainer = GeruaContainer,
    onSecondaryContainer = GeruaDeep,
    tertiary = GeruaGold,
    onTertiary = Color.White,
    background = BackgroundLight,
    onBackground = TextDark,
    surface = Color.White,
    onSurface = TextDark,
    surfaceVariant = Color(0xFFE8EFEA),
    onSurfaceVariant = TextMuted,
    outline = Color(0xFFBDC9C1),
    error = BloodRed,
    onError = Color.White,
    errorContainer = BloodRedContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = HealthGreenLight,
    onPrimary = HealthGreenDark,
    primaryContainer = HealthGreenDark,
    onPrimaryContainer = HealthGreenContainer,
    secondary = GeruaBright,
    onSecondary = GeruaDeep,
    secondaryContainer = GeruaDeep,
    onSecondaryContainer = GeruaContainer,
    tertiary = GeruaGold,
    onTertiary = Color.Black,
    background = BackgroundDark,
    onBackground = TextLight,
    surface = SurfaceDark,
    onSurface = TextLight,
    surfaceVariant = Color(0xFF24332B),
    onSurfaceVariant = Color(0xFFA6B7AC),
    outline = Color(0xFF3F5548),
    error = BloodRed,
    onError = Color.White,
    errorContainer = Color(0xFF450A0A)
)

@Composable
fun HealthPointTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val KoolDarkColorScheme = darkColorScheme(
    primary = KoolCyan,
    onPrimary = Color(0xFF00363D),
    primaryContainer = KoolSurfaceElevated,
    onPrimaryContainer = KoolCyan,
    secondary = KoolBlue,
    onSecondary = Color.White,
    secondaryContainer = KoolSurfaceCard,
    onSecondaryContainer = Color(0xFFD6E4FF),
    tertiary = KoolGreen,
    onTertiary = Color(0xFF00391A),
    background = KoolBgDark,
    onBackground = KoolTextPrimary,
    surface = KoolSurfaceDark,
    onSurface = KoolTextPrimary,
    surfaceVariant = KoolSurfaceElevated,
    onSurfaceVariant = KoolTextSecondary,
    outline = KoolBorder,
    outlineVariant = KoolBorderGlow
)

private val KoolLightColorScheme = lightColorScheme(
    primary = KoolCyanDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F7FA),
    onPrimaryContainer = Color(0xFF004D40),
    secondary = KoolBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE3EDFD),
    onSecondaryContainer = Color(0xFF102A43),
    tertiary = Color(0xFF00796B),
    onTertiary = Color.White,
    background = Color(0xFFF1F5F9),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFF94A3B8)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek tech dark mode for INKO Launcher
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) KoolDarkColorScheme else KoolLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = MyraCyan,
    onPrimary = MyraDarkBg,
    primaryContainer = MyraBlue,
    onPrimaryContainer = MyraTextPrimary,
    secondary = MyraPurpleLight,
    onSecondary = MyraTextPrimary,
    secondaryContainer = MyraPurple,
    onSecondaryContainer = MyraTextPrimary,
    tertiary = MyraPink,
    onTertiary = MyraTextPrimary,
    background = MyraDarkBg,
    onBackground = MyraTextPrimary,
    surface = MyraCardBg,
    onSurface = MyraTextPrimary,
    surfaceVariant = MyraCardSurface,
    onSurfaceVariant = MyraTextSecondary,
    outline = MyraCardBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent futuristic theme matching screenshot
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = MyraDarkBg.toArgb()
                window.navigationBarColor = MyraDarkBg.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

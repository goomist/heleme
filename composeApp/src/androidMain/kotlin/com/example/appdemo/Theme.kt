package com.example.appdemo

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFFD4956A),
            onPrimary = Color(0xFF3D1A00),
            primaryContainer = Color(0xFF5C3520),
            onPrimaryContainer = Color(0xFFF3DFB0),
            secondary = Color(0xFFC1926B),
            onSecondary = Color(0xFF3D1A00),
            background = Color(0xFF1A1208),
            surface = Color(0xFF241A10),
            surfaceVariant = Color(0xFF3A2A1A),
            onBackground = Color(0xFFF5E6CC),
            onSurface = Color(0xFFF5E6CC),
            onSurfaceVariant = Color(0xFFD4B896),
        )
    } else {
        lightColorScheme(
            primary = Color(0xFFB07240),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFF3DFB0),
            onPrimaryContainer = Color(0xFF3D2000),
            secondary = Color(0xFFC1926B),
            onSecondary = Color.White,
            background = Color(0xFFFAF3EA),
            surface = Color(0xFFFFF9F0),
            surfaceVariant = Color(0xFFF0E0CC),
            onBackground = Color(0xFF2D1A00),
            onSurface = Color(0xFF2D1A00),
            onSurfaceVariant = Color(0xFF7A5236),
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content,
    )
}

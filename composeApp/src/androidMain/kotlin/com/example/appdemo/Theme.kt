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

// Every role is spelled out on purpose: any role left to the default lightColorScheme()/
// darkColorScheme() falls back to Material's purple baseline, which is where NavigationBar
// (surfaceContainer) and its selected pill (secondaryContainer) pick their colors from.
val MilkTeaLightColorScheme = lightColorScheme(
    primary = Color(0xFFB07240),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF3DFB0),
    onPrimaryContainer = Color(0xFF3D2000),
    inversePrimary = Color(0xFFD4956A),
    secondary = Color(0xFFC1926B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3DFC4),
    onSecondaryContainer = Color(0xFF4A2B12),
    tertiary = Color(0xFF8C6D46),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFEFE0C8),
    onTertiaryContainer = Color(0xFF3A2A14),
    background = Color(0xFFFAF3EA),
    onBackground = Color(0xFF2D1A00),
    surface = Color(0xFFFFF9F0),
    onSurface = Color(0xFF2D1A00),
    surfaceVariant = Color(0xFFF0E0CC),
    onSurfaceVariant = Color(0xFF7A5236),
    surfaceTint = Color(0xFFB07240),
    surfaceBright = Color(0xFFFFF9F0),
    surfaceDim = Color(0xFFE7D9C6),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFDF6EC),
    surfaceContainer = Color(0xFFF8EEE0),
    surfaceContainerHigh = Color(0xFFF2E6D5),
    surfaceContainerHighest = Color(0xFFEBDDC9),
    inverseSurface = Color(0xFF352A1E),
    inverseOnSurface = Color(0xFFFAF0E2),
    outline = Color(0xFFA1856A),
    outlineVariant = Color(0xFFDCC8AE),
    scrim = Color.Black,
)

val MilkTeaDarkColorScheme = darkColorScheme(
    primary = Color(0xFFD4956A),
    onPrimary = Color(0xFF3D1A00),
    primaryContainer = Color(0xFF5C3520),
    onPrimaryContainer = Color(0xFFF3DFB0),
    inversePrimary = Color(0xFFB07240),
    secondary = Color(0xFFC1926B),
    onSecondary = Color(0xFF3D1A00),
    secondaryContainer = Color(0xFF4A3221),
    onSecondaryContainer = Color(0xFFF3DFB0),
    tertiary = Color(0xFFD8BC93),
    onTertiary = Color(0xFF3A2A14),
    tertiaryContainer = Color(0xFF52412B),
    onTertiaryContainer = Color(0xFFF3E4C8),
    background = Color(0xFF1A1208),
    onBackground = Color(0xFFF5E6CC),
    surface = Color(0xFF241A10),
    onSurface = Color(0xFFF5E6CC),
    surfaceVariant = Color(0xFF3A2A1A),
    onSurfaceVariant = Color(0xFFD4B896),
    surfaceTint = Color(0xFFD4956A),
    surfaceBright = Color(0xFF413225),
    surfaceDim = Color(0xFF1A1208),
    surfaceContainerLowest = Color(0xFF120C05),
    surfaceContainerLow = Color(0xFF1F160C),
    surfaceContainer = Color(0xFF261C11),
    surfaceContainerHigh = Color(0xFF312418),
    surfaceContainerHighest = Color(0xFF3D2E1F),
    inverseSurface = Color(0xFFF5E6CC),
    inverseOnSurface = Color(0xFF3A2A14),
    outline = Color(0xFF9C8467),
    outlineVariant = Color(0xFF4E3D2A),
    scrim = Color.Black,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) MilkTeaDarkColorScheme else MilkTeaLightColorScheme

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

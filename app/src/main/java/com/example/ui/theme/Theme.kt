package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4DD0E1),
    onPrimary = Color(0xFF00363D),
    primaryContainer = Color(0xFF004F58),
    onPrimaryContainer = Color(0xFF97F0FF),
    secondary = Color(0xFF81D4FA),
    onSecondary = Color(0xFF00344F),
    secondaryContainer = Color(0xFF004B70),
    onSecondaryContainer = Color(0xFFC7E7FF),
    tertiary = WhatsAppGreen,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onBackground = Color(0xFFE2E2E6),
    onSurface = Color(0xFFE2E2E6),
    outline = OutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = EnterprisePrimary,
    onPrimary = EnterpriseOnPrimary,
    primaryContainer = EnterprisePrimaryContainer,
    onPrimaryContainer = EnterpriseOnPrimaryContainer,
    secondary = EnterpriseSecondary,
    onSecondary = EnterpriseOnSecondary,
    secondaryContainer = EnterpriseSecondaryContainer,
    onSecondaryContainer = EnterpriseOnSecondaryContainer,
    tertiary = EnterpriseTertiary,
    onTertiary = EnterpriseOnTertiary,
    tertiaryContainer = EnterpriseTertiaryContainer,
    onTertiaryContainer = EnterpriseOnTertiaryContainer,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onBackground = Color(0xFF191C1E),
    onSurface = Color(0xFF191C1E),
    outline = OutlineLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent enterprise palette
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

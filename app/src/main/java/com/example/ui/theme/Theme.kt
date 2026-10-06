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

private val LiquidGrassLightColorScheme = lightColorScheme(
    primary = LiquidGrassPrimary,
    onPrimary = TextPureWhite,
    primaryContainer = LiquidGrassPale,
    onPrimaryContainer = TextForestDeep,
    secondary = LiquidGrassDeep,
    onSecondary = TextPureWhite,
    secondaryContainer = Color(0x18059669),
    onSecondaryContainer = TextForestDeep,
    tertiary = LiquidGrassMint,
    onTertiary = TextForestDeep,
    background = LiquidGrassAura,
    onBackground = TextForestDeep,
    surface = GlassWhite,
    onSurface = TextForestDeep,
    surfaceVariant = Color(0x12059669),
    onSurfaceVariant = TextForestMuted,
    outline = GlassCardBorder,
    error = ErrorCoral,
    errorContainer = ErrorCoralContainer
)

private val LiquidGrassDarkColorScheme = darkColorScheme(
    primary = LiquidGrassBright,
    onPrimary = Color(0xFF022C22),
    primaryContainer = LiquidGrassDeep,
    onPrimaryContainer = LiquidGrassPale,
    secondary = LiquidGrassMint,
    onSecondary = Color(0xFF022C22),
    background = Color(0xFF062E20),
    onBackground = Color(0xFFF0FDF4),
    surface = Color(0xFF0F3E2E),
    onSurface = Color(0xFFF0FDF4),
    outline = Color(0x4034D399)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep bespoke Liquid Grass brand aesthetics
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> LiquidGrassDarkColorScheme
        else -> LiquidGrassLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

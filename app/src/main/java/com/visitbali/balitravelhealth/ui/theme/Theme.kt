package com.visitbali.balitravelhealth.ui.theme

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager

private val DarkColorScheme = darkColorScheme(
    primary = Red80,
    onPrimary = androidx.compose.ui.graphics.Color(0xFF173B36),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF315B55),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFD4ECE7),
    secondary = Maroon80,
    tertiary = Rose80,
    background = ComfortDarkBackground,
    onBackground = androidx.compose.ui.graphics.Color(0xFFE2E8E4),
    surface = ComfortDarkSurface,
    onSurface = androidx.compose.ui.graphics.Color(0xFFE2E8E4),
    surfaceContainer = androidx.compose.ui.graphics.Color(0xFF202B29),
    surfaceVariant = ComfortDarkSurfaceVariant,
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFC4CCC8),
    outline = androidx.compose.ui.graphics.Color(0xFF8E9994),
)

private val LightColorScheme = lightColorScheme(
    primary = Red40,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = BthRedLight,
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF173B36),
    secondary = Maroon40,
    tertiary = Rose40,
    background = BthBackground,
    onBackground = androidx.compose.ui.graphics.Color(0xFF211A18),
    surface = BthSurface,
    onSurface = androidx.compose.ui.graphics.Color(0xFF211A18),
    surfaceContainer = BthSurfaceVariant,
    surfaceVariant = BthSurfaceVariant,
    outline = BthOutline,
)

@Composable
fun BaliTravelHealthTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val focusManager = LocalFocusManager.current

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
        ) {
            content()
        }
    }
}

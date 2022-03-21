package com.r.dosc.domain.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = Salem,
    primaryVariant = Dark_2,
    secondary = Ocean_Green,
    onPrimary = TextWhite

)

private val LightColorPalette = lightColors(
    primary = Ocean_Green,
    primaryVariant = Salem,
    secondary = Desert_Storm,
    onPrimary = Main_Text_Color
)

@Composable
fun DoscTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
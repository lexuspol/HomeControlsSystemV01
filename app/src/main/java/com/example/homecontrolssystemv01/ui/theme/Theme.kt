package com.example.homecontrolssystemv01.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
//    primary = Purple200,
//    primaryVariant = Purple700,
//    secondary = Teal200,
//    secondaryVariant = Teal300
primary = Color(0xff3f51b5),
primaryVariant = Color(0xFF3700B3),
secondary = Color(0xff7c4dff),
secondaryVariant = Color(0xff7c4dff),
   background = Color.DarkGray,
    //surface = Color.DarkGray,
//background = Color(0xFF121212),
surface = Color(0xFF121212),
error = Color(0xFFB00020),
onPrimary = Color.Black,
onSecondary = Color.Black,
onBackground = Color.White,
onSurface = Color.White,
onError = Color.Black


)

private val LightColorPalette = lightColors(
//    primary = Purple500,
//    primaryVariant = Purple700,
//    secondary = Teal200,
//    secondaryVariant = Teal300
primary= Color(0xff3f51b5),
primaryVariant = Color(0xFF3700B3),
secondary = Color(0xff7c4dff),
secondaryVariant = Color(0xff7c4dff),

background = Color(0xffc5cae9),
surface = Color(0xffc5cae9),
error = Color(0xFFB00020),
onPrimary = Color.White,
onSecondary = Color.Black,
onBackground = Color.Black,
onSurface = Color.Black,
onError = Color.White

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun HomeControlsSystemV01Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
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
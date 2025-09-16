package com.example.playlistmaker.composeAppTheme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R

private val LightColorPalette = AppColors(
    colorPrimary = White,
    colorOnPrimary = Gray900,
    colorSecondary = Gray50,
    colorOnSecondary = Gray400,
    colorOnTertiary = Gray400,
    isDark = false
)

private val DarkColorPalette = AppColors(
    colorPrimary = Gray900,
    colorOnPrimary = White,
    colorSecondary = White,
    colorOnSecondary = Gray900,
    colorOnTertiary = White,
    isDark = true
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val customColors = if (darkTheme) DarkColorPalette else LightColorPalette
    val typography = AppTypography(
        h1 = TextStyle(
            fontFamily = AppFont.YsDisplay,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = colorResource(R.color.gray_900),
        ),
        h2 = TextStyle(
            fontFamily = AppFont.YsDisplay,
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp,
            color = customColors.colorOnPrimary,
        ),
        h3 = TextStyle(
            fontFamily = AppFont.YsDisplay,
            fontWeight = FontWeight.Medium,
            fontSize = 19.sp,
            color = customColors.colorOnPrimary,
        ),
        h4 = TextStyle(
            fontFamily = AppFont.YsDisplay,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = customColors.colorOnPrimary,
        ),
        h5 = TextStyle(
            fontFamily = AppFont.YsDisplay,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = customColors.colorOnSecondary,
        ),
        h6 = TextStyle(
            fontFamily = AppFont.YsDisplay,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = customColors.colorOnPrimary,
        ),
        button1 = TextStyle(
            fontFamily = AppFont.YsDisplay,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = colorResource(R.color.white),
        ),
        button2 = TextStyle(
            fontFamily = AppFont.YsDisplay,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = customColors.colorPrimary,
        ),
        body1 = TextStyle(
            fontFamily = AppFont.YsDisplay,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            color = customColors.colorOnPrimary,
        ),
        body2 = TextStyle(
            fontFamily = AppFont.YsDisplay,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            color = colorResource(R.color.gray_400),
        ),
        caption = TextStyle(
            fontFamily = AppFont.YsDisplay,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = customColors.colorOnPrimary,
        ),
        overline = TextStyle(
            fontFamily = AppFont.YsDisplay,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            color = customColors.colorOnTertiary,
        ),
    )

    ProvideTheme(customColors, typography) {
        MaterialTheme(
            colors = debugColors(darkTheme),
            content = content
        )
    }
}

object AppTheme {
    val colors: AppColors
        @Composable
        get() = LocalAppColors.current

    val typography: AppTypography
        @Composable
        get() = LocalAppTypography.current
}

@Stable
class AppColors(
    colorPrimary: Color,
    colorOnPrimary: Color,
    colorSecondary: Color,
    colorOnSecondary: Color,
    colorOnTertiary: Color,
    isDark: Boolean
) {
    var colorPrimary by mutableStateOf(colorPrimary)
        private set
    var colorOnPrimary by mutableStateOf(colorOnPrimary)
        private set
    var colorSecondary by mutableStateOf(colorSecondary)
        private set
    var colorOnSecondary by mutableStateOf(colorOnSecondary)
        private set
    var colorOnTertiary by mutableStateOf(colorOnTertiary)
        private set
    var isDark by mutableStateOf(isDark)
        private set

    fun update(other: AppColors) {
        colorPrimary = other.colorPrimary
        colorOnPrimary = other.colorOnPrimary
        colorSecondary = other.colorSecondary
        colorOnSecondary = other.colorOnSecondary
        colorOnTertiary = other.colorOnTertiary
        isDark = other.isDark
    }
}

@Composable
fun ProvideTheme(
    colors: AppColors,
    typography: AppTypography,
    content: @Composable () -> Unit,
) {
    val colorPalette = remember { colors }
    colorPalette.update(colors)

    val appTypography = remember { typography }

    CompositionLocalProvider(
        LocalAppColors provides colorPalette,
        LocalAppTypography provides appTypography,
        content = content
    )
}

private val LocalAppColors = staticCompositionLocalOf<AppColors> {
    error("No ColorPalette provided")
}

private val LocalAppTypography = staticCompositionLocalOf<AppTypography> {
    error("No Typography provided")
}

fun debugColors(
    darkTheme: Boolean,
    debugColor: Color = Color.Gray
) = Colors(
    primary = debugColor,
    primaryVariant = debugColor,
    secondary = debugColor,
    secondaryVariant = debugColor,
    background = debugColor,
    surface = debugColor,
    error = debugColor,
    onPrimary = debugColor,
    onSecondary = debugColor,
    onBackground = debugColor,
    onSurface = debugColor,
    onError = debugColor,
    isLight = !darkTheme
)

@Stable
class AppTypography(
    h1: TextStyle,
    h2: TextStyle,
    h3: TextStyle,
    h4: TextStyle,
    h5: TextStyle,
    h6: TextStyle,
    button1: TextStyle,
    button2: TextStyle,
    body1: TextStyle,
    body2: TextStyle,
    caption: TextStyle,
    overline: TextStyle,
) {
    var h1 by mutableStateOf(h1)
        private set
    var h2 by mutableStateOf(h2)
        private set
    var h3 by mutableStateOf(h3)
        private set
    var h4 by mutableStateOf(h4)
        private set
    var h5 by mutableStateOf(h5)
        private set
    var h6 by mutableStateOf(h6)
        private set
    var button1 by mutableStateOf(button1)
        private set
    var button2 by mutableStateOf(button2)
        private set
    var body1 by mutableStateOf(body1)
        private set
    var body2 by mutableStateOf(body2)
        private set
    var caption by mutableStateOf(caption)
        private set
    var overline by mutableStateOf(overline)
        private set
}

object AppFont {
    val YsDisplay = FontFamily(
        Font(R.font.ys_display_bold, FontWeight.Bold),
        Font(R.font.ys_display_medium, FontWeight.Medium),
        Font(R.font.ys_display_regular, FontWeight.Normal)
    )
}

package com.zdev.newsapp.ui.theme

import android.app.Activity
import android.app.Application
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography // CRITICAL: Ensure this is the M3 import
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zdev.newsapp.utils.ThemeManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// 1. Brand Colors
val PrimaryBlue = Color(0xFF1A73E8)
val SecondaryBlue = Color(0xFF8AB4F8)
val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E1E)

// 2. Dark Palette
private val DarkColorScheme = darkColorScheme(
    primary = SecondaryBlue,
    secondary = Color(0xFF03DAC6),
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

// 3. Light Palette
private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = Color(0xFF018786),
    background = Color(0xFFFDFDFD),
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 16.sp
    )
)

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val themeManager = ThemeManager(application)

    // Converts the DataStore Flow into a StateFlow that Compose can observe
    val isDarkMode: StateFlow<Boolean> = themeManager.isDarkMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch {
            themeManager.toggleTheme(isDark)
        }
    }
}

@Composable
fun NewsAppTheme(
        darkTheme: Boolean = isSystemInDarkTheme(),
        content: @Composable () -> Unit
    ) {
        val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

        // --- Status Bar Logic ---
        // This makes the top bar (clock/battery) match your app theme
        val view = LocalView.current
        if (!view.isInEditMode) {
            SideEffect {
                val window = (view.context as Activity).window
                window.statusBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                    !darkTheme
            }
        }

        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography, // This refers to the variable in Typography.kt
            content = content
        )
}
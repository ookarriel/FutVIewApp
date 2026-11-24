package com.example.futview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import com.example.futview.navigation.FutViewApp
import com.example.futview.ui.theme.FutViewTheme
import com.example.futview.ui.theme.ThemePreference

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var darkTheme by rememberSaveable {
                mutableStateOf(ThemePreference.isDark(applicationContext))
            }
            val onDarkThemeChange: (Boolean) -> Unit = remember {
                { enabled ->
                    darkTheme = enabled
                    ThemePreference.setDark(applicationContext, enabled)
                }
            }

            FutViewTheme(darkTheme = darkTheme) {
                SideEffect {
                    WindowCompat.getInsetsController(window, window.decorView).apply {
                        isAppearanceLightStatusBars = !darkTheme
                        isAppearanceLightNavigationBars = !darkTheme
                    }
                }
                FutViewApp(
                    darkTheme = darkTheme,
                    onDarkThemeChange = onDarkThemeChange
                )
            }
        }
    }
}

package com.seongokryu.relocationplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seongokryu.relocationplanner.data.preferences.ThemePreferences
import com.seongokryu.relocationplanner.domain.model.ThemeMode
import com.seongokryu.relocationplanner.ui.navigation.RelocationNavHost
import com.seongokryu.relocationplanner.ui.theme.RelocationPlannerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var themePreferences: ThemePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by themePreferences.themeMode
                .collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)
            val darkTheme =
                when (themeMode) {
                    ThemeMode.DARK -> true
                    ThemeMode.LIGHT -> false
                    ThemeMode.SYSTEM -> isSystemInDarkTheme()
                }

            RelocationPlannerTheme(darkTheme = darkTheme) {
                RelocationNavHost(
                    themeMode = themeMode,
                    onThemeModeChanged = themePreferences::setThemeMode,
                )
            }
        }
    }
}

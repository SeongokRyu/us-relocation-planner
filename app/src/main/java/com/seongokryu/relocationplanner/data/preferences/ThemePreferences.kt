package com.seongokryu.relocationplanner.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.seongokryu.relocationplanner.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemePreferences
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) {
        val themeMode: Flow<ThemeMode> =
            dataStore.data.map { prefs ->
                val name = prefs[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name
                runCatching { ThemeMode.valueOf(name) }.getOrDefault(ThemeMode.SYSTEM)
            }

        suspend fun setThemeMode(mode: ThemeMode) {
            dataStore.edit { prefs ->
                prefs[THEME_MODE_KEY] = mode.name
            }
        }

        val notificationsEnabled: Flow<Boolean> =
            dataStore.data.map { prefs ->
                prefs[NOTIFICATIONS_KEY] ?: true
            }

        suspend fun setNotificationsEnabled(enabled: Boolean) {
            dataStore.edit { prefs ->
                prefs[NOTIFICATIONS_KEY] = enabled
            }
        }

        companion object {
            private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
            private val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications_enabled")
        }
    }

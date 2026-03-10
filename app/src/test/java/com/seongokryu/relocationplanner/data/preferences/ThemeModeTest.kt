package com.seongokryu.relocationplanner.data.preferences

import com.seongokryu.relocationplanner.domain.model.ThemeMode
import kotlin.test.Test
import kotlin.test.assertEquals

class ThemeModeTest {
    @Test
    fun should_have_three_modes() {
        assertEquals(3, ThemeMode.entries.size)
    }

    @Test
    fun should_cycle_system_to_dark() {
        val next = nextThemeMode(ThemeMode.SYSTEM)
        assertEquals(ThemeMode.DARK, next)
    }

    @Test
    fun should_cycle_dark_to_light() {
        val next = nextThemeMode(ThemeMode.DARK)
        assertEquals(ThemeMode.LIGHT, next)
    }

    @Test
    fun should_cycle_light_to_system() {
        val next = nextThemeMode(ThemeMode.LIGHT)
        assertEquals(ThemeMode.SYSTEM, next)
    }

    @Test
    fun should_parse_valid_theme_mode_name() {
        ThemeMode.entries.forEach { mode ->
            val parsed = runCatching { ThemeMode.valueOf(mode.name) }.getOrDefault(ThemeMode.SYSTEM)
            assertEquals(mode, parsed)
        }
    }

    @Test
    fun should_fallback_to_system_for_invalid_name() {
        val parsed = runCatching { ThemeMode.valueOf("INVALID") }.getOrDefault(ThemeMode.SYSTEM)
        assertEquals(ThemeMode.SYSTEM, parsed)
    }

    private fun nextThemeMode(current: ThemeMode): ThemeMode =
        when (current) {
            ThemeMode.SYSTEM -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.LIGHT
            ThemeMode.LIGHT -> ThemeMode.SYSTEM
        }
}

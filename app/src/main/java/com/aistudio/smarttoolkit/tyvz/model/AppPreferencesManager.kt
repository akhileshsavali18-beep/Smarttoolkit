package com.aistudio.smarttoolkit.tyvz.model

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Theme mode options for APS TOOLS.
 */
enum class ThemeMode(val title: String) {
    SYSTEM("System Default"),
    LIGHT("Light Mode"),
    DARK("Dark Mode")
}

/**
 * Manages user preferences:
 * 1. Pinned/Favorite tools on the home screen.
 * 2. Theme mode (System, Light, Dark) with Material 3 dynamic color support.
 */
class AppPreferencesManager(context: Context) {

    private val prefs = context.getSharedPreferences("aps_tools_prefs", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(getSavedThemeMode())
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _useDynamicColor = MutableStateFlow(prefs.getBoolean(KEY_DYNAMIC_COLOR, true))
    val useDynamicColor: StateFlow<Boolean> = _useDynamicColor.asStateFlow()

    // Store pinned tool IDs (AppScreen names)
    private val _favoriteTools = MutableStateFlow(getSavedFavorites())
    val favoriteTools: StateFlow<Set<String>> = _favoriteTools.asStateFlow()

    // Store recent tool IDs (AppScreen names, ordered from most recent)
    private val _recentTools = MutableStateFlow(getSavedRecentTools())
    val recentTools: StateFlow<List<String>> = _recentTools.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(prefs.getBoolean(KEY_NOTIFICATIONS, true))
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private fun getSavedThemeMode(): ThemeMode {
        val name = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name)
        return try {
            ThemeMode.valueOf(name ?: ThemeMode.SYSTEM.name)
        } catch (_: Exception) {
            ThemeMode.SYSTEM
        }
    }

    private fun getSavedFavorites(): Set<String> {
        return prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }

    private fun getSavedRecentTools(): List<String> {
        val raw = prefs.getString(KEY_RECENT_TOOLS, "") ?: ""
        return if (raw.isBlank()) {
            emptyList()
        } else {
            raw.split(",").filter { it.isNotBlank() }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
        _themeMode.value = mode
    }

    fun setDynamicColor(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DYNAMIC_COLOR, enabled).apply()
        _useDynamicColor.value = enabled
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply()
        _notificationsEnabled.value = enabled
    }

    fun recordRecentTool(screenName: String) {
        if (screenName == AppScreen.SPLASH.name || screenName == AppScreen.DASHBOARD.name) return
        val current = _recentTools.value.toMutableList()
        current.remove(screenName)
        current.add(0, screenName)
        val trimmed = current.take(8)
        prefs.edit().putString(KEY_RECENT_TOOLS, trimmed.joinToString(",")).apply()
        _recentTools.value = trimmed
    }

    fun toggleFavorite(screenName: String) {
        val current = _favoriteTools.value.toMutableSet()
        if (current.contains(screenName)) {
            current.remove(screenName)
        } else {
            current.add(screenName)
        }
        prefs.edit().putStringSet(KEY_FAVORITES, current).apply()
        _favoriteTools.value = current
    }

    fun isFavorite(screenName: String): Boolean {
        return _favoriteTools.value.contains(screenName)
    }

    companion object {
        private const val KEY_THEME_MODE = "key_theme_mode"
        private const val KEY_DYNAMIC_COLOR = "key_dynamic_color"
        private const val KEY_FAVORITES = "key_favorites"
        private const val KEY_RECENT_TOOLS = "key_recent_tools"
        private const val KEY_NOTIFICATIONS = "key_notifications"

        @Volatile
        private var instance: AppPreferencesManager? = null

        fun getInstance(context: Context): AppPreferencesManager {
            return instance ?: synchronized(this) {
                instance ?: AppPreferencesManager(context.applicationContext).also { instance = it }
            }
        }
    }
}

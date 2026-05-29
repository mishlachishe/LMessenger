package ru.mishlachok.LMessageClient.presentation.theme

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AppThemeState {
	private const val PREFS_NAME = "app_theme_prefs"
	private const val KEY_DARK_THEME = "dark_theme"
	private var prefs: SharedPreferences? = null

	private val _darkTheme = MutableStateFlow<Boolean?>(null)
	val darkTheme: StateFlow<Boolean?> = _darkTheme.asStateFlow()

	fun init(context: Context) {
		val storage = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
		prefs = storage
		_darkTheme.value = if (storage.contains(KEY_DARK_THEME)) {
			storage.getBoolean(KEY_DARK_THEME, false)
		} else {
			null
		}
	}

	fun setDarkTheme(enabled: Boolean) {
		_darkTheme.value = enabled
		prefs?.edit()?.putBoolean(KEY_DARK_THEME, enabled)?.apply()
	}
}

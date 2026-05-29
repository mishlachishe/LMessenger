package ru.mishlachok.LMessageClient.presentation.screens.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import ru.mishlachok.LMessageClient.presentation.theme.AppThemeState
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {
	val darkTheme: StateFlow<Boolean?> = AppThemeState.darkTheme

	fun toggleDarkTheme(enabled: Boolean) {
		AppThemeState.setDarkTheme(enabled)
	}
}

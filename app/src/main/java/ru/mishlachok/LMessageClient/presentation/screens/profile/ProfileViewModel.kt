package ru.mishlachok.LMessageClient.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.mishlachok.LMessageClient.domain.model.User
import ru.mishlachok.LMessageClient.domain.usecase.GetMeUseCase
import ru.mishlachok.LMessageClient.domain.usecase.LogoutUseCase
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
	private val getMeUseCase: GetMeUseCase,
	private val logoutUseCase: LogoutUseCase
) : ViewModel() {
	private val _user = MutableStateFlow<User?>(null)
	val user: StateFlow<User?> = _user

	fun loadProfile() {
		viewModelScope.launch {
			getMeUseCase().onSuccess { _user.value = it }
		}
	}

	fun logout() {
		viewModelScope.launch { logoutUseCase() }
	}
}
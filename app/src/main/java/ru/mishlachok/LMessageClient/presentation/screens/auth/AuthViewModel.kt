package ru.mishlachok.LMessageClient.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.mishlachok.LMessageClient.domain.usecase.LoginUseCase
import ru.mishlachok.LMessageClient.domain.usecase.LogoutUseCase
import ru.mishlachok.LMessageClient.domain.usecase.RegisterUseCase
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
	private val loginUseCase: LoginUseCase,
	private val registerUseCase: RegisterUseCase,
	private val logoutUseCase: LogoutUseCase
) : ViewModel() {

	private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
	val loginState: StateFlow<AuthState> = _loginState

	private val _registerState = MutableStateFlow<AuthState>(AuthState.Idle)
	val registerState: StateFlow<AuthState> = _registerState

	fun login(login: String, password: String) {
		viewModelScope.launch {
			_loginState.value = AuthState.Loading
			loginUseCase(login, password).fold(
				onSuccess = { _loginState.value = AuthState.Success },
				onFailure = { _loginState.value = AuthState.Error(it.message ?: "Login failed") }
			)
		}
	}

	fun register(login: String, password: String, displayName: String) {
		viewModelScope.launch {
			_registerState.value = AuthState.Loading
			registerUseCase(login, password, displayName).fold(
				onSuccess = { _registerState.value = AuthState.Success },
				onFailure = { _registerState.value = AuthState.Error(it.message ?: "Registration failed") }
			)
		}
	}

	fun logout() {
		viewModelScope.launch { logoutUseCase() }
	}
}

sealed class AuthState {
	object Idle : AuthState()
	object Loading : AuthState()
	object Success : AuthState()
	data class Error(val message: String) : AuthState()
}
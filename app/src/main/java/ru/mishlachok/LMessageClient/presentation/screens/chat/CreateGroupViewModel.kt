package ru.mishlachok.LMessageClient.presentation.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.mishlachok.LMessageClient.domain.usecase.CreateGroupChatUseCase
import javax.inject.Inject

@HiltViewModel
class CreateGroupViewModel @Inject constructor(
	private val createGroupChatUseCase: CreateGroupChatUseCase
) : ViewModel() {
	private val _createState = MutableStateFlow<CreateGroupState>(CreateGroupState.Idle)
	val createState: StateFlow<CreateGroupState> = _createState

	fun createGroup(name: String, memberLogins: List<String>) {
		if (name.isBlank()) {
			_createState.value = CreateGroupState.Error("Введите название группы")
			return
		}
		viewModelScope.launch {
			_createState.value = CreateGroupState.Loading
			createGroupChatUseCase(name, memberLogins)
				.onSuccess { _createState.value = CreateGroupState.Success }
				.onFailure { _createState.value = CreateGroupState.Error(it.message ?: "Error") }
		}
	}
}

sealed class CreateGroupState {
	object Idle : CreateGroupState()
	object Loading : CreateGroupState()
	object Success : CreateGroupState()
	data class Error(val message: String) : CreateGroupState()
}
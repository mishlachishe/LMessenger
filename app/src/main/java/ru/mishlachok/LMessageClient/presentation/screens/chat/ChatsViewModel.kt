package ru.mishlachok.LMessageClient.presentation.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mishlachok.LMessageClient.domain.model.Chat
import ru.mishlachok.LMessageClient.domain.usecase.CreateDirectChatUseCase
import ru.mishlachok.LMessageClient.domain.usecase.CreateGroupChatUseCase
import ru.mishlachok.LMessageClient.domain.usecase.DeleteChatUseCase
import ru.mishlachok.LMessageClient.domain.usecase.GetChatsUseCase
import ru.mishlachok.LMessageClient.domain.usecase.GetMeUseCase
import javax.inject.Inject

sealed class CreateChatState {
	object Idle : CreateChatState()
	object Loading : CreateChatState()
	object Success : CreateChatState()
	data class Error(val message: String) : CreateChatState()
}

@HiltViewModel
class ChatsViewModel @Inject constructor(
	private val getChatsUseCase: GetChatsUseCase,
	private val createDirectChatUseCase: CreateDirectChatUseCase,
	private val createGroupChatUseCase: CreateGroupChatUseCase,
	private val deleteChatUseCase: DeleteChatUseCase,
	private val getMeUseCase: GetMeUseCase
) : ViewModel() {
	private val _chats = MutableStateFlow<List<Chat>>(emptyList())
	val chats: StateFlow<List<Chat>> = _chats

	private val _error = MutableStateFlow<String?>(null)
	val error: StateFlow<String?> = _error
	private val _currentUserId = MutableStateFlow<Long?>(null)
	val currentUserId: StateFlow<Long?> = _currentUserId

	init {
		loadChats()
		loadCurrentUser()
	}

	fun loadChats() {
		viewModelScope.launch {
			getChatsUseCase()
				.onSuccess { _chats.value = it }
				.onFailure { e -> _error.value = e.message ?: "Не удалось загрузить чаты" }
		}
	}
	private fun loadCurrentUser() {
		viewModelScope.launch {
			getMeUseCase().onSuccess { user -> _currentUserId.value = user.id }
		}
	}
	fun deleteChat(chatId: Long) {
		viewModelScope.launch {
			deleteChatUseCase(chatId)
				.onSuccess { if (it) _chats.update { list -> list.filter { c -> c.id != chatId } } }
				.onFailure { e -> _error.value = e.message }
		}
	}

	fun createDirectChat(userLogin: String) {
		viewModelScope.launch {
			createDirectChatUseCase(userLogin)
				.onSuccess { newChat ->
					_chats.update { currentList ->
						if (currentList.none { it.id == newChat.id }) {
							currentList + newChat
						} else currentList
					}
				}
				.onFailure { e ->
					_error.value = e.message ?: "Не удалось создать чат"
				}
		}
	}

	fun createGroupChat(name: String, memberLogins: List<String>) {
		viewModelScope.launch {
			createGroupChatUseCase(name, memberLogins)
				.onSuccess { newChat ->
					_chats.update { currentList ->
						if (currentList.none { it.id == newChat.id }) {
							currentList + newChat
						} else currentList
					}
				}
				.onFailure { e ->
					_error.value = e.message ?: "Не удалось создать группу"
				}
		}
	}

	fun clearError() {
		_error.value = null
	}
	fun showError(message: String) {
		_error.value = message
	}
}
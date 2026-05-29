package ru.mishlachok.LMessageClient.presentation.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.mishlachok.LMessageClient.domain.model.Message
import ru.mishlachok.LMessageClient.domain.usecase.AddAttachmentUseCase
import ru.mishlachok.LMessageClient.domain.usecase.GetMeUseCase
import ru.mishlachok.LMessageClient.domain.usecase.GetMessagesUseCase
import ru.mishlachok.LMessageClient.domain.usecase.SendMessageUseCase
import javax.inject.Inject

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
	private val getMessagesUseCase: GetMessagesUseCase,
	private val sendMessageUseCase: SendMessageUseCase,
	private val addAttachmentUseCase: AddAttachmentUseCase,
	private val getMeUseCase: GetMeUseCase
) : ViewModel() {
	private val _messages = MutableStateFlow<List<Message>>(emptyList())
	val messages: StateFlow<List<Message>> = _messages

	private val _isLoading = MutableStateFlow(false)
	val isLoading: StateFlow<Boolean> = _isLoading

	private val _error = MutableStateFlow<String?>(null)
	val error: StateFlow<String?> = _error

	private val _currentUserId = MutableStateFlow<Long?>(null)
	val currentUserId: StateFlow<Long?> = _currentUserId

	init {
		loadCurrentUser()
	}

	private fun loadCurrentUser() {
		viewModelScope.launch {
			getMeUseCase().onSuccess { user -> _currentUserId.value = user.id }
		}
	}

	fun loadMessages(chatId: Long) {
		viewModelScope.launch {
			_isLoading.value = true
			getMessagesUseCase(chatId)
				.onSuccess { list ->
					_messages.value = list.reversed()
				}
				.onFailure { e ->
					_error.value = e.message ?: "Ошибка загрузки"
				}
			_isLoading.value = false
		}
	}

	fun sendMessage(chatId: Long, text: String) {
		viewModelScope.launch {
			sendMessageUseCase(chatId, text)
				.onSuccess { loadMessages(chatId) }
				.onFailure { e ->
					_error.value = e.message ?: "Ошибка отправки"
				}
		}
	}

	fun sendAttachment(chatId: Long, filePath: String, mimeType: String?) {
		viewModelScope.launch {
			sendMessageUseCase(chatId, null)
				.onSuccess { message ->
					addAttachmentUseCase(message.id, filePath, mimeType)
						.onSuccess { loadMessages(chatId) }
						.onFailure { e -> _error.value = e.message ?: "Ошибка загрузки файла" }
				}
				.onFailure { e ->
					_error.value = e.message ?: "Ошибка отправки файла"
				}
		}
	}

	fun clearError() { _error.value = null }
}

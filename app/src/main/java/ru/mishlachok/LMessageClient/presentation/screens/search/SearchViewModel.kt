package ru.mishlachok.LMessageClient.presentation.screens.search

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.mishlachok.LMessageClient.data.datastore.SearchHistoryStorage
import ru.mishlachok.LMessageClient.domain.model.User
import ru.mishlachok.LMessageClient.domain.usecase.CreateDirectChatUseCase
import ru.mishlachok.LMessageClient.domain.usecase.SearchUsersUseCase
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
	private val searchUsersUseCase: SearchUsersUseCase,
	private val searchHistoryStorage: SearchHistoryStorage,
	private val createDirectChatUseCase: CreateDirectChatUseCase
) : ViewModel() {

	var query by mutableStateOf("")
		private set

	var users by mutableStateOf<List<User>>(emptyList())
		private set
	var isLoading by mutableStateOf(false)
		private set
	var error by mutableStateOf<String?>(null)
		private set
	val history: Flow<List<String>> = searchHistoryStorage.historyFlow
	fun onQueryChange(newQuery: String) {
		query = newQuery
	}

	fun search(query: String) {
		if (query.isBlank()) return
		viewModelScope.launch {
			isLoading = true
			error = null
			searchUsersUseCase(query)
				.onSuccess { users = it }
				.onFailure { e ->
					error = e.message ?: "Ошибка"
					users = emptyList()
				}
			isLoading = false
		}
	}

	fun addToHistory(query: String) {
		if (query.isNotBlank()) {
			viewModelScope.launch { searchHistoryStorage.addQuery(query) }
		}
	}

	fun clearHistory() {
		viewModelScope.launch { searchHistoryStorage.clearHistory() }
	}

	fun createChat(userId: Long) {
		viewModelScope.launch {
			createDirectChatUseCase(userId)
		}
	}
}
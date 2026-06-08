package ru.mishlachok.LMessageClient.presentation.screens.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
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

	private val _history = MutableStateFlow<List<String>>(emptyList())
	val history: StateFlow<List<String>> = _history.asStateFlow()

	private var searchJob: Job? = null

	init {
		viewModelScope.launch {
			searchHistoryStorage.historyFlow.collect { list ->
				_history.value = list
			}
		}
	}

	fun onQueryChange(newQuery: String) {
		query = newQuery
		searchJob?.cancel()
		if (newQuery.isBlank()) {
			users = emptyList()
			return
		}
		searchJob = viewModelScope.launch {
			delay(300)
			search(newQuery)
		}
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
		viewModelScope.launch {
			searchHistoryStorage.clearHistory()
			_history.value = emptyList()
		}
	}

	fun createChat(userLogin: String) {
		viewModelScope.launch {
			createDirectChatUseCase(userLogin)
		}
	}
}
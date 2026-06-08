package ru.mishlachok.LMessageClient.presentation.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import ru.mishlachok.LMessageClient.domain.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
	navController: NavHostController,
	viewModel: SearchViewModel = hiltViewModel()
) {
	val query = viewModel.query
	val users = viewModel.users
	val isLoading = viewModel.isLoading
	val error = viewModel.error
	val history by viewModel.history.collectAsState(initial = emptyList())
	val focusRequester = remember { FocusRequester() }
	val focusManager = LocalFocusManager.current
	var isFocused by remember { mutableStateOf(false) }

	LaunchedEffect(Unit) { focusRequester.requestFocus() }

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Поиск пользователей") },
				navigationIcon = {
					IconButton(onClick = { navController.popBackStack() }) {
						Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
					}
				}
			)
		}
	) { padding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(8.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				TextField(
					value = query,
					onValueChange = { viewModel.onQueryChange(it) },
					modifier = Modifier
						.weight(1f)
						.focusRequester(focusRequester)
						.onFocusChanged { isFocused = it.isFocused },
					placeholder = { Text("Введите имя или логин") },
					singleLine = true,
					trailingIcon = {
						if (query.isNotEmpty()) {
							IconButton(onClick = {
								viewModel.onQueryChange("")
								focusManager.clearFocus()
							}) {
								Icon(Icons.Default.Clear, contentDescription = "Очистить")
							}
						}
					},
					keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
					keyboardActions = KeyboardActions(
						onSearch = {
							viewModel.addToHistory(query)
							focusManager.clearFocus()
						}
					)
				)
			}

			if (query.isEmpty() && isFocused && history.isNotEmpty()) {
				HistorySection(
					history = history,
					onItemClick = { item ->
						viewModel.onQueryChange(item)
						viewModel.search(item)
						viewModel.addToHistory(item)
						focusManager.clearFocus()
					},
					onClearHistory = { viewModel.clearHistory() }
				)
			}

			when {
				isLoading -> Box(
					modifier = Modifier.fillMaxSize(),
					contentAlignment = Alignment.Center
				) {
					CircularProgressIndicator()
				}
				error != null -> ErrorPlaceholder(
					message = error!!,
					onRetry = { viewModel.search(query) }
				)
				query.isNotEmpty() && users.isEmpty() && !isLoading -> EmptyPlaceholder()
				users.isNotEmpty() -> LazyColumn {
					items(users) { user ->
						UserListItem(user = user, onAddChat = { viewModel.createChat(user.login) })
					}
				}
			}
		}
	}
}

@Composable
private fun HistorySection(
	history: List<String>,
	onItemClick: (String) -> Unit,
	onClearHistory: () -> Unit
) {
	Column(modifier = Modifier.fillMaxWidth()) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp, vertical = 4.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Text("История поиска", style = MaterialTheme.typography.labelLarge)
			Spacer(modifier = Modifier.weight(1f))
			TextButton(onClick = onClearHistory) {
				Text("Очистить историю")
			}
		}
		history.forEach { item ->
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.clickable { onItemClick(item) }
					.padding(horizontal = 16.dp, vertical = 8.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(20.dp))
				Spacer(modifier = Modifier.width(8.dp))
				Text(item, style = MaterialTheme.typography.bodyLarge)
			}
		}
		HorizontalDivider()
	}
}

@Composable
private fun ErrorPlaceholder(message: String, onRetry: () -> Unit) {
	Box(
		modifier = Modifier.fillMaxSize(),
		contentAlignment = Alignment.Center
	) {
		Column(horizontalAlignment = Alignment.CenterHorizontally) {
			Text(message, style = MaterialTheme.typography.bodyLarge)
			Spacer(modifier = Modifier.height(16.dp))
			Button(onClick = onRetry) {
				Text("Обновить")
			}
		}
	}
}

@Composable
private fun EmptyPlaceholder() {
	Box(
		modifier = Modifier.fillMaxSize(),
		contentAlignment = Alignment.Center
	) {
		Text("Пользователи не найдены", style = MaterialTheme.typography.bodyLarge)
	}
}

@Composable
private fun UserListItem(user: User, onAddChat: () -> Unit) {
	ListItem(
		headlineContent = { Text(user.displayName) },
		supportingContent = { Text("@${user.login}") },
		trailingContent = {
			Button(onClick = onAddChat) {
				Text("Написать")
			}
		}
	)
	HorizontalDivider()
}
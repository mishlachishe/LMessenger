package ru.mishlachok.LMessageClient.presentation.screens.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ru.mishlachok.LMessageClient.domain.model.Chat
import ru.mishlachok.LMessageClient.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(
	navController: NavHostController,
	viewModel: ChatsViewModel = hiltViewModel()
) {
	val chats by viewModel.chats.collectAsStateWithLifecycle()
	val errorMessage by viewModel.error.collectAsStateWithLifecycle()
	var showDirectChatDialog by remember { mutableStateOf(false) }
	var directUserId by remember { mutableStateOf("") }
	val snackbarHostState = remember { SnackbarHostState() }
	val currentUserId by viewModel.currentUserId.collectAsStateWithLifecycle()

	var chatToDelete by remember { mutableStateOf<Chat?>(null) }
	if (chatToDelete != null) {
		AlertDialog(
			onDismissRequest = { chatToDelete = null },
			title = { Text("Удалить чат?") },
			text = { Text("Это действие нельзя отменить.") },
			confirmButton = {
				TextButton(onClick = {
					chatToDelete?.let { viewModel.deleteChat(it.id) }
					chatToDelete = null
				}) { Text("Удалить") }
			},
			dismissButton = { TextButton(onClick = { chatToDelete = null }) { Text("Отмена") } }
		)
	}

	LaunchedEffect(Unit) { viewModel.loadChats() }

	LaunchedEffect(errorMessage) {
		errorMessage?.let {
			snackbarHostState.showSnackbar(it)
			viewModel.clearError()
		}
	}

	if (showDirectChatDialog) {
		AlertDialog(
			onDismissRequest = { showDirectChatDialog = false },
			title = { Text("Новый личный чат") },
			text = {
				TextField(
					value = directUserId,
					onValueChange = { directUserId = it },
					label = { Text("ID пользователя") },
					singleLine = true,
					isError = directUserId.toLongOrNull() == null && directUserId.isNotBlank()
				)
			},
			confirmButton = {
				TextButton(onClick = {
					val userId = directUserId.toLongOrNull()
					when {
						userId == null -> {
							return@TextButton
						}
						userId == currentUserId -> {
							viewModel.showError("Нельзя создать чат с самим собой")
							return@TextButton
						}
						else -> {
							viewModel.createDirectChat(userId)
							showDirectChatDialog = false
							directUserId = ""
						}
					}
				}) { Text("Создать") }
			},
			dismissButton = {
				TextButton(onClick = { showDirectChatDialog = false }) {
					Text("Отмена")
				}
			}
		)
	}

	Scaffold(
		snackbarHost = { SnackbarHost(snackbarHostState) },
		topBar = {
			TopAppBar(

				title = { Text("Чаты") },
				actions = {

					IconButton(onClick = { navController.navigate(Screen.Search.route) }) {
						Icon(Icons.Default.Search, contentDescription = "Поиск")
					}
					IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
						Icon(Icons.Default.Person, contentDescription = "Профиль")
					}
					IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
						Icon(Icons.Default.Settings, contentDescription = "Настройки")
					}
				}

			)
		},
		floatingActionButton = {
			Column(horizontalAlignment = Alignment.End) {
				FloatingActionButton(
					onClick = { showDirectChatDialog = true },
					modifier = Modifier.padding(bottom = 8.dp)
				) {
					Icon(Icons.Default.PersonAdd, contentDescription = "Новый личный чат")
				}
				FloatingActionButton(
					onClick = { navController.navigate(Screen.CreateGroup.route) }
				) {
					Icon(Icons.Default.Add, contentDescription = "Новая группа")
				}
			}
		}
	) { padding ->
		if (chats.isEmpty()) {
			Box(
				modifier = Modifier.fillMaxSize().padding(padding),
				contentAlignment = Alignment.Center
			) {
				Text("Нет чатов. Создайте личный или групповой чат.")
			}
		} else {
			LazyColumn(modifier = Modifier.padding(padding)) {
				items(chats) { chat ->
					ChatItem(
						chat = chat,
						currentUserId = currentUserId,
						onClick = { navController.navigate(Screen.ChatDetail.createRoute(chat.id)) },
						onLongClick = { chatToDelete = chat }
					)
				}
			}
		}
	}
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatItem(
	chat: Chat,
	currentUserId: Long?,
	onClick: () -> Unit,
	onLongClick: () -> Unit
) {
	val displayName = when {
		!chat.isGroup && currentUserId != null -> {
			chat.members.firstOrNull { it.id != currentUserId }?.displayName ?: "Unknown"
		}
		else -> chat.name ?: "Группа"
	}

	val lastMessageText = chat.lastMessage?.takeIf { it.isNotBlank() } ?: "Нет сообщений"

	ListItem(
		headlineContent = { Text(displayName) },
		supportingContent = { Text(lastMessageText) },
		modifier = Modifier.combinedClickable(
			onClick = onClick,
			onLongClick = onLongClick
		)
	)
}
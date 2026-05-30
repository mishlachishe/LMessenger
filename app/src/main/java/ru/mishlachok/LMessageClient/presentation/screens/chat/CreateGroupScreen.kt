package ru.mishlachok.LMessageClient.presentation.screens.chat

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(
	navController: NavHostController,
	viewModel: CreateGroupViewModel = hiltViewModel()
) {
	var groupName by remember { mutableStateOf("") }
	var userIdInput by remember { mutableStateOf("") }
	val state by viewModel.createState.collectAsStateWithLifecycle()

	LaunchedEffect(state) {
		if (state is CreateGroupState.Success) navController.popBackStack()
	}

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Новая группа") },
				navigationIcon = {
					IconButton(onClick = { navController.popBackStack() }) {
						Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
					}
				}
			)
		}
	) { padding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
				.padding(16.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			TextField(
				value = groupName,
				onValueChange = { groupName = it },
				label = { Text("Название группы") },
				modifier = Modifier.fillMaxWidth()
			)
			Spacer(modifier = Modifier.height(8.dp))
			TextField(
				value = userIdInput,
				onValueChange = { userIdInput = it },
				label = { Text("Имена участников через запятую") },
				placeholder = { Text("1, 2, 3") },
				modifier = Modifier.fillMaxWidth()
			)
			Spacer(modifier = Modifier.height(16.dp))
			Button(
				onClick = {
					val ids = userIdInput.split(",").mapNotNull { it.trim().toLongOrNull() }
				viewModel.createGroup(groupName, ids)
				},
				modifier = Modifier.fillMaxWidth()
			) {
				Text("Создать группу")
			}
			when (val s = state) {
				is CreateGroupState.Loading -> CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
				is CreateGroupState.Error -> Text(
					s.message,
					color = MaterialTheme.colorScheme.error,
					modifier = Modifier.padding(top = 16.dp)
				)
				else -> {}
			}
		}
	}
}
package ru.mishlachok.LMessageClient.presentation.screens.profile

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
import ru.mishlachok.LMessageClient.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
	navController: NavHostController,
	viewModel: ProfileViewModel = hiltViewModel()
) {
	val user by viewModel.user.collectAsStateWithLifecycle()

	LaunchedEffect(Unit) { viewModel.loadProfile() }

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Профиль") },
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
			user?.let { u ->
				ElevatedCard(modifier = Modifier.fillMaxWidth()) {
					Column(modifier = Modifier.padding(16.dp)) {
						Text("Логин: ${u.login}")
						Text("Отображаемое имя: ${u.displayName}")
						Text("Статус: ${if (u.isOnline) "Online" else "Offline"}")
					}
				}
			} ?: CircularProgressIndicator()
			Spacer(modifier = Modifier.height(16.dp))
			Button(onClick = { viewModel.logout(); navController.navigate(Screen.Login.route) { popUpTo(0) } }) {
				Text("Выйти")
			}
		}
	}
}

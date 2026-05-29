package ru.mishlachok.LMessageClient.presentation.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ru.mishlachok.LMessageClient.presentation.navigation.Screen

@Composable
fun LoginScreen(
	navController: NavHostController,
	viewModel: AuthViewModel = hiltViewModel()
) {
	val login = remember { mutableStateOf("") }
	val password = remember { mutableStateOf("") }
	val state by viewModel.loginState.collectAsStateWithLifecycle()

	LaunchedEffect(state) {
		if (state is AuthState.Success) {
			navController.navigate(Screen.Chats.route) {
				popUpTo(0) { inclusive = true }
			}
		}
	}

	Surface(
		modifier = Modifier.fillMaxSize(),
		color = MaterialTheme.colorScheme.background
	) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(16.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			Text(
				text = "Вход",
				style = MaterialTheme.typography.headlineMedium,
				modifier = Modifier.padding(bottom = 24.dp)
			)

			TextField(
				value = login.value,
				onValueChange = { login.value = it },
				label = { Text("Логин") },
				singleLine = true,
				modifier = Modifier.fillMaxWidth()
			)
			Spacer(modifier = Modifier.height(8.dp))

			TextField(
				value = password.value,
				onValueChange = { password.value = it },
				label = { Text("Пароль") },
				visualTransformation = PasswordVisualTransformation(),
				singleLine = true,
				modifier = Modifier.fillMaxWidth()
			)
			Spacer(modifier = Modifier.height(16.dp))

			Button(
				onClick = { viewModel.login(login.value, password.value) },
				modifier = Modifier.fillMaxWidth(),
				enabled = login.value.isNotBlank() && password.value.isNotBlank()
			) {
				Text("Войти")
			}

			Spacer(modifier = Modifier.height(16.dp))
			TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
				Text("Нет аккаунта? Зарегистрироваться")
			}

			if (state is AuthState.Error) {
				Spacer(modifier = Modifier.height(8.dp))
				Text(
					text = (state as AuthState.Error).message,
					color = MaterialTheme.colorScheme.error,
					style = MaterialTheme.typography.bodyMedium
				)
			}

			if (state is AuthState.Loading) {
				Spacer(modifier = Modifier.height(8.dp))
				CircularProgressIndicator()
			}
		}
	}
}
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
fun RegisterScreen(
	navController: NavHostController,
	viewModel: AuthViewModel = hiltViewModel()
) {
	val login = remember { mutableStateOf("") }
	val password = remember { mutableStateOf("") }
	val displayName = remember { mutableStateOf("") }
	val state by viewModel.registerState.collectAsStateWithLifecycle()

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
			modifier = Modifier.fillMaxSize().padding(16.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			Text(
				text = "Регистрация",
				style = MaterialTheme.typography.headlineMedium,
				color = MaterialTheme.colorScheme.onBackground,
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
				value = displayName.value,
				onValueChange = { displayName.value = it },
				label = { Text("Отображаемое имя") },
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
				onClick = { viewModel.register(login.value, password.value, displayName.value) },
				modifier = Modifier.fillMaxWidth(),
				enabled = login.value.isNotBlank() && password.value.isNotBlank() && displayName.value.isNotBlank()
			) {
				Text("Зарегистрироваться")
			}

			Spacer(modifier = Modifier.height(16.dp))
			TextButton(onClick = {
				navController.navigate(Screen.Login.route) {
					popUpTo(Screen.Register.route) { inclusive = true }
				}
			}) {
				Text("У вас уже есть аккаунт? Войти")
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
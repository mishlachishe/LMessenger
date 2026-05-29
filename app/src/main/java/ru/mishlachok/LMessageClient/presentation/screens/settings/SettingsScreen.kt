package ru.mishlachok.LMessageClient.presentation.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
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
fun SettingsScreen(
	navController: NavHostController,
	viewModel: SettingsViewModel = hiltViewModel()
) {
	val savedDarkTheme by viewModel.darkTheme.collectAsStateWithLifecycle()
	val systemDarkTheme = isSystemInDarkTheme()
	val darkTheme = savedDarkTheme ?: systemDarkTheme
	var showAboutDialog by remember { mutableStateOf(false) }

	if (showAboutDialog) {
		AlertDialog(
			onDismissRequest = { showAboutDialog = false },
			title = { Text("О приложении") },
			text = {
				Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
					Text("LMessage")
					Text("Мессенджер для личных и групповых чатов с поддержкой вложений.")
					Text("Версия: 1.0")
				}
			},
			confirmButton = {
				TextButton(onClick = { showAboutDialog = false }) {
					Text("ОК")
				}
			}
		)
	}

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Настройки") },
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
			ElevatedCard(modifier = Modifier.fillMaxWidth()) {
				Column {
					ListItem(
						headlineContent = { Text("Тёмная тема") },
						supportingContent = {
							Text(if (savedDarkTheme == null) "Сейчас как в системе" else "Выбранная тема сохраняется")
						},
						trailingContent = {
							Switch(checked = darkTheme, onCheckedChange = viewModel::toggleDarkTheme)
						}
					)
					HorizontalDivider()
					ListItem(
						headlineContent = { Text("О приложении") },
						supportingContent = { Text("Информация о LMessage") },
						leadingContent = { Icon(Icons.Default.Info, contentDescription = null) },
						modifier = Modifier.clickable { showAboutDialog = true }
					)
				}
			}
		}
	}
}

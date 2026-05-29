package com.lmessage.client.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.mishlachok.LMessageClient.presentation.screens.auth.RegisterScreen
import ru.mishlachok.LMessageClient.presentation.navigation.Screen
import ru.mishlachok.LMessageClient.presentation.screens.auth.LoginScreen
import ru.mishlachok.LMessageClient.presentation.screens.chat.ChatDetailScreen
import ru.mishlachok.LMessageClient.presentation.screens.chat.ChatsScreen
import ru.mishlachok.LMessageClient.presentation.screens.chat.CreateGroupScreen
import ru.mishlachok.LMessageClient.presentation.screens.profile.ProfileScreen
import ru.mishlachok.LMessageClient.presentation.screens.search.SearchScreen
import ru.mishlachok.LMessageClient.presentation.screens.settings.SettingsScreen

@Composable
fun AppNavGraph(
	navController: NavHostController,
	startDestination: String = Screen.Login.route
) {
	NavHost(navController = navController, startDestination = startDestination) {
		composable(Screen.Login.route) {
			LoginScreen(navController)
		}
		composable(Screen.Register.route) {
			RegisterScreen(navController)
		}
		composable(Screen.Chats.route) {
			ChatsScreen(navController)
		}
		composable(
			route = Screen.ChatDetail.route,
			arguments = listOf(navArgument("chatId") { type = NavType.LongType })
		) { backStackEntry ->
			val chatId = backStackEntry.arguments?.getLong("chatId") ?: return@composable
			ChatDetailScreen(chatId, navController)
		}
		composable(Screen.CreateGroup.route) {
			CreateGroupScreen(navController)
		}
		composable(Screen.Profile.route) {
			ProfileScreen(navController)
		}
		composable(Screen.Settings.route) {
			SettingsScreen(navController)
		}
		composable(Screen.Search.route) {
			SearchScreen(navController)
		}
	}
}
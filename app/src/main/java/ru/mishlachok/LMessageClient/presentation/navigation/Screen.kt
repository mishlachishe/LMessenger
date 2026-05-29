package ru.mishlachok.LMessageClient.presentation.navigation

sealed class Screen(val route: String) {
	object Login : Screen("login")
	object Register : Screen("register")
	object Chats : Screen("chats")
	object ChatDetail : Screen("chat/{chatId}") {
		fun createRoute(chatId: Long) = "chat/$chatId"
	}
	object CreateGroup : Screen("create_group")
	object Profile : Screen("profile")
	object Settings : Screen("settings")
	object Search : Screen("search")
}
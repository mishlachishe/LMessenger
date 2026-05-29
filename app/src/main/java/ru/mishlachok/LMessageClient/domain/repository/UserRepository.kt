package ru.mishlachok.LMessageClient.domain.repository

import ru.mishlachok.LMessageClient.domain.model.User

interface UserRepository {
	suspend fun getMe(): Result<User>
	suspend fun updateOnlineStatus(online: Boolean): Result<Boolean>
	suspend fun updateAvatar(avatarPath: String): Result<String> // returns URL
	suspend fun searchUsers(query: String): Result<List<User>>
}
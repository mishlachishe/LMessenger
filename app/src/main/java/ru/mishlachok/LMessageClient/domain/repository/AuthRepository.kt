package ru.mishlachok.LMessageClient.domain.repository

interface AuthRepository {
	suspend fun register(login: String, password: String, displayName: String): Result<String>
	suspend fun login(login: String, password: String): Result<String>
	suspend fun logout()
}
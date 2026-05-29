package ru.mishlachok.LMessageClient.data.repository
import ru.mishlachok.LMessageClient.data.datastore.TokenStorage
import ru.mishlachok.LMessageClient.data.remote.ApiService
import ru.mishlachok.LMessageClient.data.remote.dto.AuthResponse
import ru.mishlachok.LMessageClient.data.remote.dto.LoginRequest
import ru.mishlachok.LMessageClient.data.remote.dto.RegisterRequest
import ru.mishlachok.LMessageClient.data.remote.dto.UserIdResponse
import ru.mishlachok.LMessageClient.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
	private val apiService: ApiService,
	private val tokenStorage: TokenStorage
) : AuthRepository {
	override suspend fun register(login: String, password: String, displayName: String): Result<Long> {
		return try {
			val resp: UserIdResponse = apiService.post("/api/register", RegisterRequest(login, password, displayName))
			if (resp.userId != null) Result.success(resp.userId)
			else Result.failure(Exception(resp.error ?: "Registration failed"))
		} catch (e: Exception) {
			Result.failure(e)
		}
	}

	override suspend fun login(login: String, password: String): Result<String> {
		return try {
			val resp: AuthResponse = apiService.post("/api/login", LoginRequest(login, password))
			if (resp.token != null) {
				tokenStorage.saveToken(resp.token)
				Result.success(resp.token)
			} else Result.failure(Exception(resp.error ?: "Login failed"))
		} catch (e: Exception) {
			Result.failure(e)
		}
	}

	override suspend fun logout() {
		runCatching { apiService.put("/api/user/status", mapOf("online" to "false")) }
		tokenStorage.clearToken()
	}
}
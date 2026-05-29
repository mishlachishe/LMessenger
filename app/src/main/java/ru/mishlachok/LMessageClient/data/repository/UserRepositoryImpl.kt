package ru.mishlachok.LMessageClient.data.repository
import io.ktor.client.statement.*
import io.ktor.http.*
import ru.mishlachok.LMessageClient.data.remote.ApiService
import ru.mishlachok.LMessageClient.data.remote.dto.UserDto
import ru.mishlachok.LMessageClient.data.remote.toDomain
import ru.mishlachok.LMessageClient.domain.repository.UserRepository
import ru.mishlachok.LMessageClient.domain.model.User
import java.io.File
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
	private val api: ApiService
) : UserRepository {
	override suspend fun getMe(): Result<User> = try {
		val user: UserDto = api.get("/api/user/me")
		Result.success(user.toDomain())
	} catch (e: Exception) { Result.failure(e) }

	override suspend fun updateOnlineStatus(online: Boolean): Result<Boolean> = try {
		val resp: HttpResponse = api.put("/api/user/status", mapOf("online" to online.toString()))
		if (resp.status.isSuccess()) Result.success(true)
		else Result.failure(Exception("Failed to update status"))
	} catch (e: Exception) { Result.failure(e) }

	override suspend fun updateAvatar(avatarPath: String): Result<String> = try {
		val file = File(avatarPath)
		val resp: Map<String, String> = api.postMultipart("/api/user/avatar", listOf(
			PartData("avatar", file.name, file.readBytes(), ContentType.Image.JPEG.toString())
		))
		Result.success(resp["avatarUrl"] ?: throw Exception("Avatar upload failed"))
	} catch (e: Exception) { Result.failure(e) }
	override suspend fun searchUsers(query: String): Result<List<User>> = try {
		val users: List<UserDto> = api.get("/api/users/search", mapOf("query" to query))
		Result.success(users.map { it.toDomain() })
	} catch (e: Exception) {
		Result.failure(e)
	}
}
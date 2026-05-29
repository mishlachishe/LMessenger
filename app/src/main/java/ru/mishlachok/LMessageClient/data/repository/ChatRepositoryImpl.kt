package ru.mishlachok.LMessageClient.data.repository

import ru.mishlachok.LMessageClient.data.remote.ApiService
import ru.mishlachok.LMessageClient.data.remote.dto.AddMemberRequest
import ru.mishlachok.LMessageClient.data.remote.dto.AddMemberResponse
import ru.mishlachok.LMessageClient.data.remote.dto.ChatDto
import ru.mishlachok.LMessageClient.data.remote.dto.CreateGroupRequest
import ru.mishlachok.LMessageClient.data.remote.dto.DirectChatRequest
import ru.mishlachok.LMessageClient.data.remote.toDomain
import ru.mishlachok.LMessageClient.domain.model.Chat
import ru.mishlachok.LMessageClient.domain.repository.ChatRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
	private val api: ApiService
) : ChatRepository {

	override suspend fun getChats(): Result<List<Chat>> = try {
		val list: List<ChatDto> = api.get("/api/chats")
		Result.success(list.map { it.toDomain() })
	} catch (e: Exception) { Result.failure(e) }

	override suspend fun getChat(chatId: Long): Result<Chat> = try {
		val chat: ChatDto = api.get("/api/chats/$chatId")
		Result.success(chat.toDomain())
	} catch (e: Exception) { Result.failure(e) }

	override suspend fun createGroupChat(name: String, memberIds: List<Long>): Result<Chat> = try {
		val chat: ChatDto = api.post("/api/chats/group", CreateGroupRequest(name, memberIds))
		Result.success(chat.toDomain())
	} catch (e: Exception) { Result.failure(e) }

	override suspend fun createDirectChat(userId: Long): Result<Chat> = try {
		val chat: ChatDto = api.post("/api/chats/direct", DirectChatRequest(userId))
		Result.success(chat.toDomain())
	} catch (e: Exception) { Result.failure(e) }

	override suspend fun addMember(chatId: Long, userId: Long): Result<Boolean> = try {
		val response: AddMemberResponse = api.post("/api/chats/$chatId/members", AddMemberRequest(userId))
		Result.success(response.success)
	} catch (e: Exception) { Result.failure(e) }

	override suspend fun deleteChat(chatId: Long): Result<Boolean> = try {
		val response: Map<String, Boolean> = api.delete("/api/chats/$chatId")
		Result.success(response["success"] ?: false)
	} catch (e: Exception) {
		Result.failure(e)
	}
}
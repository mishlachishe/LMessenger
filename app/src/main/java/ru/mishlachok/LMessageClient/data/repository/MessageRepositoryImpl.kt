package ru.mishlachok.LMessageClient.data.repository
import io.ktor.http.*
import ru.mishlachok.LMessageClient.data.remote.ApiService
import ru.mishlachok.LMessageClient.data.remote.dto.AttachmentDto
import ru.mishlachok.LMessageClient.data.remote.dto.MessageDto
import ru.mishlachok.LMessageClient.data.remote.dto.SendMessageRequest
import ru.mishlachok.LMessageClient.data.remote.toDomain
import ru.mishlachok.LMessageClient.domain.repository.MessageRepository
import ru.mishlachok.LMessageClient.domain.model.Message
import ru.mishlachok.LMessageClient.domain.model.Attachment
import java.io.File
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
	private val api: ApiService
) : MessageRepository {
	override suspend fun getMessages(chatId: Long, limit: Int, offset: Long): Result<List<Message>> = try {
		val list: List<MessageDto> = api.get("/api/chats/$chatId/messages", mapOf("limit" to limit.toString(), "offset" to offset.toString()))
		Result.success(list.map { it.toDomain() })
	} catch (e: Exception) { Result.failure(e) }

	override suspend fun sendMessage(chatId: Long, text: String?): Result<Message> = try {
		val msg: MessageDto = api.post("/api/messages", SendMessageRequest(chatId, text))
		Result.success(msg.toDomain())
	} catch (e: Exception) { Result.failure(e) }

	override suspend fun addAttachment(messageId: Long, filePath: String, mimeType: String?): Result<Attachment> {
		return try {
			val file = File(filePath)
			val resp: AttachmentDto = api.postMultipart("/api/messages/$messageId/attachments", listOf(
				PartData("file", file.name, file.readBytes(), mimeType ?: ContentType.Application.OctetStream.toString())
			))
			Result.success(resp.toDomain())
		} catch (e: Exception) { Result.failure(e) }
	}
}
data class PartData(val key: String, val filename: String, val bytes: ByteArray, val contentType: String) {
	val headers by lazy {
		Headers.build {
			append(HttpHeaders.ContentDisposition, "form-data; name=\"$key\"; filename=\"$filename\"")
			append(HttpHeaders.ContentType, contentType)
		}
	}
	val value by lazy { bytes }
}
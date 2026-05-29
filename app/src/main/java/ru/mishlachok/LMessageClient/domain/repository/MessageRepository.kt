package ru.mishlachok.LMessageClient.domain.repository

import ru.mishlachok.LMessageClient.domain.model.Attachment
import ru.mishlachok.LMessageClient.domain.model.Message

interface MessageRepository {
	suspend fun getMessages(chatId: Long, limit: Int = 50, offset: Long = 0): Result<List<Message>>
	suspend fun sendMessage(chatId: Long, text: String?): Result<Message>
	suspend fun addAttachment(messageId: Long, filePath: String, mimeType: String?): Result<Attachment>
}
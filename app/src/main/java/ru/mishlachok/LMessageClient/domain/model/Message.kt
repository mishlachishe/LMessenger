package ru.mishlachok.LMessageClient.domain.model

data class Attachment(
	val id: Long,
	val fileName: String,
	val mimeType: String?,
	val url: String
)

data class Message(
	val id: Long,
	val chatId: Long,
	val senderId: Long?,
	val text: String?,
	val attachments: List<Attachment>,
	val createdAt: String
)
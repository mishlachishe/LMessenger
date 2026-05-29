package ru.mishlachok.LMessageClient.data.remote.dto

import kotlinx.serialization.Serializable
@Serializable
data class MessageDto(
	val id: Long,
	val chatId: Long,
	val senderId: Long? = null,
	val text: String? = null,
	val attachments: List<AttachmentDto> = emptyList(),
	val createdAt: String
)
@Serializable
data class AttachmentDto(
	val id: Long,
	val fileName: String,
	val mimeType: String? = null,
	val url: String
)
@Serializable
data class SendMessageRequest(val chatId: Long, val text: String?)
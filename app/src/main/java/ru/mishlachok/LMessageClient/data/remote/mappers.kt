package ru.mishlachok.LMessageClient.data.remote

import ru.mishlachok.LMessageClient.data.remote.dto.AttachmentDto
import ru.mishlachok.LMessageClient.data.remote.dto.ChatDto
import ru.mishlachok.LMessageClient.data.remote.dto.MessageDto
import ru.mishlachok.LMessageClient.data.remote.dto.UserDto
import ru.mishlachok.LMessageClient.domain.model.Attachment
import ru.mishlachok.LMessageClient.domain.model.Chat
import ru.mishlachok.LMessageClient.domain.model.Message
import ru.mishlachok.LMessageClient.domain.model.User

fun UserDto.toDomain(): User = User(id, login, displayName, avatarUrl, isOnline, lastSeen)
fun ChatDto.toDomain(): Chat = Chat(id, name, isGroup, avatarUrl, members.map { it.toDomain() })
fun AttachmentDto.toDomain(): Attachment = Attachment(id, fileName, mimeType, url)
fun MessageDto.toDomain(): Message = Message(id, chatId, senderId, text, attachments.map { it.toDomain() }, createdAt)
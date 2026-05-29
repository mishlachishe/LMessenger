package ru.mishlachok.LMessageClient.domain.usecase
import ru.mishlachok.LMessageClient.domain.model.Attachment
import ru.mishlachok.LMessageClient.domain.model.Message
import ru.mishlachok.LMessageClient.domain.repository.MessageRepository
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(private val repo: MessageRepository) {
	suspend operator fun invoke(chatId: Long, limit: Int = 50, offset: Long = 0): Result<List<Message>> =
		repo.getMessages(chatId, limit, offset)
}

class SendMessageUseCase @Inject constructor(private val repo: MessageRepository) {
	suspend operator fun invoke(chatId: Long, text: String?): Result<Message> = repo.sendMessage(chatId, text)
}

class AddAttachmentUseCase @Inject constructor(private val repo: MessageRepository) {
	suspend operator fun invoke(messageId: Long, filePath: String, mimeType: String?): Result<Attachment> =
		repo.addAttachment(messageId, filePath, mimeType)
}
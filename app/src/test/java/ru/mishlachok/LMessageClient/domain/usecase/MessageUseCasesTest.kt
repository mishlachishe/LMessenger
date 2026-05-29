package ru.mishlachok.LMessageClient.domain.usecase

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.mishlachok.LMessageClient.domain.model.Attachment
import ru.mishlachok.LMessageClient.domain.model.Message
import ru.mishlachok.LMessageClient.domain.repository.MessageRepository

class MessageUseCasesTest {
    private class FakeMessageRepository : MessageRepository {
        val message = Message(
            id = 1L,
            chatId = 7L,
            senderId = 5L,
            text = "Привет",
            attachments = emptyList(),
            createdAt = "2026-05-29T12:00:00"
        )
        val attachment = Attachment(id = 10L, fileName = "file.pdf", mimeType = "application/pdf", url = "/uploads/file.pdf")

        override suspend fun getMessages(chatId: Long, limit: Int, offset: Long): Result<List<Message>> = Result.success(listOf(message.copy(chatId = chatId)))
        override suspend fun sendMessage(chatId: Long, text: String?): Result<Message> = Result.success(message.copy(chatId = chatId, text = text))
        override suspend fun addAttachment(messageId: Long, filePath: String, mimeType: String?): Result<Attachment> = Result.success(attachment.copy(mimeType = mimeType))
    }

    @Test
    fun getMessages_returnsMessagesForSelectedChat() = runTest {
        val useCase = GetMessagesUseCase(FakeMessageRepository())

        val result = useCase(chatId = 9L)

        assertTrue(result.isSuccess)
        assertEquals(9L, result.getOrNull()?.first()?.chatId)
    }

    @Test
    fun sendMessage_returnsCreatedMessageWithText() = runTest {
        val useCase = SendMessageUseCase(FakeMessageRepository())

        val result = useCase(chatId = 9L, text = "Тестовое сообщение")

        assertTrue(result.isSuccess)
        assertEquals("Тестовое сообщение", result.getOrNull()?.text)
    }

    @Test
    fun addAttachment_returnsUploadedFileDescriptor() = runTest {
        val useCase = AddAttachmentUseCase(FakeMessageRepository())

        val result = useCase(messageId = 1L, filePath = "/tmp/file.pdf", mimeType = "application/pdf")

        assertTrue(result.isSuccess)
        assertEquals("file.pdf", result.getOrNull()?.fileName)
    }
}
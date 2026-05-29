package ru.mishlachok.LMessageClient.domain.usecase

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.mishlachok.LMessageClient.domain.model.Chat
import ru.mishlachok.LMessageClient.domain.repository.ChatRepository

class ChatUseCasesTest {
	private class FakeChatRepository : ChatRepository {
		val directChat = Chat(1L, null, false, null, emptyList())
		val groupChat = Chat(2L, "Учебная группа", true, null, emptyList())
		var deletedChatId: Long? = null
		var addMemberResult: Result<Boolean> = Result.success(true)

		override suspend fun getChats(): Result<List<Chat>> =
			Result.success(listOf(directChat, groupChat))

		override suspend fun getChat(chatId: Long): Result<Chat> =
			Result.success(if (chatId == 1L) directChat else groupChat)

		override suspend fun createGroupChat(name: String, memberIds: List<Long>): Result<Chat> =
			Result.success(groupChat.copy(name = name))

		override suspend fun createDirectChat(userId: Long): Result<Chat> =
			Result.success(directChat)

		override suspend fun addMember(chatId: Long, userId: Long): Result<Boolean> =
			addMemberResult

		override suspend fun deleteChat(chatId: Long): Result<Boolean> {
			deletedChatId = chatId
			return Result.success(true)
		}
	}

	@Test
	fun getChats_returnsAllUserChats() = runTest {
		val useCase = GetChatsUseCase(FakeChatRepository())
		val result = useCase()

		assertTrue(result.isSuccess)
		assertEquals(2, result.getOrNull()?.size)
	}

	@Test
	fun createDirectChat_returnsPersonalChat() = runTest {
		val useCase = CreateDirectChatUseCase(FakeChatRepository())
		val result = useCase(10L)

		assertTrue(result.isSuccess)
		assertEquals(false, result.getOrNull()?.isGroup)
	}

	@Test
	fun createGroupChat_usesProvidedName() = runTest {
		val useCase = CreateGroupChatUseCase(FakeChatRepository())
		val result = useCase("Проект", listOf(2L, 3L))

		assertTrue(result.isSuccess)
		assertEquals("Проект", result.getOrNull()?.name)
	}

	@Test
	fun deleteChat_passesChatIdToRepository() = runTest {
		val repository = FakeChatRepository()
		val useCase = DeleteChatUseCase(repository)
		val result = useCase(15L)

		assertTrue(result.getOrNull() == true)
		assertEquals(15L, repository.deletedChatId)
	}
}
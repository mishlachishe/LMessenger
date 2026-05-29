package ru.mishlachok.LMessageClient.domain.usecase
import ru.mishlachok.LMessageClient.domain.model.Chat
import ru.mishlachok.LMessageClient.domain.repository.ChatRepository
import javax.inject.Inject

class GetChatsUseCase @Inject constructor(private val repo: ChatRepository) {
	suspend operator fun invoke(): Result<List<Chat>> = repo.getChats()
}

class GetChatUseCase @Inject constructor(private val repo: ChatRepository) {
	suspend operator fun invoke(chatId: Long): Result<Chat> = repo.getChat(chatId)
}

class CreateGroupChatUseCase @Inject constructor(
	private val repo: ChatRepository
) {
	suspend operator fun invoke(name: String, memberIds: List<Long>): Result<Chat> = repo.createGroupChat(name, memberIds)
}
class CreateDirectChatUseCase @Inject constructor(
	private val repo: ChatRepository
) {
	suspend operator fun invoke(userId: Long): Result<Chat> = repo.createDirectChat(userId)
}

class AddMemberUseCase @Inject constructor(private val repo: ChatRepository) {
	suspend operator fun invoke(chatId: Long, userId: Long): Result<Boolean> = repo.addMember(chatId, userId)
}
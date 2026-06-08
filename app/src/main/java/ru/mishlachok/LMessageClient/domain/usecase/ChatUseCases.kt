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

/*class CreateGroupChatUseCase @Inject constructor(
	private val repo: ChatRepository
) {
	suspend operator fun invoke(name: String, memberIds: List<Long>): Result<Chat> = repo.createGroupChat(name, memberIds)
}*/
class CreateGroupChatUseCase @Inject constructor(private val repo: ChatRepository) {
	suspend operator fun invoke(name: String, memberLogins: List<String>): Result<Chat> =
		repo.createGroupChat(name, memberLogins)
}
/*class CreateDirectChatUseCase @Inject constructor(
	private val repo: ChatRepository
) {
	suspend operator fun invoke(userId: Long): Result<Chat> = repo.createDirectChat(userId)
}*/
class CreateDirectChatUseCase @Inject constructor(private val repo: ChatRepository) {
	suspend operator fun invoke(login: String): Result<Chat> = repo.createDirectChat(login)
}

/*class AddMemberUseCase @Inject constructor(private val repo: ChatRepository) {
	suspend operator fun invoke(chatId: Long, userId: Long): Result<Boolean> = repo.addMember(chatId, userId)
}*/
class AddMemberUseCase @Inject constructor(private val repo: ChatRepository) {
	suspend operator fun invoke(chatId: Long, login: String): Result<Boolean> =
		repo.addMember(chatId, login)
}
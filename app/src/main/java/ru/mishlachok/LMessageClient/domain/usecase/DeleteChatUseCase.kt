package ru.mishlachok.LMessageClient.domain.usecase

import ru.mishlachok.LMessageClient.domain.repository.ChatRepository
import javax.inject.Inject

class DeleteChatUseCase @Inject constructor(
	private val repository: ChatRepository
) {
	suspend operator fun invoke(chatId: Long): Result<Boolean> = repository.deleteChat(chatId)
}
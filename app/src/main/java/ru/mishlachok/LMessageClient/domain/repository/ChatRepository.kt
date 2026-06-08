package ru.mishlachok.LMessageClient.domain.repository

import ru.mishlachok.LMessageClient.domain.model.Chat

interface ChatRepository {
	suspend fun getChats(): Result<List<Chat>>
	suspend fun getChat(chatId: Long): Result<Chat>
	//suspend fun createGroupChat(name: String, memberIds: List<Long>): Result<Chat>
	suspend fun createGroupChat(name: String, memberLogins: List<String>): Result<Chat>
	//suspend fun createDirectChat(userId: Long): Result<Chat>
	suspend fun createDirectChat(userLogin: String): Result<Chat>
	//suspend fun addMember(chatId: Long, userId: Long): Result<Boolean>
	suspend fun addMember(chatId: Long, userLogin: String): Result<Boolean>
	suspend fun deleteChat(chatId: Long): Result<Boolean>
}
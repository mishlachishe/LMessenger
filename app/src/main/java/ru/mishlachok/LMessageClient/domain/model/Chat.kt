package ru.mishlachok.LMessageClient.domain.model

data class Chat(
	val id: Long,
	val name: String?,
	val isGroup: Boolean,
	val avatarUrl: String?,
	val members: List<User>,
	val lastMessage: String? = null
)

package ru.mishlachok.LMessageClient.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatDto(
	val id: Long,
	val name: String? = null,
	val isGroup: Boolean,
	val avatarUrl: String? = null,
	val members: List<UserDto>
)

@Serializable
data class CreateGroupRequest(val name: String, val members: List<Long>)

@Serializable
data class AddMemberRequest(val userId: Long)

@Serializable
data class AddMemberResponse(val success: Boolean)
@Serializable
data class DirectChatRequest(val userId: Long)
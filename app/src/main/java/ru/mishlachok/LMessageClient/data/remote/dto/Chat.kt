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
//data class CreateGroupRequest(val name: String, val members: List<Long>)
data class CreateGroupRequest(val name: String, val members: List<String>)
@Serializable
//data class AddMemberRequest(val userId: Long)
data class AddMemberRequest(val userLogin: String)

@Serializable
data class AddMemberResponse(val success: Boolean)
@Serializable
//data class DirectChatRequest(val userId: Long)
data class DirectChatRequest(val userLogin: String)
package ru.mishlachok.LMessageClient.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
	val id: Long,
	val login: String,
	val displayName: String,
	val avatarUrl: String? = null,
	val isOnline: Boolean,
	val lastSeen: String
)
@Serializable
data class StatusResponse(val success: Boolean)
package ru.mishlachok.LMessageClient.domain.model

data class User(
	val id: Long,
	val login: String,
	val displayName: String,
	val avatarUrl: String?,
	val isOnline: Boolean,
	val lastSeen: String
)
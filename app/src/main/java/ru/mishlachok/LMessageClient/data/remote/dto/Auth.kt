package ru.mishlachok.LMessageClient.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val login: String, val password: String)
@Serializable
data class RegisterRequest(val login: String, val password: String, val displayName: String)
@Serializable
data class AuthResponse(val token: String? = null, val error: String? = null)
package ru.mishlachok.LMessageClient.domain.usecase

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.mishlachok.LMessageClient.domain.repository.AuthRepository

class AuthUseCaseTest {
    private class FakeAuthRepository : AuthRepository {
        var loginResult: Result<String> = Result.success("jwt-token")
        var registerResult: Result<Long> = Result.success(10L)
        var logoutCalled = false

        override suspend fun register(login: String, password: String, displayName: String): Result<Long> = registerResult
        override suspend fun login(login: String, password: String): Result<String> = loginResult
        override suspend fun logout() { logoutCalled = true }
    }

    @Test
    fun login_returnsToken_whenRepositorySuccess() = runTest {
        val repository = FakeAuthRepository()
        val useCase = LoginUseCase(repository)

        val result = useCase("mishlachok", "password123")

        assertTrue(result.isSuccess)
        assertEquals("jwt-token", result.getOrNull())
    }

    @Test
    fun login_returnsFailure_whenRepositoryFailure() = runTest {
        val repository = FakeAuthRepository().apply {
            loginResult = Result.failure(IllegalArgumentException("Invalid credentials"))
        }
        val useCase = LoginUseCase(repository)

        val result = useCase("mishlachok", "wrong")

        assertTrue(result.isFailure)
        assertEquals("Invalid credentials", result.exceptionOrNull()?.message)
    }

    @Test
    fun register_returnsUserId_whenInputIsValid() = runTest {
        val repository = FakeAuthRepository()
        val useCase = RegisterUseCase(repository)

        val result = useCase("mishlachok", "password123", "Михаил")

        assertTrue(result.isSuccess)
        assertEquals(10L, result.getOrNull())
    }

    @Test
    fun logout_delegatesCallToRepository() = runTest {
        val repository = FakeAuthRepository()
        val useCase = LogoutUseCase(repository)

        useCase()

        assertTrue(repository.logoutCalled)
    }
}
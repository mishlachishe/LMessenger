package ru.mishlachok.LMessageClient.domain.usecase
import ru.mishlachok.LMessageClient.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
	private val repository: AuthRepository
) {
	suspend operator fun invoke(login: String, password: String, displayName: String): Result<String> =
		repository.register(login, password, displayName)
}
class LoginUseCase @Inject constructor(
	private val repository: AuthRepository
) {
	suspend operator fun invoke(login: String, password: String): Result<String> =
		repository.login(login, password)
}

class LogoutUseCase @Inject constructor(
	private val repository: AuthRepository
) {
	suspend operator fun invoke() = repository.logout()
}
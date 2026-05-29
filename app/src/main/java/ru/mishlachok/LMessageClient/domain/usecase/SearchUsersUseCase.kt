package ru.mishlachok.LMessageClient.domain.usecase

import ru.mishlachok.LMessageClient.domain.model.User
import ru.mishlachok.LMessageClient.domain.repository.UserRepository
import javax.inject.Inject

class SearchUsersUseCase @Inject constructor(
	private val repository: UserRepository
) {
	suspend operator fun invoke(query: String): Result<List<User>> = repository.searchUsers(query)
}
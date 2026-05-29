package ru.mishlachok.LMessageClient.domain.usecase
import ru.mishlachok.LMessageClient.domain.repository.UserRepository
import javax.inject.Inject

class GetMeUseCase @Inject constructor(private val repo: UserRepository) {
	suspend operator fun invoke() = repo.getMe()
}

class UpdateOnlineStatusUseCase @Inject constructor(private val repo: UserRepository) {
	suspend operator fun invoke(online: Boolean) = repo.updateOnlineStatus(online)
}

class UpdateAvatarUseCase @Inject constructor(private val repo: UserRepository) {
	suspend operator fun invoke(avatarPath: String) = repo.updateAvatar(avatarPath)
}
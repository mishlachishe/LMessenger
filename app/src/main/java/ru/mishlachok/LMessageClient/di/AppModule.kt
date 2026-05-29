package ru.mishlachok.LMessageClient.di
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.mishlachok.LMessageClient.data.repository.AuthRepositoryImpl
import ru.mishlachok.LMessageClient.data.repository.ChatRepositoryImpl
import ru.mishlachok.LMessageClient.data.repository.MessageRepositoryImpl
import ru.mishlachok.LMessageClient.data.repository.UserRepositoryImpl
import ru.mishlachok.LMessageClient.domain.repository.AuthRepository
import ru.mishlachok.LMessageClient.domain.repository.ChatRepository
import ru.mishlachok.LMessageClient.domain.repository.MessageRepository
import ru.mishlachok.LMessageClient.domain.repository.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

	@Provides
	@Singleton
	fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

	@Provides
	@Singleton
	fun provideChatRepository(impl: ChatRepositoryImpl): ChatRepository = impl

	@Provides
	@Singleton
	fun provideMessageRepository(impl: MessageRepositoryImpl): MessageRepository = impl

	@Provides
	@Singleton
	fun provideUserRepository(impl: UserRepositoryImpl): UserRepository = impl
}
package tech.alt255.research.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tech.alt255.research.data.local.LocalUserDataSource
import tech.alt255.research.data.local.dao.UserDao
import tech.alt255.research.data.remote.AuthService
import tech.alt255.research.data.remote.QuestService
import tech.alt255.research.data.remote.RatingService
import tech.alt255.research.data.remote.UserService
import tech.alt255.research.domain.repository.AuthRepository
import tech.alt255.research.domain.repository.QuestRepository
import tech.alt255.research.domain.repository.RatingRepository
import tech.alt255.research.domain.repository.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        authService: AuthService,
        securePrefs: tech.alt255.research.data.local.SecurePrefs
    ): AuthRepository {
        return AuthRepository(authService, securePrefs)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        userService: UserService,
        questRepository: QuestRepository
    ): UserRepository {
        return UserRepository(userService, questRepository)
    }

    @Provides
    @Singleton
    fun provideQuestRepository(
        questService: QuestService
    ): QuestRepository {
        return QuestRepository(questService)
    }

    @Provides
    @Singleton
    fun provideRatingRepository(
        ratingService: RatingService
    ): RatingRepository {
        return RatingRepository(ratingService)
    }

    @Provides
    @Singleton
    fun provideLocalUserDataSource(userDao: UserDao): LocalUserDataSource {
        return LocalUserDataSource(userDao)
    }
}
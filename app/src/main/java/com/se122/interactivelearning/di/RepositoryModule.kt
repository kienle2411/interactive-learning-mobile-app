package com.se122.interactivelearning.di

import com.se122.interactivelearning.domain.repository.ClassroomRepository
import com.se122.interactivelearning.data.repository.ClassroomRepositoryImpl
import com.se122.interactivelearning.domain.repository.FileRepository
import com.se122.interactivelearning.data.repository.FileRepositoryImpl
import com.se122.interactivelearning.domain.repository.LoginRepository
import com.se122.interactivelearning.data.repository.LoginRepositoryImpl
import com.se122.interactivelearning.domain.repository.MeetingRepository
import com.se122.interactivelearning.data.repository.MeetingRepositoryImpl
import com.se122.interactivelearning.data.repository.QuizRepositoryImpl
import com.se122.interactivelearning.data.repository.QuizSocketRepositoryImpl
import com.se122.interactivelearning.data.repository.SessionRepositoryImpl
import com.se122.interactivelearning.data.repository.SessionSocketRepositoryImpl
import com.se122.interactivelearning.domain.repository.UserRepository
import com.se122.interactivelearning.data.repository.UserRepositoryImpl
import com.se122.interactivelearning.domain.repository.QuizRepository
import com.se122.interactivelearning.domain.repository.QuizSocketRepository
import com.se122.interactivelearning.domain.repository.SessionRepository
import com.se122.interactivelearning.domain.repository.SessionSocketRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindLoginRepository(
        loginRepositoryImpl: LoginRepositoryImpl
    ): LoginRepository

    @Binds
    @Singleton
    abstract fun bindClassroomRepository(
        classroomRepositoryImpl: ClassroomRepositoryImpl
    ): ClassroomRepository

    @Binds
    @Singleton
    abstract fun bindFileRepository(
        fileRepositoryImpl: FileRepositoryImpl
    ): FileRepository

    @Binds
    @Singleton
    abstract fun bindMeetingRepository(
        meetingRepositoryImpl: MeetingRepositoryImpl
    ): MeetingRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindSessionSocketRepository(
        sessionSocketRepositoryImpl: SessionSocketRepositoryImpl
    ): SessionSocketRepository

    @Binds
    @Singleton
    abstract fun bindQuizSocketRepository(
        quizSocketRepositoryImpl: QuizSocketRepositoryImpl
    ): QuizSocketRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(
        sessionRepositoryImpl: SessionRepositoryImpl
    ): SessionRepository

    @Binds
    @Singleton
    abstract fun bindQuizRepository(
        quizRepositoryImpl: QuizRepositoryImpl
    ): QuizRepository
}
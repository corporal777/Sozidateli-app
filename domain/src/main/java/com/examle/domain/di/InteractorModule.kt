package com.examle.domain.di

import com.examle.domain.interactor.AuthInteractor
import com.examle.domain.interactor.EventDetailInteractor
import com.examle.domain.interactor.EventInteractor
import com.examle.domain.interactor.UserInteractor
import com.examle.domain.repository.AuthRepository
import com.examle.domain.repository.EventRepository
import com.examle.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class InteractorModule {

    @Provides
    @Singleton
    fun provideAuthInteractor(repository: AuthRepository): AuthInteractor = AuthInteractor(repository)

    @Provides
    @Singleton
    fun provideEventInteractor(repository: EventRepository): EventInteractor = EventInteractor(repository)

    @Provides
    @Singleton
    fun provideEventDetailInteractor(repository: EventRepository): EventDetailInteractor = EventDetailInteractor(repository)


    @Provides
    @Singleton
    fun provideUserInteractor(repository: UserRepository): UserInteractor = UserInteractor(repository)
}
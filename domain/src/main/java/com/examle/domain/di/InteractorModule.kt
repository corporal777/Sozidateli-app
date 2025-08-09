package com.examle.domain.di

import android.app.Application
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.net.NetworkInfo
import com.examle.domain.interactor.AuthInteractor
import com.examle.domain.interactor.EventInteractor
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
    fun provideAuthInteractor(repository: AuthRepositoryNew): AuthInteractor = AuthInteractor(repository)

    @Provides
    @Singleton
    fun provideEventInteractor(repository: EventRepositoryNew): EventInteractor = EventInteractor(repository)
}
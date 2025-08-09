package com.examle.data.di

import android.app.Application
import android.content.Context
import com.examle.data.AppData
import com.examle.data.repository.AppPrefsImpl
import com.examle.domain.di.InteractorModule
import com.examle.domain.repository.AppPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppDataModule {

    @Provides
    @Singleton
    fun provideAppData(appPrefs: AppPrefs): AppData = AppData(appPrefs)

    @Provides
    @Singleton
    fun provideAppPrefs(context: Context): AppPrefs = AppPrefsImpl(context)
}
package com.example.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides

@Module(includes = [RepositoryModule::class])
class AppModule {

    @Provides
    fun provideContext(app: Application): Context = app

}
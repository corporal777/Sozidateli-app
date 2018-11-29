package com.example.di

import android.app.Application
import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides

@Module(includes = [RepositoryModule::class])
class AppModule {

    @Provides
    fun provideContext(app: Application): Context = app

    @Provides
    fun provideFireStore() = FirebaseFirestore.getInstance()
}
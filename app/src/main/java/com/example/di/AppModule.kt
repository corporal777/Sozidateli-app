package com.example.di

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.provider.Settings
import com.example.data.AppData
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [RepositoryModule::class])
class AppModule {

    @Provides
    fun provideContext(app: Application): Context = app

    @Provides
    fun provideFireStore() = FirebaseFirestore.getInstance()

    @SuppressLint("HardwareIds")
    @Provides
    @Singleton
    fun provideAppData(context: Context): AppData = AppData.apply {
        uid = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
}
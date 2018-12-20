package com.example.di

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.provider.Settings
import com.example.R
import com.example.data.AppData
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.android.support.AndroidSupportInjectionModule
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import javax.inject.Singleton

@Module(includes = [AndroidSupportInjectionModule::class, RepositoryModule::class])
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

    @Provides
    @Singleton
    fun provideCalligraphyDefaultConfig(): CalligraphyConfig {
        return CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
    }
}
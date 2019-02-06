package com.example.di

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.provider.Settings
import com.example.R
import com.example.data.AppData
import com.example.data.prefs.AppPrefs
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.android.support.AndroidSupportInjectionModule
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import javax.inject.Singleton

@Module(includes = [RepositoryModule::class, AndroidSupportInjectionModule::class, RetrofitModule::class])
class AppModule {

    @Provides
    fun provideContext(app: Application): Context = app

    @Provides
    fun provideFireStore() = FirebaseFirestore.getInstance()

    @SuppressLint("HardwareIds")
    @Provides
    @Singleton
    fun provideAppData(context: Context,appPrefs: AppPrefs): AppData = AppData.apply {
        uid = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        token = "595a687c6b1c69de065a549072ae4737"
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
package com.example.di

import android.app.Application
import android.content.Context
import com.example.R
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.prefs.AppPrefs
import com.example.util.chat.ChatNotificationHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.android.support.AndroidSupportInjectionModule
import ru.houseofapps.chat.ChatRepository
import ru.houseofapps.chat.SocketRepository
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import javax.inject.Singleton

@Module(includes = [RepositoryModule::class, AndroidSupportInjectionModule::class, RetrofitModule::class])
class AppModule {

    @Provides
    fun provideContext(app: Application): Context = app

    @Provides
    fun provideFireStore() = FirebaseFirestore.getInstance()

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAppData(appPrefs: AppPrefs): AppData = AppData(appPrefs)

    @Provides
    @Singleton
    fun provideUserEventData(): UserEventData = UserEventData()

    @Provides
    @Singleton
    fun provideChatData(context: Context): ChatNotificationHelper = ChatNotificationHelper(context)

    @Provides
    @Singleton
    fun provideSocketRepository(context: Context): SocketRepository = SocketRepository.getInstance(context)

    @Provides
    @Singleton
    fun provideChatRepository(context: Context): ChatRepository = ChatRepository(context)

    @Provides
    @Singleton
    fun provideCalligraphyDefaultConfig(): CalligraphyConfig {
        return CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
    }
}
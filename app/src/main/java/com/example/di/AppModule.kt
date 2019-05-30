package com.example.di

import android.app.Application
import android.content.Context
import com.example.R
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.database.Db
import com.example.data.prefs.AppPrefs
import com.example.util.ConnnectivityHelper
import com.example.util.chat.ChatHelper
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.android.support.AndroidSupportInjectionModule
import ru.houseofapps.chat.HAChat
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import javax.inject.Singleton

@Module(includes = [RepositoryModule::class, AndroidSupportInjectionModule::class, RetrofitModule::class,DataDataRetrofitModule::class])
class AppModule {

    @Provides
    fun provideContext(app: Application): Context = app

    @Provides
    fun provideFireStore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideAppData(appPrefs: AppPrefs): AppData = AppData(appPrefs)

    @Provides
    @Singleton
    fun provideDB(context: Context): Db = Db.getInstance(context)

    @Provides
    @Singleton
    fun provideConnnectivityHelper(context: Context): ConnnectivityHelper = ConnnectivityHelper(context)

    @Provides
    @Singleton
    fun provideUserEventData(): UserEventData = UserEventData()

    @Provides
    @Singleton
    fun provideChatData(context: Context): ChatHelper = ChatHelper(context)

    @Provides
    @Singleton
    fun provideHAChat(context: Context): HAChat = HAChat.getInstance(context)

    @Provides
    @Singleton
    fun provideCalligraphyDefaultConfig(): CalligraphyConfig {
        return CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
    }
}
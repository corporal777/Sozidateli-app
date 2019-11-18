package com.example.di

import android.app.Application
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.net.NetworkInfo
import com.example.R
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.database.Db
import com.example.data.prefs.AppPrefs
import com.example.repository.EventRepository
import com.example.ui.snAuth.SnAuthManager
import com.example.util.ChatHelper
import com.example.util.NotificationUtil
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.android.support.AndroidSupportInjectionModule
import io.reactivex.Observable
import ru.houseofapps.chat.HAChat
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import javax.inject.Singleton

@Module(includes = [RepositoryModule::class, AndroidSupportInjectionModule::class, RetrofitModule::class, DataDataRetrofitModule::class])
class AppModule {

    @Provides
    fun provideContext(app: Application): Context = app

    @Provides
    @Singleton
    fun provideAppData(appPrefs: AppPrefs): AppData = AppData(appPrefs)

    @Provides
    @Singleton
    fun provideDB(context: Context): Db = Db.getInstance(context)

    @Provides
    @Singleton
    fun provideUserEventData(eventRepository: EventRepository, db: Db): UserEventData = UserEventData(eventRepository, db.userEventDao())

    @Provides
    @Singleton
    fun provideChatData(context: Context, notificationUtil: NotificationUtil): ChatHelper = ChatHelper(context, notificationUtil)

    @Provides
    @Singleton
    fun provideHAChat(context: Context): HAChat = HAChat.getInstance(context)

    @Provides
    @Singleton
    fun provideCalligraphyDefaultConfig(): CalligraphyConfig {
        return CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
    }

    @Provides
    @Connectivity
    fun provideConnectivityObservable(context: Context): Observable<Boolean> =
            ReactiveNetwork.observeNetworkConnectivity(context)
                    .map { it.state() == NetworkInfo.State.CONNECTED }
                    .share()

    @Singleton
    @Provides
    fun providesSnAuthManager(context: Context) = SnAuthManager(context)

    @Provides
    fun providesContentResolver(context: Context): ContentResolver = context.contentResolver

    @Provides
    fun providesNotificationManager(context: Context): NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @Provides
    fun providesEventLocationAlarmHelper(context: Context): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
}
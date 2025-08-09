package com.example.di.module

import android.app.Application
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.net.NetworkInfo
import com.examle.data.di.RetrofitModule
import com.examle.domain.di.InteractorModule
import com.example.app.R
import com.examle.data.AppData
import com.examle.data.di.AppDataModule
import com.examle.data.di.RepositoryModule
import com.example.data.StatesData
import com.example.data.UserEventData
import com.example.data.database.Db
import com.example.di.Connectivity
import com.examle.domain.repository.EventRepository
import com.example.util.SnAuthManager
import com.example.util.ChatHelper
import com.example.util.ConnectivityProvider
import com.example.util.NotificationUtil
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.reactivex.Observable
import javax.inject.Singleton

@Module(includes = [RepositoryModule::class, RetrofitModule::class, AppDataModule::class, InteractorModule::class])
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideContext(app: Application): Context = app


    @Provides
    @Singleton
    fun provideStatesData(): StatesData = StatesData()

    @Provides
    @Singleton
    fun provideDB(context: Context): Db = Db.getInstance(context)

    @Provides
    @Singleton
    fun provideUserEventData(eventRepository: EventRepository, db: Db): UserEventData =
        UserEventData(eventRepository, db.userEventDao(), db.eventMemberDao(), db.userQrImageDao())

    @Provides
    @Singleton
    fun provideChatData(context: Context, notificationUtil: NotificationUtil): ChatHelper =
        ChatHelper(context, notificationUtil)

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

    @Provides
    fun provideConnectivityProvider(context: Context): ConnectivityProvider =
        ConnectivityProvider(context)

    @Singleton
    @Provides
    fun providesSnAuthManager(context: Context) = SnAuthManager(context)

    @Provides
    fun providesContentResolver(context: Context): ContentResolver = context.contentResolver

    @Provides
    fun providesNotificationManager(context: Context): NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @Provides
    fun providesEventLocationAlarmHelper(context: Context): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
}
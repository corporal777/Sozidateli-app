package com.example.di

import com.example.services.FcmMessagingService
import com.example.services.NotificationClickJobService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ServiceBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeFcmService(): FcmMessagingService

    @ContributesAndroidInjector
    abstract fun contributeNotificationService(): NotificationClickJobService
}

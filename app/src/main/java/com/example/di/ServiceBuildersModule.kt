package com.example.di

import com.example.util.FcmMessagingService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ServiceBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeFcmService(): FcmMessagingService
}

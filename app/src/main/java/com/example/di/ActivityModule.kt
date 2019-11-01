package com.example.di

import com.example.ui.image.ImageViewActivity
import com.example.ui.main.MainActivity
import com.example.ui.snAuth.SnAuthActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [MainActivityModule::class, FragmentBuildersModule::class, MainActivityProvidersModule::class])
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun contributeSnAuthActivity(): SnAuthActivity

    @ContributesAndroidInjector
    abstract fun contributeImageViewActivity(): ImageViewActivity
}
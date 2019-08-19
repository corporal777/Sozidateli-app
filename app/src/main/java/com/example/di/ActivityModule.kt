package com.example.di

import com.example.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [MainActivityModule::class, FragmentBuildersModule::class, RxTakePhotoModule::class])
    abstract fun contributeMainActivity(): MainActivity
}

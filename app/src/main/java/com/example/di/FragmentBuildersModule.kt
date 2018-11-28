package com.example.di

import com.example.ui.firstFragment.FirstFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeFirsFragment(): FirstFragment
}

package com.example.di

import androidx.fragment.app.FragmentActivity
import com.example.ui.main.MainActivity
import dagger.Binds
import dagger.Module

@Module
abstract class MainActivityModule {
    @Binds
    abstract fun providesMainActivity(activity: MainActivity): FragmentActivity
}
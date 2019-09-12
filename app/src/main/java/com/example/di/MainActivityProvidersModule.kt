package com.example.di

import androidx.fragment.app.FragmentActivity
import com.example.util.rxtakephoto.RxTakePhoto
import dagger.Module
import dagger.Provides

@Module
class MainActivityProvidersModule {

    @Provides
    fun provideRxTakePhoto(activity: FragmentActivity): RxTakePhoto {
        return RxTakePhoto(activity)
    }
}
package com.example.di

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.ui.main.MainActivity
import com.example.util.rxtakephoto.RxTakePhoto
import com.tbruyelle.rxpermissions2.RxPermissions
import dagger.Module
import dagger.Provides
import io.reactivex.subjects.CompletableSubject
import javax.inject.Singleton

@Module
class MainActivityProvidersModule {

    @Provides
    fun provideRxTakePhoto(activity: FragmentActivity): RxTakePhoto {
        return RxTakePhoto(activity)
    }

    @Provides
    fun provideRxPermissions(activity: FragmentActivity): RxPermissions {
        return RxPermissions(activity)
    }
}
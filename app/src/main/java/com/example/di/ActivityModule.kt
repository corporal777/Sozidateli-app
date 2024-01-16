package com.example.di

import com.example.ui.gallery.camera.CustomCameraActivity
import com.example.ui.image.ImageViewActivity
import com.example.ui.main.MainActivity
import com.example.ui.auth.snAuth.SnAuthActivity
import com.example.ui.gallery.cropImage.CropImageActivity
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

    @ContributesAndroidInjector
    abstract fun contributeCropImageActivity(): CropImageActivity

    @ContributesAndroidInjector
    abstract fun contributeCustomCameraActivity(): CustomCameraActivity
}
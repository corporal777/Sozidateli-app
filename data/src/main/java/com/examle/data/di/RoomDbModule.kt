package com.examle.data.di

import android.content.Context
import androidx.room.RoomDatabase
import com.examle.data.AppData
import com.examle.data.repository.AppPrefsImpl
import com.examle.data.source.room.Db
import com.examle.domain.repository.AppPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomDbModule {

    @Provides
    @Singleton
    fun provideRoomDb(context: Context): Db = Db.getInstance(context)
}
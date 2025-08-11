package com.examle.data.source.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.examle.data.source.room.dto.UserEvent
import com.examle.data.models.UserQrImage
import com.examle.data.source.room.dao.EventMemberDao
import com.examle.data.source.room.dao.UserEventDao
import com.examle.data.source.room.dao.UserQrImageDao
import com.examle.data.source.room.dto.EventMember

@Database(entities = [UserEvent::class, EventMember::class, UserQrImage::class], version = 1, exportSchema = false)
abstract class Db : RoomDatabase() {

    abstract fun userEventDao(): UserEventDao
    abstract fun eventMemberDao(): EventMemberDao
    abstract fun userQrImageDao(): UserQrImageDao

    companion object {
        @Volatile
        private var INSTANCE: Db? = null

        fun getInstance(context: Context): Db =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also {
                        INSTANCE = it
                    }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        Db::class.java, "app.db")
                        .build()
    }
}
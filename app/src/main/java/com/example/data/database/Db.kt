package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.models.UserEvent

@Database(entities = [UserEvent::class], version = 1)
abstract class Db : RoomDatabase() {

    abstract fun userEventDao(): UserEventDao

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
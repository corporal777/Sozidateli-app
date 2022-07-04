package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.database.converters.EventMemberConverter
import com.example.data.models.EventMember
import com.example.data.models.UserEvent

@Database(entities = [UserEvent::class, EventMember::class], version = 1, exportSchema = false)
abstract class Db : RoomDatabase() {

    abstract fun userEventDao(): UserEventDao
    abstract fun eventMemberDao(): EventMemberDao

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
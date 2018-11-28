package com.example.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.example.data.models.User
import com.example.data.persistence.UserDao

@Database(entities = [User::class],
        version = 1)
abstract class Db : RoomDatabase() {
    abstract fun userDao(): UserDao

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
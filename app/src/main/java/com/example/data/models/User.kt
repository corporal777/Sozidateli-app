package com.example.data.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class User(
        @PrimaryKey
        val id: Int,
        var name: String?,
        var image: String?
)
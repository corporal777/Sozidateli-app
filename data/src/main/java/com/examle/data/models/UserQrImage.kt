package com.examle.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.data.database.converters.UserEventConverter

@Entity
data class UserQrImage(
    @PrimaryKey(autoGenerate = false)
    val imageId: String,
    val imageName: String,
    val imageQrUrl: String,
    val imageData: ByteArray
)
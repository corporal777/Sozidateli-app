package com.example.data.models.user

import android.graphics.Bitmap
import com.example.data.models.Interest

data class UserData(
        var user: User,
        var avatar: Bitmap?,
        var interests: Map<Interest, List<Interest>>?
)
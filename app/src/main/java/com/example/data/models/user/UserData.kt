package com.example.data.models.user

import android.graphics.Bitmap
import com.example.data.models.Interest
import com.example.data.models.InterestNew
import com.example.data.models.UserDetail
import com.example.data.models.UserInterest

data class UserData(
        var user: /*User*/UserDetail,
        var avatar: Bitmap?,
        var interests: Map<InterestNew, List<InterestNew>>?
)
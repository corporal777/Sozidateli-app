package com.example.data.bodies

import com.google.gson.annotations.SerializedName

data class ApproveBody(
      @SerializedName("approvedBy")
      val approvedBy: Int
)
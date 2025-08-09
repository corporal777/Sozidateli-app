package com.examle.data.bodies

import com.google.gson.annotations.SerializedName

data class ApproveBody(
      @SerializedName("approvedBy")
      val approvedBy: Int
)
package com.example.data.models

import com.google.gson.annotations.SerializedName

data class NotFilledFields(
       @SerializedName("field")
       val field: String? = null,
       @SerializedName("title")
       val title: String? = null,
       @SerializedName("filled")
       val filled: Boolean? = false
)
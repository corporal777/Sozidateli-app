package com.example.data.models

import com.google.gson.annotations.SerializedName

data class AddressResponse(
        val index: String? = null,
        val country: String? = null,
        val federal: String? = null,
        val region: String? = null,
        val area: String? = null,
        val city: String? = null,
        val lon: Double? = null,
        val lat: Double? = null,
        val settlement: String? = null,
        val street: String? = null,
        val house: String? = null,
        val flat: String? = null,
        @SerializedName("fiasId")
        val fiasId: String? = null,
        @SerializedName("fullValue")
        val fullValue: String? = null
)
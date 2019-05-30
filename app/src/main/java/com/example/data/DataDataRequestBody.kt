package com.example.data

import com.example.data.models.DataDataLocations
import retrofit2.http.Field

data class DataDataRequestBody(
        var query: String,
        val count: Int=10,
        val locations: Array<DataDataLocations> = arrayOf(DataDataLocations("*")),
        val from_bound:HashMap<String,String> = hashMapOf("value" to "city"),
        val to_bound:HashMap<String,String> = hashMapOf("value" to "city")
)
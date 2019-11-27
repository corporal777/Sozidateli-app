package com.example.data

data class DaDataRequestBody(
        val query: String,
        val count: Int = 10,
        val locations: Map<String, String> = mapOf("country" to "Россия"),
        val from_bound: Map<String, String> = mapOf("value" to "region"),
        val to_bound: Map<String, String> = mapOf("value" to "settlement")
)
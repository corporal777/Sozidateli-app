package com.example.data.models

data class DaDataResponse(
        var suggestions: List<DaDataItem>
)

data class DaDataItem(
        val value: String,
        val unrestricted_value: String,
        val data: DaDataInformation
)

data class DaDataInformation(
        val postal_code: String?,
        val country: String?,
        val federal_district: String?,
        val region: String?,
        val area: String?,
        val city: String?,
        val city_district: String?,
        val settlement: String?,
        val street: String?,
        val house: String?,
        val flat: String?,
        val fias_id: String?,
        val geo_lat: Double?,
        val geo_lon: Double?
)
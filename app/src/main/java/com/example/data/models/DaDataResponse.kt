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
        val region_with_type: String?,
        val area_with_type: String?,
        val city_with_type: String?,
        val city_district_with_type: String?,
        val settlement_with_type: String?,
        val street_with_type: String?,
        val house: String?,
        val flat: String?,
        val geo_lat: Double?,
        val geo_lon: Double?
)
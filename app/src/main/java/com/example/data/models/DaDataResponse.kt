package com.example.data.models

data class DaDataResponse(
        var suggestions: List<DaDataItem>
)

data class DaDataItem(
        var value: String,
        var unrestricted_value: String,
        var data: DaDataInformation
)

data class DaDataInformation(
        var postal_code: String?,
        var country: String?,
        var federal_district: String?,
        var region_with_type: String?,
        var area_with_type: String?,
        var city_with_type: String?,
        var city_district_with_type: String?,
        var settlement_with_type: String?,
        var street_with_type: String?,
        var house: String?,
        var flat: String?,
        var geo_lat: Double?,
        var geo_lon: Double?
)
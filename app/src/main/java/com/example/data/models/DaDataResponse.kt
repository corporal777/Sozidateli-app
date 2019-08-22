package com.example.data.models

data class DaDataResponse (
        var suggestions:List<DaDataItem>
)

 data class DaDataItem(
        val value:String,
        val unrestricted_value:String,
        val data: DataDataInformation
)

data class DataDataInformation(
        val country: String,
        val region: String,
        val city: String
)
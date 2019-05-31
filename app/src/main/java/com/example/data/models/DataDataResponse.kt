package com.example.data.models

data class DataDataResponse (
        var suggestions:List<DataDataItem>
)

 data class DataDataItem(
        val value:String,
        val unrestricted_value:String,
        val data: DataDataInformation
)

data class DataDataInformation(
        val country: String,
        val region: String,
        val city: String
)
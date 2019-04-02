package com.example.data.models

data class MapInfo(
        var event_place_id:Int,
        var event_id:Int,
        var name:String?,
        var address:String?,
        var scheme:String?,
        var scheme_descriptions:String?,
        var int_scheme:String?,
        var int_scheme_descriptions:String?,
        var geo_lat:Double,
        var geo_log:Double
)
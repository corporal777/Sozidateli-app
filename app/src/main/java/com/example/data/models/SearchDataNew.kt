package com.example.data.models

data class SearchDataNew(
    val users : SearchResponseData<SearchUserData>,
    val events : SearchResponseData<SearchEventData>
)


data class SearchResponseData<T>(
    val data: List<T>,
    val count: Int
)

data class SearchUserData(
    val user_id : Int,
    val user_name : String,
    val user_last_name : String,
    val user_middle_name : String
)


data class SearchEventData(
    val event_id : Int,
    val event_name : String
)
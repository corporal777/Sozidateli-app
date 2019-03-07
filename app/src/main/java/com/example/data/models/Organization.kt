package com.example.data.models

data class Organization(
        var id: Int,
        var name: String?,
        var description: String?,
        var logo: String?,
        var bg_image: String?,
        var status: String?,
        var is_user_subscribed: Boolean = false,
        var is_user_in_favorite: Boolean = false
)
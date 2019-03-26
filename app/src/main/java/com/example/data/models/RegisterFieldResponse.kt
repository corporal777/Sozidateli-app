package com.example.data.models

data class RegisterFieldResponse(
        var fields: ArrayList<RegisterEventField>?,
        var categories: ArrayList<Category>,
        var selectedCategory: Category?,
        var moderate_registration: Boolean,
        var user_is_verified: Boolean
)
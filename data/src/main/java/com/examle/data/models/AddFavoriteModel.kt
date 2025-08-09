package com.examle.data.models

data class AddFavoriteModel(
        val id: Long? = null,
        val user: Int? = null
)

data class AddTempFavoriteModel(
        val id: Long? = null,
        val tempUser: Int? = null
)
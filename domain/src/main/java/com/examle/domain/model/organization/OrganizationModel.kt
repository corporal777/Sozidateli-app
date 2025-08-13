package com.examle.domain.model.organization

import com.examle.domain.model.FavoriteModel

data class OrganizationModel(
    val id : Long?,
    val name : String?,
    val image : String?,
    val backgroundColor : String?,
    val userFavorite : FavoriteModel?
)
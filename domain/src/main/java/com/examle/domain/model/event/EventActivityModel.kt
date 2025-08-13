package com.examle.domain.model.event

import com.examle.domain.model.FavoriteModel


class EventActivityModel(
    val id: Int? = null,
    val createdDate: String? = null,
    val holdingDate: String? = null,
    val event: Int? = null,
    val title: String? = null,
    val description: String? = null,
    val auditorium: String? = null,
    var userFavorite: FavoriteModel? = null,
)
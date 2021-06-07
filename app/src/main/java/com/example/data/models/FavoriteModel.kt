package com.example.data.models

data class FavoriteModel(
        val id: Int? = null,
        val user: Int? = null,
        val entity: FavoriteEntityModel? = null
) {

    companion object {
        const val ORGANIZATION_FAVORITE_SORT_FIELD = "sortField"
        const val ORGANIZATION_FAVORITE_SORT_Type = "sortType"
        const val ORGANIZATION_FAVORITE_LIMIT = "limit"
        const val ORGANIZATION_FAVORITE_OFFSET = "offset"
        const val ORGANIZATION_FAVORITE_TYPE = "entityType"
        const val ORGANIZATION_FAVORITE_LOAD_MODEL = "loadModel"
        const val ORGANIZATION_FAVORITE_USER = "user"

        const val ORGANIZATION_TYPE = "organization"
    }
}

data class FavoriteEntityModel(
    val type: String? = null,
    val id: Int? = null,
    val model: OrganizationNew? = null
)

data class EventFavoriteModel(
        val id: Int? = null,
        val user: Int? = null,
        val entity: EventFavoriteEntityModel? = null
) {
    companion object {
        const val EVENT_FAVORITE_SORT_FIELD = "sortField"
        const val EVENT_FAVORITE_SORT_Type = "sortType"
        const val EVENT_FAVORITE_LIMIT = "limit"
        const val EVENT_FAVORITE_OFFSET = "offset"
        const val EVENT_FAVORITE_TYPE = "entityType"
        const val EVENT_FAVORITE_LOAD_MODEL = "loadModel"
        const val EVENT_FAVORITE_USER = "user"

        const val EVENT_TYPE = "organization"
    }
}

data class EventFavoriteEntityModel(
        val type: String? = null,
        val id: Int? = null,
        val model: EventNew? = null
)
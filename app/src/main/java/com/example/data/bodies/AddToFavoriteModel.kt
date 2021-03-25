package com.example.data.bodies

data class AddToFavoriteModel(
        val user: Int? = null,
        val entity: AddToFavoriteEntityModel? = null
)

data class AddToFavoriteEntityModel(
        val type: String? = null,
        val id: Int? = null
) {
    companion object {
        const val FAVORITE_ORGANIZATION = "organization"
        const val FAVORITE_EVENT = "event"
        const val FAVORITE_SPEAKER = "speaker"
    }
}
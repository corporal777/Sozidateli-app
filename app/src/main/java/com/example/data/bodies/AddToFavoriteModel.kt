package com.example.data.bodies

data class AddToFavoriteModel(
        val user: Int? = null,
        val entity: AddToFavoriteEntityModel? = null
){
    companion object {
        fun toBody(userId : Int, type: String?, id : Int) : AddToFavoriteModel {
            return AddToFavoriteModel(
                userId,
                AddToFavoriteEntityModel(
                    type,
                    id
                )
            )
        }

        fun toEventBody(userId : Int, eventId : String) : AddToFavoriteModel {
            return AddToFavoriteModel(
                userId, AddToFavoriteEntityModel(AddToFavoriteEntityModel.FAVORITE_EVENT, eventId.toInt())
            )
        }

        fun toOrgBody(userId : Int, orgId : Long?) : AddToFavoriteModel {
            return AddToFavoriteModel(
                userId, AddToFavoriteEntityModel(
                    AddToFavoriteEntityModel.FAVORITE_ORGANIZATION,
                    orgId?.toInt()
                )
            )
        }
    }
}

data class AddToFavoriteEntityModel(
        val type: String? = null,
        val id: Int? = null
) {
    companion object {
        const val FAVORITE_ORGANIZATION = "organization"
        const val FAVORITE_EVENT = "event"
        const val FAVORITE_SPEAKER = "speaker"
        const val FAVORITE_SUB_EVENT = "eventActivity"
    }
}
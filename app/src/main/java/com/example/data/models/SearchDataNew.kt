package com.example.data.models

data class SearchDataNew(
    val users: SearchResponseData<UserDetail>,
    val organizations: SearchResponseData<OrganizationNew>,
    val events: SearchResponseData<EventNew>
)


data class SearchResponseData<T>(
    val data: List<T>,
    val count: Int
)

data class SearchUserData(
    val user_id: Int,
    val name: String,
    val lastName: String,
    val middleName: String,
    val inFavourites: Boolean,
    val image: ImageModel? = null
) {
    val nameLastName: String
        get() {
            val nameList = listOfNotNull(
                name,
                lastName
            )
            return nameList.joinToString(" ")
        }
}

data class SearchOrganizationData(
    val id: Int,
    val legalInformation: LegalInformationModel,
    val inFavourites: Boolean,
    val logo: ImageModel? = null
)


data class SearchEventData(
    val id: Int,
    val name: String,
    val holdingDate: HoldingDateModel,
    val backgroundColor: BackgroundColorModel,
    val inFavourites: Boolean
)
package com.examle.data.models

import com.example.data.models.user.User


data class UserAddress(
    val address: String? = null,
    val index: String? = null,
    val country: String? = null,
    val federal: String? = null,
    val region: String? = null,
    val area: String? = null,
    val city: String? = null,
    val district: String? = null,
    val settlement: String? = null,
    val street: String? = null,
    val house: String? = null,
    val flat: String? = null,
    val lat: Double? = null,
    val lon: Double? = null,
    var showInProfile: Boolean? = null,
    val fullValue: String? = null,
    val shortValue: String? = null,
) {

    companion object {
        fun fromDaDataItem(item: NewUserAddress): UserAddress = UserAddress(
            item.shortAddres,
            item.index,
            item.country,
            item.federal,
            item.region,
            item.area,
            item.city,
            item.fullValue,
            item.settlement,
            item.street,
            item.house,
            item.flat,
            item.lat,
            item.lon,
            item.showInProfile
        )
    }
}

data class UserAddressBody(
    val index: String? = null,
    val region: String? = null,
    val city: String? = null,
    val fullValue: String? = null,
    val shortValue: String? = null,
    var showInProfile: Boolean? = null
)
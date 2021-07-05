package com.example.data.models

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
        val lon: Double? = null
) {

    companion object {
        fun fromUser(user: User): UserAddress = UserAddress(
                user.user_short_address ?: user.user_address,
                user.user_address_index,
                user.user_address_country,
                user.user_address_federal,
                user.user_address_region,
                user.user_address_area,
                user.user_address_city,
                user.user_address_district,
                user.user_address_settlement,
                user.user_address_street,
                user.user_address_house,
                user.user_address_flat
        )

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
                item.lon
        )
    }
}
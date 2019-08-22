package com.example.data.models

import com.example.data.models.user.User


data class UserAddress(
        val address: String? = null,
        val index: String? = null,
        val country: String? = null,
        val region: String? = null,
        val area: String? = null,
        val city: String? = null,
        val district: String? = null,
        val settlement: String? = null,
        val street: String? = null,
        val house: String? = null,
        val flat: String? = null
) {

    companion object {
        fun fromUser(user: User): UserAddress = UserAddress(
                user.user_address,
                user.user_address_index,
                user.user_address_country,
                user.user_address_region,
                user.user_address_area,
                user.user_address_city,
                user.user_address_district,
                user.user_address_settlement,
                user.user_address_street,
                user.user_address_house,
                user.user_address_flat
        )

        fun fromDaDataItem(item: DaDataItem): UserAddress = UserAddress(
                item.unrestricted_value,
                item.data.postal_code,
                item.data.country,
                item.data.region,
                item.data.area,
                item.data.city,
                item.data.city_district,
                item.data.settlement,
                item.data.street,
                item.data.house,
                item.data.flat
        )
    }
}
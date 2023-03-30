package com.example.data.models

import java.io.Serializable

sealed class SearchFilter : Serializable {

    data class Organization(
        var address: String? = null,
        var name: String? = null,
        var inn: String? = null,

        var index: String? = null,
        var country: String? = null,
        var federal: String? = null,
        var region: String? = null,
        var area: String? = null,
        var city: String? = null,
        var settlement: String? = null,
        var street: String? = null,
        var house: String? = null,
        var flat: String? = null
    ) : SearchFilter() {

        fun isHasFilter(): Boolean {
            return (!address.isNullOrEmpty() || !name.isNullOrEmpty() || !inn.isNullOrEmpty())
        }

        fun setAddressFilter(address: NewUserAddress?) {
            index = address?.index
            country = address?.country
            federal = address?.federal
            region = address?.region
            area = address?.area
            city = address?.city
            settlement = address?.settlement
            street = address?.street
            house = address?.house
            flat = address?.flat
        }
    }


    data class EventNew(
        var address: String? = null,
        var name: String? = null,
        var dateStart: String? = null,
        var dateFinish: String? = null,
        var registration: String? = null,
        var theme: Int? = null,
        var spec: Int? = null,
        var format: Int? = null,
        var customFormat: String? = null,
        var organizationId: Long? = null,
        var organizationName: String? = null,
        var fullAddress: NewUserAddress? = null
    ) : SearchFilter() {
        var interests: Map<InterestNew, List<InterestNew>>? = null
        var formats: List<NewEventFormat>? = null
        var organizations: List<OrganizationNew>? = null

        fun isHasFilter(): Boolean {
            return (!address.isNullOrEmpty() || !name.isNullOrEmpty()
                    || !dateStart.isNullOrEmpty() || !dateFinish.isNullOrEmpty()
                    || theme != null || spec != null || !customFormat.isNullOrEmpty()
                    || format != null || organizationId != null || organizationName != null)
        }

        fun getFormatName(): String? {
            return if (this.format != null) formats?.find { it.id == this.format }?.name
            else if (!customFormat.isNullOrBlank()) customFormat
            else null
        }

        fun getOrgName(): String? {
            if (organizationId != null) {
                return organizations?.find { it.id == organizationId }?.legalInformation?.name?.short
            } else if (!organizationName.isNullOrBlank()) {
                return organizationName
            } else return null
        }
    }


    data class UserNew(
        var address: String? = null,
        var theme: Int? = null,
        var spec: Int? = null,
        var ageFrom: Int? = null,
        var ageTo: Int? = null,

        var index: String? = null,
        var country: String? = null,
        var federal: String? = null,
        var region: String? = null,
        var area: String? = null,
        var city: String? = null,
        var settlement: String? = null,
        var street: String? = null,
        var house: String? = null,
        var flat: String? = null
    ) : SearchFilter() {
        var interests: Map<InterestNew, List<InterestNew>>? = null

        fun isHasFilter(): Boolean {
            return (!address.isNullOrEmpty() || ageFrom != null || ageTo != null || theme != null || spec != null)
        }

        fun setAddressFilter(address: NewUserAddress?) {
            index = address?.index
            country = address?.country
            federal = address?.federal
            region = address?.region
            area = address?.area
            city = address?.city
            settlement = address?.settlement
            street = address?.street
            house = address?.house
            flat = address?.flat
        }

        companion object {
            const val AGE_MIN = 14
            const val AGE_MAX = 80
        }
    }
}
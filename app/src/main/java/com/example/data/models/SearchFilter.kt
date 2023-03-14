package com.example.data.models

import java.io.Serializable

sealed class SearchFilter : Serializable {

    data class Organization(
        var address: String? = null,
        var name: String? = null,
        var inn: String? = null,
        var type: String? = null,
        var subscription: Boolean? = null,

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
    ) : SearchFilter()


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
        var organizations : List<OrganizationNew>? = null

        fun getFormatName() : String? {
            val format = if (format != null){
                formats?.find { it.id == format }?.name
            } else if (!customFormat.isNullOrBlank()){
                //"Искать «" + customFormat + "»"
                 customFormat
            } else null
            return format
        }

        fun getOrgName() : String?{
            if (organizationId != null){
                return organizations?.find { it.id == organizationId }?.legalInformation?.name?.short
            } else if (!organizationName.isNullOrBlank()){
                return organizationName
            } else return null
//            if (organizationId == null && !organizationName.isNullOrBlank()){
//                return "Искать «" + organizationName + "»"
//            }
//            else if (organizationId != null && !organizationName.isNullOrBlank()){
//                return organizationName
//            } else return null
        }
    }


    data class UserNew(
        var name: String? = null,
        var address: String? = null,
        var email: String? = null,
        var phone: String? = null,
        var theme: Int? = null,
        var spec: Int? = null,
        var ageFrom: Int? = null,
        var ageTo: Int? = null,
        var favorites: Boolean? = null,

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

        companion object {
            const val AGE_MIN = 14
            const val AGE_MAX = 80
        }
    }
}
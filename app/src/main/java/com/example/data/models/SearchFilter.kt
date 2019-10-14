package com.example.data.models

sealed class SearchFilter {
    data class Organization(
            var address: String? = null,
            var name: String? = null,
            var inn: String? = null,
            var type: String? = null,
            var subscription: Boolean? = null
    ) : SearchFilter()

    data class Event(
            var address: String? = null,
            var name: String? = null,
            var dateStart: String? = null,
            var dateFinish: String? = null,
            var registration: String? = null,
            var theme: Int? = null,
            var spec: Int? = null
    ) : SearchFilter() {
        var interests: Map<Interest, List<Interest>>? = null
    }

    data class User(
            var name: String? = null,
            var address: String? = null,
            var email: String? = null,
            var phone: String? = null,
            var theme: Int? = null,
            var spec: Int? = null,
            var ageFrom: Int? = null,
            var ageTo: Int? = null,
            var favorites: Boolean? = null
    ) : SearchFilter() {
        var interests: Map<Interest, List<Interest>>? = null

        companion object{
            const val AGE_MIN = 14
            const val AGE_MAX = 80
        }
    }
}
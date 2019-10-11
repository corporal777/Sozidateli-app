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
            var specialization: Int? = null,
            var theme: Int? = null
    ) : SearchFilter() {
        var interests:  Map<Interest, List<Interest>>? = null
    }

    data class User(
            var name: String? = null
    ) : SearchFilter()
}
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
            var name: String? = null
    ) : SearchFilter()

    data class User(
            var name: String? = null
    ) : SearchFilter()
}
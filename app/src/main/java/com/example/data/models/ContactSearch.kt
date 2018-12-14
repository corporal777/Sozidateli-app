package com.example.data.models

data class ContactSearch(
        val user: User,
        val contactType: Type,
        val searchText: String
) {

    enum class Type(type: String) {
        FAVORITE("favorite"),
        CHAT("chat"),
        CONTACT("contact")
    }
}
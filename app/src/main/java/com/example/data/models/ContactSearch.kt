package com.example.data.models

import com.example.data.models.user.User

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
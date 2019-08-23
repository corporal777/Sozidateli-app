package com.example.data.models

import com.example.data.models.user.UserData

data class ProfileUserData(
        private val userData: UserData,
        val editable: Boolean,
        var isEditMainData: Boolean = false,
        var isEditPersonalData: Boolean = false,
        var isEditEducationData: Boolean = false
) {
    var user
        get() = userData.user
        set(value) {
            userData.user = value
        }

    var avatar
        get() = userData.avatar
        set(value) {
            userData.avatar = value
        }

    var interests
        get() = userData.interests
        set(value) {
            userData.interests = value
        }
}
package com.example.data.models

import com.example.holders.profile.ProfileBaseFieldItem

data class ProfileFieldExpand(
        var nameField: String,
        var listOfField: MutableList<MutableList<ProfileField>>,
        var defaultFields:MutableList<ProfileField>
        )
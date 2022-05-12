package com.example.data.models

data class ProfileFieldExpand(
        var nameField: String,
        var listOfField: MutableList<MutableList<ProfileField>>,
        var defaultFields:MutableList<ProfileField>
        )
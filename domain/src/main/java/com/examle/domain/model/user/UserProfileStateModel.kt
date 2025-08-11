package com.examle.domain.model.user

data class UserProfileStateModel(
    val fields: List<UserProfileState>,
    val state: String
)

data class UserProfileState(
    val name: String,
    val filled: Boolean,
    val requiredFor: List<String>
)
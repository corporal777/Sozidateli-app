package com.example.data.models

data class RegistrationAgreementStatus(
    val status: String
) {
    fun isAccepted() = status == "accepted"
}
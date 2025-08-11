package com.examle.domain.model.event

data class EventDetailModel(
    val id: Int,
    val name: String,
    val description : String,
    val holdingDate: String,
    val requestDate: String?,
    val backgroundColor: String?,
    val status: String? = null,
    val actionStatus : EventActionStatus,
    val isStatusActionAvailable : Boolean,
    val isHasFormResult : Boolean,
    val state: EventStateModel? = null,
    val userAgreement: String? = null,
    val image: String? = null,
    val address: String? = null,
    val userRegistration: UserRegistrationModel? = null,
    val userRegistrationState: EventRegistrationModel? = null,
    val eventRegistrationState: EventRegistrationModel? = null,
)

enum class EventActionStatus {
    TEMPORARY,
    REGISTER,
    WITHDRAW,
    VIEW,
    CLOSED,
    CANCELED,
    UNSUBSCRIBE,
    SUBSCRIBE,
    NONE,
}
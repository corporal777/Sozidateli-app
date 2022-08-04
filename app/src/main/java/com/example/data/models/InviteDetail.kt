package com.example.data.models

data class InviteDetail(
    val id: Int,
    val user: Int,
    val state: String
) {
    companion object {
        fun getInviteState(inviteDetail: InviteDetail): InviteState {
            return when (inviteDetail.state) {
                "confirmed", "approved" -> InviteState.ACCEPTED
                "declined" -> InviteState.DECLINED
                "canceled" -> InviteState.CANCELED
                else -> InviteState.NONE
            }
        }
    }

    enum class InviteState {
        NONE, ACCEPTED, CANCELED, DECLINED,  DISABLED
    }
}



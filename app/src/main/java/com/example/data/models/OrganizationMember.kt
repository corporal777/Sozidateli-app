package com.example.data.models

import com.example.data.models.user.User
import com.google.gson.annotations.SerializedName

data class OrganizationMember(
        @SerializedName("member_id")
        val id: Int,
        val position: String?,
        val user: User?
) {

        companion object {
                const val MEMBERS_LIMIT = "limit"
                const val MEMBERS_OFFSET = "offset"
                const val MEMBERS_BINDS = "binds"
                const val MEMBERS_ORGANIZATION = "organization"
        }
}

data class OrganizationNewMemberModel(
        val id: Int? = null,
        @SerializedName("invitedBy")
        val invitedBy: Int? = null,
        val organization: Int? = null,
        val user: Int? = null,
        val position: OrganizationPosition? = null,
        val binds: OrganizationBinds? = null
)

data class OrganizationPosition(
        val value: String? = null,
        val manual: String? = null
)

data class OrganizationBinds(
        val user: UserDetail?
)
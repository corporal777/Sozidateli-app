package com.examle.data.models

import com.google.gson.annotations.SerializedName

data class InterestsModel(
        val data: List<InterestNew>,
        @SerializedName("totalCount")
        val totalCount: Int?
)

data class InterestNew(
        val id: Int?,
        @SerializedName("pgrfId")
        val pgrfId: String?,
        val parent: Int?,
        val name: String?,
        val order: Int?
)
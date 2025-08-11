package com.examle.data.mapper

import com.examle.data.models.UserProfileFieldsModel
import com.examle.domain.model.user.UserProfileState
import com.examle.domain.model.user.UserProfileStateModel

internal fun UserProfileFieldsModel.mapToUserProfileStateDomainModel(): UserProfileStateModel {
    return UserProfileStateModel(
        fields = fields.map {
            UserProfileState(
                it.name,
                it.filled,
                it.requiredFor
            )
        },
        state = state
    )
}
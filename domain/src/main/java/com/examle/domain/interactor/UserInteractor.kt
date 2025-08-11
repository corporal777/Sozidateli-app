package com.examle.domain.interactor

import com.examle.domain.model.user.EducationLevelModel
import com.examle.domain.model.user.UserProfileStateModel
import com.examle.domain.repository.AuthRepository
import com.examle.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class UserInteractor(private val repository: UserRepository) {

    fun checkUserProfileState(): Flow<UserProfileStateModel> {
        return repository.checkUserProfileState()
    }

    fun getUserProfileAdditionalData(): Flow<EducationLevelModel> {
        return merge(
            repository.getAcademicDegrees(),
            repository.getEducationLevel(),
            repository.getSpeciality()
        )
    }
}
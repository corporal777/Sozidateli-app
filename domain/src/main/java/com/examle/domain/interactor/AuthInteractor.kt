package com.examle.domain.interactor

import com.examle.domain.model.AuthModel
import com.examle.domain.model.TokenStatusModel
import com.examle.domain.model.body.AuthBody
import com.examle.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class AuthInteractor(private val repository: AuthRepository) {

    fun checkUserAuthFlow(): Flow<TokenStatusModel> {
        return repository.checkUserAuthFlow()
    }

    fun authEmailOrPhoneWithResult(login: AuthBody): Flow<AuthModel> =
        repository.authEmailOrPhoneWithResult(login)


    fun authEmailOrPhoneWithInvite(invite: Int, body: AuthBody): Flow<AuthModel> {
        return repository.authEmailOrPhoneWithInvite(invite, body)
    }
}
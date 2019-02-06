package com.example.repository

import com.example.api.Api
import com.example.data.models.ApiResponse
import com.example.data.models.user.User
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class AuthRepositoryImp
@Inject constructor(
        private val api: Api
) : AuthRepository {

    override fun authVk(token: String, email: String?) = callAuthAndSaveResult(api.authVk(token, email))
    override fun authFb(token: String) = callAuthAndSaveResult(api.authFb(token))
    override fun authOk(token: String) = callAuthAndSaveResult(api.authOk(token))

    override fun authEmail(email: String, password: String): Completable {
        return callAuthAndSaveResult(api.authEmail(email, password))
    }

    override fun register(email: String, password: String, name: String): Completable {
        return callAuthAndSaveResult(api.registerEmail(email, password, name, name))
    }

    private fun callAuthAndSaveResult(authRequest: Single<ApiResponse<User>>): Completable {
        return authRequest.flatMapCompletable { saveUserData(it.response) }
    }

    private fun saveUserData(user: User): Completable = Completable.complete()
}
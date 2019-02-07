package com.example.repository

import com.example.api.Api
import com.example.data.AppData
import com.example.data.models.ApiResponse
import com.example.data.models.AuthResponse
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class AuthRepositoryImp
@Inject constructor(
        appData: AppData,
        private val api: Api
) : ApiRepository(appData), AuthRepository {

    override fun authVk(token: String, email: String?) = callAuthCompletable(api.authVk(token, email))
    override fun authFb(token: String) = callAuthCompletable(api.authFb(token))
    override fun authOk(token: String) = callAuthCompletable(api.authOk(token))

    override fun authEmail(email: String, password: String): Completable {
        return callAuthCompletable(api.authEmail(email, password))
    }

    override fun register(email: String, password: String, name: String): Completable {
        return callAuthCompletable(api.registerEmail(email, password, name, name))
    }

    private fun callAuthCompletable(authRequest: Single<ApiResponse<AuthResponse>>): Completable {
        return call(authRequest).flatMapCompletable { Completable.complete() }
    }
}
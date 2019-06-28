package com.example.repository

import com.example.api.Api
import com.example.data.AppData
import com.example.data.models.ApiResponse
import com.example.data.models.AuthResponse
import com.example.data.models.AuthSNResponse
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class AuthRepositoryImp
@Inject constructor(
        appData: AppData,
        private val api: Api
) : ApiRepository(appData), AuthRepository {

    override fun authSocialNetwork(snType: String, token: String, email: String?): Single<AuthSNResponse> {
        return api.authSocialNetwork(snType, token, email)
                .doOnError { processError(it) }
                .doOnSuccess {
                    if (!it.response.user_email_not_set && it.response.user_email_confirmed) {
                        saveSession(it)
                    }
                }
                .map { it.response }
    }

    override fun setEmailSocialNetwork(snType: String, email: String, token: String): Completable {
        return callAuthCompletable(api.setEmailSocialNetwork(snType, email, token))
    }

    override fun confirmEmailSocialNetwork(snType: String, id: String, code: String): Completable {
        return callAuthCompletable(api.confirmEmailSocialNetwork(snType, id, code))
    }

    override fun authEmail(email: String, password: String): Completable {
        return callAuthCompletable(api.authEmail(email, password))
    }

    override fun register(email: String, password: String, name: String, lastName: String): Completable {
        return callAuthCompletable(api.registerEmail(email, password, name, lastName))
    }

    override fun registerConfirm(email: String, code: String): Completable {
        return callAuthCompletable(api.registerEmailConfirm(email, code))
    }

    private fun callAuthCompletable(authRequest: Single<ApiResponse<AuthResponse>>): Completable {
        return call(authRequest).flatMapCompletable { Completable.complete() }
    }

    override fun sendRecoveryEmail(email: String): Completable {
        return callAuthCompletable(api.sendEmailRecovery(email))
    }

    override fun checkRecoveryCode(email: String, code: String): Completable {
        return callAuthCompletable(api.checkRecoveryCode(email, code))
    }

    override fun setPassword(email: String, code: String, password: String): Completable {
        return callAuthCompletable(api.setPassword(email, code, password))
    }
}
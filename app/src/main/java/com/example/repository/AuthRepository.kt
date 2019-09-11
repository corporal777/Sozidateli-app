package com.example.repository

import com.example.data.models.RegisterStatus
import com.example.data.models.SnUserData
import io.reactivex.Completable
import io.reactivex.Single

interface AuthRepository {

    fun authSocialNetwork(snType: String, token: String, email: String? = null, firstName: String? = null, lastName: String? = null, password: String? = null): Completable
    fun setEmailSocialNetwork(snType: String, email: String, token: String): Completable
    fun confirmEmailSocialNetwork(snType: String, id: String, code: String): Completable

    fun authEmail(email: String, password: String): Completable
    fun register(email: String, password: String, firstName: String, lastName: String): Completable
    fun registerConfirm(email: String, code: String): Completable
    fun registerEmailResend(email: String): Completable

    fun sendRecoveryEmail(email: String): Completable
    fun checkRecoveryCode(email: String, code: String): Completable
    fun setPassword(email: String, code: String, password: String): Completable

    fun checkSnRegisterStatus(snType: String, snId: String): Single<RegisterStatus>
    fun getVkUser(): Single<SnUserData>
    fun getFbUser(): Single<SnUserData>
    fun getOkUser(): Single<SnUserData>
}
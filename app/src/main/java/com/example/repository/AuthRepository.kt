package com.example.repository

import com.example.data.bodies.AuthBody
import com.example.data.bodies.ConfirmCodeBody
import com.example.data.bodies.RecoverPasswordBody
import com.example.data.bodies.RegisterBody
import com.example.data.models.*
import com.example.data.models.user.User
import com.example.data.models.user.UserResp
import com.example.ui.snAuth.SnAuth
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Path

interface AuthRepository {

    fun authSocialNetwork(snAuth: SnAuth): Single<Pair<RegisterStatus, SnUser>>
    fun authSocialNetwork(snType: String, token: String, email: String? = null, firstName: String? = null, lastName: String? = null, password: String? = null): Completable
    fun setEmailSocialNetwork(snType: String, email: String, token: String): Completable
    fun confirmEmailSocialNetwork(id: String, code: String): Completable

    fun authEmailOrPhone(login: AuthBody): Completable
    fun register(body: RegisterBody): Completable
    fun registerConfirm(email: String, code: String, name: String,lastName: String,
                        middleName: String?, phone: String?, newEmail: String? = null, password: String? = null): Completable
    fun registerEmailResend(email: String): Completable
    fun registerPhoneResend(type: String, phone: String): Completable
    fun confirmPhone(body: ConfirmCodeBody): Completable
    //fun registerSnResend(email: String, token: String): Completable

    fun sendRecoveryEmail(email: String): Completable
    fun sendRecoveryEmail(type: String, email: String): Completable
    fun checkRecoveryCode(email: String, code: String): Completable
    fun setPassword(email: String, code: String, password: String): Completable

    fun checkRegisterStatus(snType: String?, snId: String?, email: String?): Single<RegisterStatus>
    fun getVkUser(): Single<SnUserData>
    fun getFbUser(): Single<SnUserData>
    fun getOkUser(): Single<SnUserData>
    fun registerData(email: String, code: String): Single<UserResp>
    fun checkRecoveryCodeNew(type: String, code: String): Completable
    fun recoverPasswordNew(body: RecoverPasswordBody): Completable
}
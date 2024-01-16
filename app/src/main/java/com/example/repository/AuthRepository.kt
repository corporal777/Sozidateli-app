package com.example.repository

import com.example.data.bodies.*
import com.example.data.models.*
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

interface AuthRepository {

    //fun setEmailSocialNetwork(snType: String, email: String, token: String): Completable
    fun getStories() : Maybe<List<String>>
    fun authEmailOrPhone(login: AuthBody): Completable
    fun authEmailOrPhoneWithResult(login: AuthBody):Single<AuthResponse>

    fun authEmailOrPhoneWithInvite(invite: Int, body: AuthBody): Completable

    //auth social
    fun authWithVk(token : String, uuid : String): Single<VKAuthResponse>
    fun authEmailOrPhoneWithSn(body: AuthBody, snAuth: SnAuth): Completable

    fun sendQrCode(body: QrBody):Single<QrAuthResponse>
    fun authWebWithQrCode(body: QrBody): Single<AuthResponse>
    fun registerUser(body: RegisterBody): Completable
    fun registerSnUser(body: SnRegisterBody): Completable

    fun registerEmailResend(email: String): Completable
    fun registerPhoneResend(type: String, phone: String): Completable
    fun confirmPhoneCode(body: ConfirmCodeBody): Completable
    fun confirmEmailCode(body: EmailCodeBody): Completable
    fun deleteConfirmEmail(email: String): Completable

    //fun registerSnResend(email: String, token: String): Completable

    //fun sendRecoveryEmail(email: String): Completable
    fun sendRecoveryEmail(type: String, email: String): Maybe<RecoverPasswordResponse>

    //fun checkRecoveryCode(email: String, code: String): Completable
    //fun setPassword(email: String, code: String, password: String): Completable

    fun checkPasswordRecoveryCode(type: String, code: String): Completable
    fun recoverPassword(body: RecoverPasswordBody): Completable

    fun checkAppUpdate(appVersion : String): Maybe<AppUpdateModel>
}
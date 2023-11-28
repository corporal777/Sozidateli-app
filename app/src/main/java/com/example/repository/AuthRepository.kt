package com.example.repository

import com.example.data.bodies.*
import com.example.data.models.*
import com.example.data.models.user.User
import com.example.data.models.user.UserResp
import com.example.ui.snAuth.SnAuth
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthRepository {

    //fun setEmailSocialNetwork(snType: String, email: String, token: String): Completable

    fun authEmailOrPhone(login: AuthBody): Completable
    fun authEmailOrPhoneWithResult(login: AuthBody):Single<NewAuthResponse>

    fun sendQrCode(body: QrBody):Single<QrAuthResponse>
    fun authWebWithQrCode(body: QrBody):Single<NewAuthResponse>
    fun registerUser(body: RegisterBody): Completable

    fun registerEmailResend(email: String): Completable
    fun registerPhoneResend(type: String, phone: String): Completable
    fun confirmPhoneCode(body: ConfirmCodeBody): Completable
    fun confirmEmailCode(body: EmailCodeBody): Completable
    //fun registerSnResend(email: String, token: String): Completable

    //fun sendRecoveryEmail(email: String): Completable
    fun sendRecoveryEmail(type: String, email: String): Maybe<RecoverPasswordResponse>
    //fun checkRecoveryCode(email: String, code: String): Completable
    //fun setPassword(email: String, code: String, password: String): Completable

    //fun registerData(email: String, code: String): Single<UserResp>
    fun checkRecoveryCodeNew(type: String, code: String): Completable
    fun recoverPasswordNew(body: RecoverPasswordBody): Completable
    fun rebaseInvite(id: Int, body: RebaseInviteBody): Completable
    fun deleteConfirmEmail(email: String): Completable

    fun checkAppUpdate(appVersion : String): Maybe<AppUpdateModel>
}
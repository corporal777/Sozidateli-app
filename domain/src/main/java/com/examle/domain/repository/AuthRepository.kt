package com.examle.domain.repository

import com.examle.domain.model.Optional
import com.examle.domain.model.auth.AuthModel
import com.examle.domain.model.auth.SnAuthModel
import com.examle.domain.model.auth.TokenStatusModel
import com.examle.domain.model.body.AuthBody
import com.examle.domain.model.body.VKAuthBody
import com.examle.domain.model.user.SnUser
import io.reactivex.Completable
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun checkUserAuth(): Flow<TokenStatusModel>

    //fun getFcmToken(): Maybe<String>
    //fun sendFcmToken(): Completable
    //fun deleteFcmToken(): Completable

    //fun getTemporaryToken() : Completable
    fun authEmailOrPhoneWithResult(login: AuthBody): Flow<AuthModel>

    fun authEmailOrPhoneWithInvite(invite: Int, body: AuthBody): Flow<AuthModel>
    fun authEmailOrPhoneWithSn(body: AuthBody, sn: SnAuthModel): Flow<AuthModel>


    //auth social
    fun authWithVk(body: VKAuthBody, sn: SnAuthModel): Flow<Optional<SnUser>>
//
//    fun sendQrCode(body: QrBody):Single<QrAuthResponse>
//    fun authWebWithQrCode(body: QrBody): Single<AuthResponse>
//
//    fun registerUser(body: RegisterBody): Single<AuthResponse>
//    fun registerSnUser(body: SnRegisterBody): Single<AuthResponse>
//
//    fun registerEmailResend(id : Int, email: String): Completable
//    fun registerPhoneResend(id : Int, phone: String): Completable
//
//    fun confirmPhoneCode(id : Int, body: com.examle.data.bodies.ConfirmCodeBody): Completable
//    fun confirmEmailCode(id : Int, body: com.examle.data.bodies.EmailCodeBody): Completable
//    fun deleteConfirmEmail(email: String): Completable
//
//
//    //fun sendRecoveryEmail(email: String): Completable
//    fun sendRecoveryEmail(type: String, email: String): Maybe<RecoverPasswordResponse>
//
//    //fun checkRecoveryCode(email: String, code: String): Completable
//    //fun setPassword(email: String, code: String, password: String): Completable
//
//    fun checkPasswordRecoveryCode(type: String, code: String): Completable
//    fun recoverPassword(body: RecoverPasswordBody): Completable
//
//    fun checkAppUpdate(appVersion : String): Maybe<AppUpdateModel>
}
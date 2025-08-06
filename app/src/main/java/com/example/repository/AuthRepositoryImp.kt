package com.example.repository

import com.example.api.Api
import com.example.data.AppData
import com.example.data.bodies.*
import com.example.data.models.*
import com.example.extensions.getAppVersion
import com.example.extensions.getAppVersionCode
import com.example.extensions.getDeviceName
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMap
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.future.await
import java.lang.NullPointerException
import javax.inject.Inject


class AuthRepositoryImp
@Inject constructor(
    private val appData: AppData,
    private val api: Api
) : ApiRepository(appData), AuthRepository {

    override fun checkUserAuth(): Completable {
        return Completable.complete()
    }

    override fun checkUserAuthFlow(): Flow<AuthResponse> {
        return flow { emit(api.getTokenStatus()) }
    }

    override fun getFcmToken(): Maybe<String> {
        return Maybe.create { emitter ->
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token -> emitter.onSuccess(token) }
                .addOnFailureListener { e -> emitter.onError(e) }
        }
    }

    override fun sendFcmToken(): Completable {
        return getFcmToken().flatMapCompletable { api.registerFcmToken(FcmTokenBody(it)) }
    }

    override fun deleteFcmToken(): Completable {
        return getFcmToken().flatMapCompletable { api.unregisterFcmToken(FcmTokenBody(it)) }
    }

    override fun getTemporaryToken(): Completable {
        return api.getTemporaryToken().doOnSuccess {
            appData.tempToken = it.token
            appData.tempUserId = it.id ?: -1
        }.ignoreElement()
    }

    override fun getStories(): List<String> {
        return listOf(
            "Завязывайте новые знакомства и встречайте единомышленников",
            "Находите интересные события и выступайте на мероприятиях",
            "Будьте в курсе актуальных событий в вашем городе и во всей стране",
            "Создавайте интересные страницы своих мероприятий",
            "Рассказывайте о себе, обменивайтесь опытом и получайте новые знания",
        )
    }

    override fun authEmailOrPhone(login: AuthBody): Completable {
        return callAuthCompletable(api.authEmailOrPhone(login))
    }

    override fun authEmailOrPhoneWithResult(login: AuthBody): Flow<AuthResponse> =
        flow { emit(api.authByEmailOrPhone(login)) }


    override fun authEmailOrPhoneWithInvite(invite: Int, body: AuthBody): Flow<AuthResponse> {
        return flow<AuthResponse> { api.authByEmailOrPhone(body) }
            .flatMapConcat { auth ->
                val rebase = RebaseInviteBody(auth.id ?: 0, auth.token ?: "")
                flowOf(api.rebaseInvite(invite, rebase)).map { auth }
            }
    }

    override fun authEmailOrPhoneWithSn(body: AuthBody, snAuth: SnAuth): Flow<AuthResponse> {
        return flow { emit(api.authByEmailOrPhone(body)) }
            .flatMapConcat { auth ->
                bindSocialAccount(snAuth.uuid, auth.id, snAuth.snType.code, auth.token)
                    .map { auth }
            }
    }

    override fun authWithVk(token: String, uuid: String): Flow<SnAuthResponse> {
        return flow {
            emit(api.authWithVk(
                VKAuthBody(
                    token,
                    uuid,
                    appData.deviceId ?: "",
                    getDeviceName(),
                    getAppVersionCode(),
                    getAppVersion()
                )
            ))
        }
    }

    override fun sendQrCode(body: QrBody): Single<QrAuthResponse> {
        return api.sendQrCodeToGetDeviceInfo(body)
    }

    override fun authWebWithQrCode(body: QrBody): Single<AuthResponse> {
        return api.authWithQrCode(body)
    }

    //+
    override fun registerUser(body: RegisterBody): Single<AuthResponse> {
        return api.registerUser(body)
    }

    //+
    override fun registerSnUser(body: SnRegisterBody): Single<AuthResponse> {
        return api.registerSnUser(body)
    }

    override fun registerEmailResend(id: Int, email: String): Completable {
        return api.registerEmailResend(id, email)
    }

    override fun registerPhoneResend(id: Int, phone: String): Completable {
        return api.registerPhoneResend(id, "personal", phone)
    }

    override fun confirmEmailCode(id: Int, body: EmailCodeBody): Completable =
        api.confirmEmailCode(id, body)

    override fun confirmPhoneCode(id: Int, body: ConfirmCodeBody): Completable {
        return api.confirmPhoneCode(id, body).doOnSuccess {}.ignoreElement()
    }

    private fun callAuthCompletable(authRequest: Single<AuthResponse>): Completable {
        return authRequest.doOnSuccess {
            val token = it?.token
            if (token != null) {
                appData.login(token)
                appData.saveId(it.id)
            }
        }.doOnSuccess { appData.token = it.token }.map { it }.ignoreElement()
    }

    override fun sendRecoveryEmail(type: String, email: String): Maybe<RecoverPasswordResponse> {
        return api.sendEmailRecovery(type, email)
    }

    override fun checkPasswordRecoveryCode(type: String, code: String): Completable {
        return api.checkPasswordRecover(type, code)
    }

    override fun recoverPassword(body: RecoverPasswordBody): Completable {
        return callAuthCompletable(api.recoverPassword(body))
    }

    override fun deleteConfirmEmail(email: String): Completable =
        api.deleteConfirmEmail(appData.getId(), email)

    override fun checkAppUpdate(appVersion: String): Maybe<AppUpdateModel> {
        return api.checkAppVersion(appVersion, "android")
    }

    private fun bindSocialAccount(
        uuid: String,
        userId: Int?,
        socialType: String,
        token: String?
    ): Flow<Unit> {
        return flow {
            if (userId == null || token.isNullOrEmpty()) throw NullPointerException()
            else {
                val oldTempToken = appData.tempToken
                appData.tempToken = null

                api.bindSocialAccountWithToken(BindSocialAccountBody(uuid, userId, socialType, false), "Token $token")
                appData.tempToken = oldTempToken
                emit(Unit)
            }
        }
    }
}
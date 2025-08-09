package com.examle.data.repository

import com.examle.data.AppData
import com.examle.data.api.Api
import com.examle.data.bodies.RebaseInviteBody
import com.examle.domain.model.AuthModel
import com.examle.domain.model.TokenStatusModel
import com.examle.domain.model.body.AuthBody
import com.examle.domain.repository.AuthRepository
import com.example.common.flatMap
import io.reactivex.Completable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import javax.inject.Inject


class AuthRepositoryImp
@Inject constructor(
    private val appData: AppData,
    private val api: Api
) : ApiRepository(appData), AuthRepository {

    override fun checkUserAuth(): Completable {
        return Completable.complete()
    }

    override fun checkUserAuthFlow(): Flow<TokenStatusModel> {
        return flow { emit(api.getTokenStatus()) }
            .catch {
                if (it is HttpException && it.code() == 400) appData.logoutInvalidation()
                else it.printStackTrace()
            }
    }

//    override fun getFcmToken(): Maybe<String> {
//        return Maybe.create { emitter ->
//            FirebaseMessaging.getInstance().token
//                .addOnSuccessListener { token -> emitter.onSuccess(token) }
//                .addOnFailureListener { e -> emitter.onError(e) }
//        }
//    }

//    override fun sendFcmToken(): Completable {
//        return getFcmToken().flatMapCompletable { api.registerFcmToken(
//            com.examle.data.bodies.FcmTokenBody(
//                it
//            )
//        ) }
//    }

//    override fun deleteFcmToken(): Completable {
//        return getFcmToken().flatMapCompletable { api.unregisterFcmToken(
//            com.examle.data.bodies.FcmTokenBody(
//                it
//            )
//        ) }
//    }

//    override fun getTemporaryToken(): Completable {
//        return api.getTemporaryToken().doOnSuccess {
//            appData.tempToken = it.token
//            appData.tempUserId = it.id ?: -1
//        }.ignoreElement()
//    }

    override fun getStories(): List<String> {
        val list = listOf(
            "Завязывайте новые знакомства и встречайте единомышленников",
            "Находите интересные события и выступайте на мероприятиях",
            "Будьте в курсе актуальных событий в вашем городе и во всей стране",
            "Создавайте интересные страницы своих мероприятий",
            "Рассказывайте о себе, обменивайтесь опытом и получайте новые знания",
        )
        return arrayListOf<String>().apply {
            add(list.last())
            addAll(list)
            add(list.first())
        }
    }

    override fun authEmailOrPhoneWithResult(login: AuthBody): Flow<AuthModel> {
        return flow { emit(api.authByEmailOrPhone(login)) }.map { AuthModel(it.id, it.token) }
    }

    override fun authEmailOrPhoneWithInvite(invite: Int, body: AuthBody): Flow<AuthModel> {
        return flow { emit(api.authByEmailOrPhone(body)) }
            .map { AuthModel(it.id, it.token) }
            .flatMap { auth ->
                val rebase = RebaseInviteBody(auth.id, auth.token)
                flowOf(api.rebaseInvite(invite, rebase)).map { auth }
            }
    }



//    override fun authEmailOrPhoneWithSn(body: com.examle.data.bodies.AuthBody, snAuth: SnAuth): Flow<AuthResponse> {
//        return flow { emit(api.authByEmailOrPhone(body)) }
//            .flatMapConcat { auth ->
//                bindSocialAccount(snAuth.uuid, auth.id, snAuth.snType.code, auth.token)
//                    .map { auth }
//            }
//    }
//
//    override fun authWithVk(token: String, uuid: String): Flow<SnAuthResponse> {
//        return flow {
//            emit(api.authWithVk(
//                com.examle.data.bodies.VKAuthBody(
//                    token,
//                    uuid,
//                    appData.deviceId ?: "",
//                    getDeviceName(),
//                    getAppVersionCode(),
//                    getAppVersion()
//                )
//            ))
//        }
//    }

//    override fun sendQrCode(body: QrBody): Single<QrAuthResponse> {
//        return api.sendQrCodeToGetDeviceInfo(body)
//    }
//
//    override fun authWebWithQrCode(body: QrBody): Single<AuthResponse> {
//        return api.authWithQrCode(body)
//    }
//
//    //+
//    override fun registerUser(body: RegisterBody): Single<AuthResponse> {
//        return api.registerUser(body)
//    }
//
//    //+
//    override fun registerSnUser(body: SnRegisterBody): Single<AuthResponse> {
//        return api.registerSnUser(body)
//    }
//
//    override fun registerEmailResend(id: Int, email: String): Completable {
//        return api.registerEmailResend(id, email)
//    }
//
//    override fun registerPhoneResend(id: Int, phone: String): Completable {
//        return api.registerPhoneResend(id, "personal", phone)
//    }
//
//    override fun confirmEmailCode(id: Int, body: com.examle.data.bodies.EmailCodeBody): Completable =
//        api.confirmEmailCode(id, body)
//
//    override fun confirmPhoneCode(id: Int, body: com.examle.data.bodies.ConfirmCodeBody): Completable {
//        return api.confirmPhoneCode(id, body).doOnSuccess {}.ignoreElement()
//    }
//
//    private fun callAuthCompletable(authRequest: Single<AuthResponse>): Completable {
//        return authRequest.doOnSuccess {
//            val token = it?.token
//            if (token != null) {
//                appData.login(token)
//                appData.saveId(it.id)
//            }
//        }.doOnSuccess { appData.token = it.token }.map { it }.ignoreElement()
//    }
//
//    override fun sendRecoveryEmail(type: String, email: String): Maybe<RecoverPasswordResponse> {
//        return api.sendEmailRecovery(type, email)
//    }
//
//    override fun checkPasswordRecoveryCode(type: String, code: String): Completable {
//        return api.checkPasswordRecover(type, code)
//    }
//
//    override fun recoverPassword(body: RecoverPasswordBody): Completable {
//        return callAuthCompletable(api.recoverPassword(body))
//    }
//
//    override fun deleteConfirmEmail(email: String): Completable =
//        api.deleteConfirmEmail(appData.getId(), email)
//
//    override fun checkAppUpdate(appVersion: String): Maybe<AppUpdateModel> {
//        return api.checkAppVersion(appVersion, "android")
//    }

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

                api.bindSocialAccountWithToken(
                    com.examle.data.bodies.BindSocialAccountBody(
                        uuid,
                        userId,
                        socialType,
                        false
                    ), "Token $token")
                appData.tempToken = oldTempToken
                emit(Unit)
            }
        }
    }
}
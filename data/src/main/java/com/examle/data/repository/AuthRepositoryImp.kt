package com.examle.data.repository

import com.examle.data.AppData
import com.examle.data.bodies.BindSocialAccountBody
import com.examle.domain.model.Optional
import com.examle.data.source.remote.Api
import com.examle.domain.model.asOptional
import com.examle.domain.model.auth.AuthModel
import com.examle.domain.model.auth.SnAuthModel
import com.examle.domain.model.auth.TokenStatusModel
import com.examle.domain.model.body.AuthBody
import com.examle.domain.model.body.InviteBody
import com.examle.domain.model.body.VKAuthBody
import com.examle.domain.model.user.SnUser
import com.examle.domain.repository.AuthRepository
import com.example.common.flatMap
import io.reactivex.Completable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import retrofit2.HttpException
import javax.inject.Inject


class AuthRepositoryImp
@Inject constructor(
    private val appData: AppData,
    private val api: Api
) : ApiRepository(appData), AuthRepository {


    override fun checkUserAuth(): Flow<TokenStatusModel> {
        return flow { emit(api.getTokenStatus()) }.catch {
            if (it is HttpException && it.code() == 400) appData.logoutInvalidation()
            else it.printStackTrace()
        }
    }

    override fun authEmailOrPhoneWithResult(login: AuthBody): Flow<AuthModel> {
        return flow { emit(api.authByEmailOrPhone(login)) }
            .map { AuthModel(it.id, it.token) }
            .saveLogin()
    }

    override fun authEmailOrPhoneWithInvite(invite: Int, body: AuthBody): Flow<AuthModel> {
        return flow { emit(api.authByEmailOrPhone(body)) }
            .flatMap { auth ->
                val rebase = InviteBody(auth.id, auth.token)
                flowOf(api.rebaseInvite(invite, rebase)).map { AuthModel(auth.id, auth.token) }
            }.saveLogin()
    }

    override fun authEmailOrPhoneWithSn(body: AuthBody, sn: SnAuthModel): Flow<AuthModel> {
        return flow { emit(api.authByEmailOrPhone(body)) }
            .flatMap {
                val oldTempToken = appData.tempToken
                appData.tempToken = null
                api.bindSocialAccountWithToken(
                    BindSocialAccountBody(
                        sn.uuid,
                        it.id,
                        sn.snType.code,
                        false
                    ), "Token ${it.token}"
                )
                appData.tempToken = oldTempToken

                flowOf(AuthModel(it.id, it.token))
            }.saveLogin()
    }

    override fun authWithVk(body: VKAuthBody, sn: SnAuthModel): Flow<Optional<SnUser>> {
        return flow { emit(api.authWithVk(body)) }
            .map {
                if (it.accessData != null && it.accessData.token.isNotEmpty()){
                    appData.login(it.accessData.token)
                    appData.saveId(it.accessData.id)
                    Optional()
                } else SnUser(sn, it.personalData).asOptional()
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


//


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

    private fun Flow<AuthModel>.saveLogin(): Flow<AuthModel> {
        return onEach {
            appData.login(it.token)
            appData.saveId(it.id)
        }
    }
}
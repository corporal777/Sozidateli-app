package com.example.repository

import com.example.api.Api
import com.example.data.AppData
import com.example.data.bodies.*
import com.example.data.models.*
import com.example.extensions.getAppVersion
import com.example.extensions.getAppVersionCode
import com.example.extensions.getDeviceName
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import java.lang.NullPointerException
import javax.inject.Inject


class AuthRepositoryImp
@Inject constructor(
    private val appData: AppData,
    private val api: Api
) : ApiRepository(appData), AuthRepository {

    override fun getTemporaryToken(): Completable {
        return api.getTemporaryToken().doOnSuccess {
            appData.tempToken = it.token
            appData.tempUserId = it.id ?: -1
        }.ignoreElement()
    }

    override fun getStories(): Maybe<List<String>> {
        return Maybe.just(
            listOf(
                "Завязывайте новые знакомства и встречайте единомышленников",
                "Находите интересные события и выступайте на мероприятиях",
                "Будьте в курсе актуальных событий в вашем городе и во всей стране",
                "Создавайте интересные страницы своих мероприятий",
                "Рассказывайте о себе, обменивайтесь опытом и получайте новые знания",
            )
        )
    }

    override fun authEmailOrPhone(login: AuthBody): Completable {
        return callAuthCompletable(api.authEmailOrPhone(login))
    }

    override fun authEmailOrPhoneWithResult(login: AuthBody): Single<AuthResponse> =
        api.authEmailOrPhone(login)

    override fun authEmailOrPhoneWithInvite(invite: Int, body: AuthBody): Completable {
        return api.authEmailOrPhone(body)
            .flatMap { auth ->
                api.rebaseInvite(invite, RebaseInviteBody(auth.id ?: 0, auth.token ?: ""))
                    .andThen(Single.just(auth))
            }.doOnSuccess {
                if (it.token != null) appData.login(it.token)
                if (it.id != null) appData.saveId(it.id)
            }.ignoreElement()
    }

    override fun authEmailOrPhoneWithSn(body: AuthBody, snAuth: SnAuth): Completable {
        return api.authEmailOrPhone(body)
            .flatMap { auth ->
                bindSocialAccount(snAuth.uuid, auth.id, snAuth.snType.code, auth.token)
                    .andThen(Single.just(auth))
            }.doOnSuccess {
                if (it.token != null) appData.login(it.token)
                if (it.id != null) appData.saveId(it.id)
            }.ignoreElement()
    }

    override fun authWithVk(token: String, uuid: String): Single<SnAuthResponse> {
        return api.authWithVk(
            VKAuthBody(
                token,
                uuid,
                appData.deviceId ?: "",
                getDeviceName(),
                getAppVersionCode(),
                getAppVersion()
            )
        )
    }

    override fun sendQrCode(body: QrBody): Single<QrAuthResponse> {
        return api.sendQrCodeToGetDeviceInfo(body)
    }

    override fun authWebWithQrCode(body: QrBody): Single<AuthResponse> {
        return api.authWithQrCode(body)
    }

    //+
    override fun registerUser(body: RegisterBody): Completable {
        return api.registerUser(body).doOnSuccess { appData.saveId(it.id) }.ignoreElement()
    }

    //+
    override fun registerSnUser(body: SnRegisterBody): Completable {
        return api.registerSnUser(body).doOnSuccess { appData.saveId(it.id) }.ignoreElement()
    }

    override fun registerEmailResend(email: String): Completable {
        return api.registerEmailResend(appData.getId(), email)
    }

    override fun registerPhoneResend(type: String, phone: String): Completable {
        return api.registerPhoneResend(appData.getId(), type, phone)
    }

    override fun confirmEmailCode(body: EmailCodeBody): Completable =
        api.confirmEmailCode(appData.getId(), body).doOnSuccess {
            appData.login(it.token)
            appData.saveId(it.id)
        }.ignoreElement()

    override fun confirmPhoneCode(body: ConfirmCodeBody): Completable {
        return api.confirmPhoneCode(appData.getId(), body).doOnSuccess {
            if (!it.token.isNullOrEmpty()) appData.login(it.token)
            if (it.id != null) appData.saveId(it.id)
        }.ignoreElement()
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
    ): Completable {
        return if (userId == null || token.isNullOrEmpty()) Completable.error(NullPointerException())
        else api.bindSocialAccountWithToken(
            BindSocialAccountBody(
                uuid,
                userId,
                socialType,
                false
            ),
            "Token $token"
        )
    }
}
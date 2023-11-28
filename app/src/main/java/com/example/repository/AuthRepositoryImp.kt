package com.example.repository

import com.example.api.Api
import com.example.data.AppData
import com.example.data.bodies.*
import com.example.data.models.*
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject


class AuthRepositoryImp
@Inject constructor(
        private val appData: AppData,
        private val api: Api
) : ApiRepository(appData), AuthRepository {

    override fun authEmailOrPhone(login: AuthBody): Completable {
        return callAuthCompletable(api.authEmailOrPhone(login))
    }

    override fun authEmailOrPhoneWithResult(login: AuthBody): Single<NewAuthResponse> =
            api.authEmailOrPhone(login)

    override fun sendQrCode(body: QrBody): Single<QrAuthResponse> {
        return api.sendQrCodeToGetDeviceInfo(body)
    }

    override fun authWebWithQrCode(body: QrBody): Single<NewAuthResponse> {
        return api.authWithQrCode(body)
    }

    //+
    override fun registerUser(body: RegisterBody): Completable {
        return api.registerUser(body).doOnSuccess { appData.saveId(it.id) }.ignoreElement()
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

    private fun callAuthCompletable(authRequest: Single<NewAuthResponse>): Completable {
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

    /*override fun checkRecoveryCode(email: String, code: String): Completable {
        return callAuthCompletable(api.checkRecoveryCode(email, code))
    }

    override fun setPassword(email: String, code: String, password: String): Completable {
        return callAuthCompletable(api.setPassword(email, code, password))
    }*/


    override fun checkRecoveryCodeNew(type: String, code: String): Completable {
        return api.checkPasswordRecover(type, code)
    }

    override fun recoverPasswordNew(body: RecoverPasswordBody): Completable {
        return callAuthCompletable(api.recoverPassword(body))
    }

    override fun rebaseInvite(id: Int, body: RebaseInviteBody): Completable =
            api.rebaseInvite(id, body).doOnComplete {
                appData.login(body.token)
                appData.saveId(body.user)
            }.doOnComplete { appData.token = body.token }

    override fun deleteConfirmEmail(email: String): Completable =
            api.deleteConfirmEmail(appData.getId(), email)

    override fun checkAppUpdate(appVersion: String): Maybe<AppUpdateModel> {
        return api.checkAppVersion(appVersion, "android")
    }
}
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
        return callNewAuthCompletable(api.authEmailOrPhone(login))
    }

    override fun authEmailOrPhoneWithResult(login: AuthBody): Single<NewAuthResponse> =
            api.authEmailOrPhone(login)

    override fun sendQrCode(body: QrBody): Single<QrAuthResponse> {
        return api.sendQrCodeToGetDeviceInfo(body)
    }

    override fun authWebWithQrCode(body: QrBody): Single<NewAuthResponse> {
        return api.authWithQrCode(body)
    }

    override fun register(body: RegisterBody): Completable {
        return api.registerEmail(body).doOnSuccess {
            appData.setUserShort(it)
        }.ignoreElement()
    }

    //+
    override fun registerUser(body: RegisterBody): Completable {
        return api.registerUser(body).doOnSuccess {
            appData.saveId(it.id)
            if (it.token != null) appData.login(it.token)
        }.ignoreElement()
    }

    /*override fun registerData(email: String, code: String): Single<UserResp> {
        return call(api.registerData(email, code))
    }

    override fun registerConfirm(email: String, code: String, name: String,lastName: String,
                                 middleName: String?, phone: String?, newEmail: String?, password: String?): Completable {
        return callAuthCompletable(api.registerEmailConfirm(email, code, name, lastName, middleName, phone, newEmail, password))
    }*/

    override fun registerEmailResend(email: String): Completable {
        return api.registerEmailResend(appData.getId(), email)
    }

    override fun registerPhoneResend(type: String, phone: String): Completable {
        return api.registerPhoneResend(appData.getId(), type, phone)
    }


    private fun callNewAuthCompletable(authRequest: Single<NewAuthResponse>): Completable {
        return authRequest.doOnSuccess {
            val token = it?.token
            if (token != null) {
                appData.login(token)
                appData.saveId(it.id)
            }
        }.doOnSuccess { appData.token = it.token }.map { it }.ignoreElement()
    }

    private fun callAuthCompletable(authRequest: Single<ApiResponse<AuthResponse>>): Completable {
        return call(authRequest.doOnSuccess {
            val token = it.session?.token
            if (token != null) {
                appData.login(token)
            }
        }).ignoreElement()
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

    /*override fun registerSnResend(email: String, token: String): Completable {
        return callAuthCompletable(api.registerSnResend(email, token))
    }

    override fun sendRecoveryEmail(email: String): Completable {
        return callAuthCompletable(api.sendEmailRecovery(email))
    }*/

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
        return callNewAuthCompletable(api.recoverPassword(body))
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
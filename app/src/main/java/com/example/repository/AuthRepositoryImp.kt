package com.example.repository

import androidx.core.os.bundleOf
import com.example.api.Api
import com.example.api.NewApi
import com.example.data.AppData
import com.example.data.bodies.*
import com.example.data.models.*
import com.example.data.models.user.User
import com.example.data.models.user.UserResp
import com.example.exceptions.NoInternetConnectionException
import com.example.ui.snAuth.SnAuth
import com.example.ui.snAuth.SnAuthError
import com.example.ui.snAuth.SnType
import com.example.util.ApiErrorParser
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.vk.sdk.api.VKApi
import com.vk.sdk.api.VKError
import com.vk.sdk.api.VKRequest
import com.vk.sdk.api.VKResponse
import com.vk.sdk.api.model.VKUsersArray
import getStringOrNull
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import org.json.JSONObject
import retrofit2.http.Field
import ru.ok.android.sdk.Odnoklassniki
import ru.ok.android.sdk.OkListener
import java.net.ConnectException
import javax.inject.Inject


class AuthRepositoryImp
@Inject constructor(
        private val appData: AppData,
        private val api: Api,
        private val newApi: NewApi
) : ApiRepository(appData), AuthRepository {

    override fun authSocialNetwork(snAuth: SnAuth): Single<Pair<RegisterStatus, SnUser>> {
        val userRequest = when (snAuth.snType) {
            SnType.VK -> getVkUser()
            SnType.FB -> getFbUser()
            SnType.OK -> getOkUser()
        }

        return userRequest
                .flatMap {
                    val checkStatus = checkRegisterStatus(snAuth.snType.code, it.id, null)
                    Single.zip<RegisterStatus, SnUserData, Pair<RegisterStatus, SnUser>>(checkStatus, Single.just(it), BiFunction { status, snUser ->
                        status to SnUser(snAuth, snUser)
                    })
                }
    }

    override fun authSocialNetwork(snType: String, token: String, email: String?, firstName: String?, lastName: String?, password: String?): Completable {
        return callAuthCompletable(
                api.authSocialNetwork(snType, token, email, firstName, lastName, password)
                        .map {
                            ApiResponse(
                                    server = it.server,
                                    response = AuthResponse(it.response.user_id),
                                    response_detail = it.response_detail,
                                    session = it.session,
                                    code = it.code
                            )
                        }
        )
    }

    /*override fun setEmailSocialNetwork(snType: String, email: String, token: String): Completable {
        return callAuthCompletable(api.setEmailSocialNetwork(snType, email, token))
    }*/

    override fun confirmEmailSocialNetwork(id: String, code: String): Completable {
        return callAuthCompletable(api.confirmEmailSocialNetwork(id, code))
    }

    override fun authEmailOrPhone(login: AuthBody): Completable {
        return callNewAuthCompletable(newApi.authEmailOrPhone(login))
    }

    override fun authEmailOrPhoneWithResult(login: AuthBody): Single<NewAuthResponse> =
            newApi.authEmailOrPhone(login)

    override fun sendQrCode(body: QrBody): Single<QrAuthResponse> {
        return newApi.sendQrCodeToGetDeviceInfo(body)
    }

    override fun authWebWithQrCode(body: QrBody): Single<NewAuthResponse> {
        return newApi.authWithQrCode(body)
    }

    override fun register(body: RegisterBody): Completable {
        return newApi.registerEmail(body).doOnSuccess {
            appData.setUserShortNew(it)
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
        return newApi.registerEmailResend(appData.getId(), email)
    }

    override fun registerPhoneResend(type: String, phone: String): Completable {
        return newApi.registerPhoneResend(appData.getId(), type, phone)
    }

    override fun confirmPhone(body: ConfirmCodeBody): Completable {
        return callNewAuthCompletable(newApi.confirmPhone(appData.getId(), body))
    }

    /*override fun registerSnResend(email: String, token: String): Completable {
        return callAuthCompletable(api.registerSnResend(email, token))
    }

    override fun sendRecoveryEmail(email: String): Completable {
        return callAuthCompletable(api.sendEmailRecovery(email))
    }*/

    override fun sendRecoveryEmail(type: String, email: String): Completable {
        return newApi.sendEmailRecovery(type, email)
    }

    /*override fun checkRecoveryCode(email: String, code: String): Completable {
        return callAuthCompletable(api.checkRecoveryCode(email, code))
    }

    override fun setPassword(email: String, code: String, password: String): Completable {
        return callAuthCompletable(api.setPassword(email, code, password))
    }*/

    override fun checkRegisterStatus(snType: String?, snId: String?, email: String?): Single<RegisterStatus> {
        return call(api.registerStatus(email, snType, snId))
    }

    override fun getVkUser(): Single<SnUserData> {
        return Single.create { emitter ->
            VKApi.users().get().apply {
                addExtraParameter("fields", "photo_200")
            }.executeWithListener(object : VKRequest.VKRequestListener() {
                override fun onComplete(response: VKResponse) {
                    val user = VKUsersArray().let {
                        it.parse(response.json)
                        it[0]
                    }
                    emitter.onSuccess(SnUserData(user.id.toString(), user.first_name, user.last_name, user.photo_200, null))
                }

                override fun onError(error: VKError?) {
                    emitter.onError(RuntimeException(error?.errorMessage ?: "Vk get user error"))
                }
            })
        }
    }

    override fun getFbUser(): Single<SnUserData> {
        val accessToken = AccessToken.getCurrentAccessToken()
                ?: return Single.error(SnAuthError("No fb access token error"))

        return Single.create<SnUserData> { emitter ->
            GraphRequest.newMeRequest(accessToken) { json, _ ->
                val id = json.getString("id")
                val firstName = json.getStringOrNull("first_name")
                val lastName = json.getStringOrNull("last_name")
                val email = json.getStringOrNull("email")
                val avatar = "https://graph.facebook.com/$id/picture?width=200&height=200"
                emitter.onSuccess(SnUserData(id, firstName, lastName, avatar, email))
            }.apply {
                parameters = bundleOf("fields" to "id,first_name,last_name,email")
                executeAndWait()
            }
        }
    }

    override fun getOkUser(): Single<SnUserData> {
        return Single.create { emitter ->
            Odnoklassniki.instance.request("users.getCurrentUser", listener = object : OkListener {
                override fun onError(error: String?) {
                    emitter.onError(RuntimeException(error ?: "Ok get user error"))
                }

                override fun onSuccess(json: JSONObject) {
                    val id = json.getString("uid")
                    val firstName = json.getStringOrNull("first_name")
                    val lastName = json.getStringOrNull("last_name")
                    val avatar = json.getStringOrNull("pic_3") ?: json.getStringOrNull("pic_2")
                    ?: json.getStringOrNull("pic_1")
                    val email = json.getStringOrNull("email")
                    emitter.onSuccess(SnUserData(id, firstName, lastName, avatar, email))
                }
            })
        }
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

    override fun checkRecoveryCodeNew(type: String, code: String): Completable {
        return newApi.checkPasswordRecover(type, code)
    }

    override fun recoverPasswordNew(body: RecoverPasswordBody): Completable {
        return callNewAuthCompletable(newApi.recoverPassword(body))
    }

    override fun rebaseInvite(id: Int, body: RebaseInviteBody): Completable =
            newApi.rebaseInvite(id, body).doOnComplete {
                appData.login(body.token)
                appData.saveId(body.user)
            }.doOnComplete { appData.token = body.token }

    override fun deleteConfirmEmail(email: String): Completable =
            newApi.deleteConfirmEmail(appData.getId(), email)
}
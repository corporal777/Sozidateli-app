package com.example.repository

import androidx.core.os.bundleOf
import com.example.api.Api
import com.example.data.AppData
import com.example.data.models.*
import com.example.ui.snAuth.SnAuth
import com.example.ui.snAuth.SnAuthError
import com.example.ui.snAuth.SnType
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
import ru.ok.android.sdk.Odnoklassniki
import ru.ok.android.sdk.OkListener
import javax.inject.Inject


class AuthRepositoryImp
@Inject constructor(
        appData: AppData,
        private val api: Api
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
        return call(api.authSocialNetwork(snType, token, email, firstName, lastName, password)).flatMapCompletable { Completable.complete() }
    }

    override fun setEmailSocialNetwork(snType: String, email: String, token: String): Completable {
        return callAuthCompletable(api.setEmailSocialNetwork(snType, email, token))
    }

    override fun confirmEmailSocialNetwork(snType: String, id: String, code: String): Completable {
        return callAuthCompletable(api.confirmEmailSocialNetwork(snType, id, code))
    }

    override fun authEmail(email: String, password: String): Completable {
        return callAuthCompletable(api.authEmail(email, password))
    }

    override fun register(email: String, password: String, firstName: String, lastName: String): Completable {
        return callAuthCompletable(api.registerEmail(email, password, firstName, lastName))
    }

    override fun registerConfirm(email: String, code: String): Completable {
        return callAuthCompletable(api.registerEmailConfirm(email, code))
    }

    override fun registerEmailResend(email: String): Completable {
        return callAuthCompletable(api.registerEmailResend(email))
    }

    override fun registerSnResend(snType: String, email: String, token: String): Completable {
        return callAuthCompletable(api.registerSnResend(snType, email, token))
    }

    override fun sendRecoveryEmail(email: String): Completable {
        return callAuthCompletable(api.sendEmailRecovery(email))
    }

    override fun checkRecoveryCode(email: String, code: String): Completable {
        return callAuthCompletable(api.checkRecoveryCode(email, code))
    }

    override fun setPassword(email: String, code: String, password: String): Completable {
        return callAuthCompletable(api.setPassword(email, code, password))
    }

    override fun checkRegisterStatus(snType: String?, snId: String?, email: String?): Single<RegisterStatus> {
        return call(api.registerStatus(email, snType, snId))
    }

    override fun getVkUser(): Single<SnUserData> {
        return Single.create { emitter ->
            VKApi.users().get().apply {
                addExtraParameter("fields", "photo_100")
            }.executeWithListener(object : VKRequest.VKRequestListener() {
                override fun onComplete(response: VKResponse) {
                    val user = VKUsersArray().let {
                        it.parse(response.json)
                        it[0]
                    }
                    emitter.onSuccess(SnUserData(user.id.toString(), user.first_name, user.last_name, user.photo_100, null))
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
                val avatar = "https://graph.facebook.com/$id/picture"
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

    private fun callAuthCompletable(authRequest: Single<ApiResponse<AuthResponse>>): Completable {
        return call(authRequest).flatMapCompletable { Completable.complete() }
    }
}
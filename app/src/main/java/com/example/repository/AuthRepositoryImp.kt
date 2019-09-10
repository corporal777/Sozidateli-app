package com.example.repository

import com.example.api.Api
import com.example.data.AppData
import com.example.data.models.ApiResponse
import com.example.data.models.AuthResponse
import com.example.data.models.RegisterStatus
import com.example.data.models.SnUserData
import com.facebook.Profile
import com.vk.sdk.api.VKApi
import com.vk.sdk.api.VKError
import com.vk.sdk.api.VKRequest
import com.vk.sdk.api.VKResponse
import com.vk.sdk.api.model.VKUsersArray
import io.reactivex.Completable
import io.reactivex.Single
import org.json.JSONObject
import ru.ok.android.sdk.Odnoklassniki
import ru.ok.android.sdk.OkListener
import javax.inject.Inject

class AuthRepositoryImp
@Inject constructor(
        appData: AppData,
        private val api: Api
) : ApiRepository(appData), AuthRepository {

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

    override fun register(email: String, password: String, name: String, lastName: String): Completable {
        return callAuthCompletable(api.registerEmail(email, password, name, lastName))
    }

    override fun registerConfirm(email: String, code: String): Completable {
        return callAuthCompletable(api.registerEmailConfirm(email, code))
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

    override fun checkSnRegisterStatus(snType: String, snId: String): Single<RegisterStatus> {
        return call(api.registerStatus(null, snType, snId))
    }

    override fun getVkUser(): Single<SnUserData> {
        return Single.create { emitter ->
            VKApi.users().get().executeWithListener(object : VKRequest.VKRequestListener() {
                override fun onComplete(response: VKResponse) {
                    val user = VKUsersArray().let {
                        it.parse(response.json)
                        it[0]
                    }
                    emitter.onSuccess(SnUserData(user.id.toString(), user.first_name, user.last_name))
                }

                override fun onError(error: VKError?) {
                    emitter.onError(RuntimeException(error?.errorMessage ?: "Vk get user error"))
                }
            })
        }
    }

    override fun getFbUser(): Single<SnUserData> {
        return Single.fromCallable {
            val profile = Profile.getCurrentProfile()
            SnUserData(profile.id, profile.firstName, profile.lastName)
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
                    val firstName = json.getString("first_name")
                    val lastName = json.getString("last_name")
                    emitter.onSuccess(SnUserData(id, firstName, lastName))
                }
            })
        }
    }

    private fun callAuthCompletable(authRequest: Single<ApiResponse<AuthResponse>>): Completable {
        return call(authRequest).flatMapCompletable { Completable.complete() }
    }
}
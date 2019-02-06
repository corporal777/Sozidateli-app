package com.example.ui.snAuth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import bundleOf
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.VKSdk
import com.vk.sdk.api.VKError
import io.reactivex.disposables.CompositeDisposable
import org.json.JSONObject
import ru.ok.android.sdk.Odnoklassniki
import ru.ok.android.sdk.OkAuthListener
import ru.ok.android.sdk.util.OkAuthType
import ru.ok.android.sdk.util.OkScope


class SnAuthActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    private val vkAuthCallback by lazy {
        object : VKCallback<VKAccessToken> {
            override fun onResult(res: VKAccessToken?) {
                if (res == null) authError()
                else authComplete(SnAuth(res.accessToken, res.email, SnType.VK))
            }

            override fun onError(error: VKError?) {
                authError(error?.errorMessage)
            }
        }
    }

    private val fbAuthCallbackManager by lazy { CallbackManager.Factory.create() }
    private val fbAuthCallback by lazy {
        object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                if (result == null) authError()
                else authComplete(SnAuth(result.accessToken.token, snType = SnType.FB))
            }

            override fun onCancel() {
                authError(ERROR_AUTH_CANCELLED)
            }

            override fun onError(error: FacebookException?) {
                authError(error?.message)
            }
        }
    }

    private val okAuthListener by lazy {
        object : OkAuthListener {
            override fun onSuccess(json: JSONObject?) {
                if (json == null) authError()
                else authComplete(SnAuth(json.getString("access_token"), snType = SnType.OK))
            }

            override fun onCancel(error: String?) {
                authError(error ?: ERROR_AUTH_CANCELLED)
            }

            override fun onError(error: String?) {
                authError(error)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = intent.extras
        val authType = args?.getSerializable(ARG_AUTH_TYPE)

        when (authType) {
            SnType.VK -> authVk(args.getStringArray(ARG_AUTH_VK_SCOPES) ?: emptyArray())
            SnType.FB -> authFb()
            SnType.OK -> authOk()
            else -> finish()
        }
    }

    private fun authVk(scopes: Array<String>) {
        VKSdk.login(this, *scopes)
    }

    private fun authFb() {
        LoginButton(this).apply {
            registerCallback(fbAuthCallbackManager, fbAuthCallback)
            callOnClick()
        }
    }

    private fun authOk() {
        val ok = Odnoklassniki.createInstance(this, OK_APP_ID, OK_APP_KEY)
        ok.requestAuthorization(this, OK_REDIRECT_URL, OkAuthType.ANY, OkScope.VALUABLE_ACCESS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            VKSdk.onActivityResult(requestCode, resultCode, data, vkAuthCallback) ||
                    fbAuthCallbackManager.onActivityResult(requestCode, resultCode, data) -> {
                //do nothing
            }
            Odnoklassniki.getInstance().isActivityRequestOAuth(requestCode) -> {
                Odnoklassniki.getInstance().onAuthActivityResult(requestCode, resultCode, data, okAuthListener)
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun authComplete(snUser: SnAuth) {
        SnAuthManager.onSnAuthComplete(snUser)
        finish()
    }

    private fun authError(message: String? = null) {
        SnAuthManager.onSnAuthError(SnAuthError(message))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    companion object {

        private const val OK_APP_ID = "1274287872"
        private const val OK_APP_KEY = "CBAIBFPMEBABABABA"
        private const val OK_REDIRECT_URL = "okauth://ok1274287872"

        private const val ARG_AUTH_TYPE = "auth_type"
        private const val ARG_AUTH_VK_SCOPES = "auth_vk_scopes"

        private const val ERROR_AUTH_CANCELLED = "Authorization was cancelled"

        internal fun getStartIntent(context: Context, snType: SnAuthActivity.SnType, vkScopes: Array<String>? = null): Intent {
            return Intent(context, SnAuthActivity::class.java).apply {
                putExtras(bundleOf(
                        SnAuthActivity.ARG_AUTH_TYPE to snType,
                        SnAuthActivity.ARG_AUTH_VK_SCOPES to vkScopes
                ))
            }
        }
    }

    enum class SnType {
        VK, FB, OK
    }
}

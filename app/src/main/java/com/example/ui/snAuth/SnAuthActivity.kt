package com.example.ui.snAuth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.VKSdk
import com.vk.sdk.api.VKError
import dagger.android.AndroidInjection
import org.json.JSONObject
import ru.ok.android.sdk.Odnoklassniki
import ru.ok.android.sdk.OkAuthListener
import ru.ok.android.sdk.util.OkAuthType
import ru.ok.android.sdk.util.OkScope
import javax.inject.Inject

class SnAuthActivity : AppCompatActivity() {

    @Inject lateinit var snAuthManager: SnAuthManager

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
            override fun onSuccess(json: JSONObject) {
                authComplete(SnAuth(json.getString("access_token"), snType = SnType.OK))
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
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        val args = intent.extras

        when (args?.getSerializable(ARG_AUTH_TYPE)) {
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
        val accessToken = AccessToken.getCurrentAccessToken()
        if (AccessToken.isCurrentAccessTokenActive()) {
            authComplete(SnAuth(accessToken.token, snType = SnType.FB))
        } else {
            LoginManager.getInstance().apply {
                registerCallback(fbAuthCallbackManager, fbAuthCallback)
                logInWithReadPermissions(this@SnAuthActivity, listOf())
            }
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
            Odnoklassniki.of(this).isActivityRequestOAuth(requestCode) -> {
                Odnoklassniki.of(this).onAuthActivityResult(requestCode, resultCode, data, okAuthListener)
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun authComplete(snAuth: SnAuth) {
        snAuthManager.onSnAuthComplete(snAuth)
        finish()
    }

    private fun authError(message: String? = null) {
        snAuthManager.onSnAuthError(SnAuthError(message))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        LoginManager.getInstance().unregisterCallback(fbAuthCallbackManager)
    }

    companion object {

        private const val OK_APP_ID = "1274287872"
        private const val OK_APP_KEY = "CBAIBFPMEBABABABA"
        private const val OK_REDIRECT_URL = "okauth://ok1274287872"

        private const val ARG_AUTH_TYPE = "auth_type"
        private const val ARG_AUTH_VK_SCOPES = "auth_vk_scopes"

        private const val ERROR_AUTH_CANCELLED = "Authorization was cancelled"

        internal fun getStartIntent(context: Context, snType: SnType, vkScopes: Array<String>? = null): Intent {
            return Intent(context, SnAuthActivity::class.java).apply {
                putExtras(bundleOf(
                        ARG_AUTH_TYPE to snType,
                        ARG_AUTH_VK_SCOPES to vkScopes
                ))
            }
        }
    }
}

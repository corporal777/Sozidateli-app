package com.example.ui.snAuth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.VKSdk
import com.vk.sdk.api.VKError
import dagger.android.AndroidInjection
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

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        val args = intent.extras

        when (args?.getSerializable(ARG_AUTH_TYPE)) {
            SnType.VK -> authVk(args.getStringArray(ARG_AUTH_VK_SCOPES) ?: emptyArray())
            else -> finish()
        }
    }

    private fun authVk(scopes: Array<String>) {
        VKSdk.login(this, *scopes)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            VKSdk.onActivityResult(requestCode, resultCode, data, vkAuthCallback) -> {}
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

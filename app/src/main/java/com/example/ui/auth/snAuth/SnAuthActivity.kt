package com.example.ui.auth.snAuth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.example.data.models.SnAuth
import com.example.data.models.SnType
import com.example.exceptions.SnAuthError
import com.vk.id.AccessToken
import com.vk.id.VKID
import com.vk.id.VKIDAuthFail
import com.vk.id.internal.log.LogEngine
import dagger.android.AndroidInjection
import io.reactivex.subjects.SingleSubject


class SnAuthActivity : AppCompatActivity() {

    private val vkAuthCallback = object : VKID.AuthCallback {
        override fun onSuccess(accessToken: AccessToken) {
            val token = accessToken.token
            val userId = accessToken.userID.toString()
            authComplete(SnAuth(token, userId, SnType.VK))
        }

        override fun onFail(fail: VKIDAuthFail) {
            authError(fail.description)
        }
    }

    private lateinit var authSubject: SingleSubject<SnAuth>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        val args = intent.extras ?: return
        if (args.getSerializable(ARG_AUTH_TYPE) == SnType.VK) authVk()
        else finish()

        authSubject = SnAuthCallbackHelper.getRequest()
    }

    private fun authVk() {
        val vkId = VKID(this)
        vkId.authorize(this, vkAuthCallback)
        VKID.logsEnabled = true
        VKID.logEngine = object: LogEngine {
            override fun log(logLevel: LogEngine.LogLevel, tag: String, message: String, throwable: Throwable?) {
                Log.e(tag, message)
                throwable?.printStackTrace()
            }
        }
    }

    private fun authComplete(snAuth: SnAuth) {
        finish()
        authSubject.onSuccess(snAuth)
    }

    private fun authError(message: String? = null) {
        finish()
        authSubject.onError(SnAuthError(message ?: ERROR_AUTH_CANCELLED))
    }

    companion object {
        private const val ARG_AUTH_TYPE = "auth_type"
        private const val ERROR_AUTH_CANCELLED = "Authorization was cancelled"

        internal fun createStartRequest(context: Context, snType: SnType): SingleSubject<SnAuth> {
            context.startActivity(Intent(context, SnAuthActivity::class.java).apply {
                putExtras(bundleOf(ARG_AUTH_TYPE to snType))
            })
            return SnAuthCallbackHelper.createRequest()
        }
    }
}

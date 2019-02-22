package com.example.ui.auth.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseFragment
import com.example.ui.snAuth.SnAuthManager
import com.vk.sdk.VKScope
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject
import javax.inject.Provider

class LoginFragment : BaseFragment(), LoginContract.View {

    @InjectPresenter
    lateinit var presenter: LoginPresenter

    @Inject
    lateinit var presenterProvider: Provider<LoginPresenter>

    @ProvidePresenter
    fun providePresenter(): LoginPresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*flFbAuth.setOnClickListener {
            //            presenter.onClickFb()
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", requireContext().packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        flVkAuth.setOnClickListener { presenter.onClickVk() }
        flOkAuth.setOnClickListener { presenter.onClickOk() }*/
        flEmailAuth.setOnClickListener { presenter.onClickEmail() }
    }

    override fun startVkAuthorization() {
        SnAuthManager.startAuthVk(requireContext(), arrayOf(VKScope.EMAIL))
    }

    override fun startFbAuthorization() {
        SnAuthManager.startAuthFacebook(requireContext())
    }

    override fun startOkAuthorization() {
        SnAuthManager.startAuthOk(requireContext())
    }

    override fun showLogin() {
        findNavController().navigate(LoginFragmentDirections.loginToLoginEmailAction(null, null, false))
    }

    override fun layout() = R.layout.fragment_login
}

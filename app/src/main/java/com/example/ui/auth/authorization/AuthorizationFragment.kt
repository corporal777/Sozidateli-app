package com.example.ui.auth.authorization

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.SnUser
import com.example.ui.base.BaseFragment
import com.example.ui.snAuth.SnAuthManager
import com.example.util.AuthBackground
import com.vk.sdk.VKScope
import kotlinx.android.synthetic.main.fragment_authorization.*
import javax.inject.Inject
import javax.inject.Provider

class AuthorizationFragment : BaseFragment(), AuthorizationContract.View {

    @InjectPresenter
    lateinit var presenter: AuthorizationPresenter

    @Inject
    lateinit var presenterProvider: Provider<AuthorizationPresenter>

    @ProvidePresenter
    fun providePresenter(): AuthorizationPresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ibFacebook.setOnClickListener { presenter.onFbClick() }
        ibVk.setOnClickListener { presenter.onVkClick() }
        ibOk.setOnClickListener { presenter.onOkClick() }
        ibEmail.setOnClickListener { presenter.onEmailClick() }
        ibLogin.setOnClickListener { presenter.onLoginClick() }

        svContent.background = AuthBackground.get(resources)
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
        findNavController().navigate(AuthorizationFragmentDirections.loginToLoginEmailAction(null, null, false, false))
    }

    override fun showRegistration(snUser: SnUser?) {
        findNavController().navigate(AuthorizationFragmentDirections.actionAuthorizationFragmentToRegisterFragment(snUser))
    }

    override fun layout() = R.layout.fragment_authorization
}

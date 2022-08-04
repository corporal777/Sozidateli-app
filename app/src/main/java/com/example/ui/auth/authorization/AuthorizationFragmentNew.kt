package com.example.ui.auth.authorization

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.SnUser
import com.example.databinding.FragmentAuthorizationBinding
import com.example.interfaces.BackgroundImageFragment
import com.example.ui.auth.login.LoginFragmentArgs
import com.example.ui.base.BaseFragmentNew
import com.example.ui.views.FinishRegisterDialog
import com.example.util.AuthBackground
import javax.inject.Inject
import javax.inject.Provider

class AuthorizationFragmentNew : BaseFragmentNew<FragmentAuthorizationBinding>(),
    BackgroundImageFragment, AuthorizationContract.View {

    override val isLightStatus = false

    @InjectPresenter
    lateinit var presenter: AuthorizationPresenter

    @Inject
    lateinit var presenterProvider: Provider<AuthorizationPresenter>

    @ProvidePresenter
    fun providePresenter(): AuthorizationPresenter = presenterProvider.get().apply {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            ibEmail.setOnClickListener { presenter.onEmailClick() }
            ibLogin.setOnClickListener { presenter.onLoginClick() }
        }

    }

    override fun showLogin() {
        findNavController().navigate(
            R.id.login_fragment,
            LoginFragmentArgs.Builder("").build().toBundle()
        )
    }

    override fun showEmailRegistration() {
        findNavController().navigate(R.id.register_email_new_fragment)
    }

    override fun showSnRegistration(snUser: SnUser) {
    }

    override fun getFragmentBackgroundDrawable(): Drawable? {
        return AuthBackground.get(resources)
    }

    override fun layout() = R.layout.fragment_authorization
}

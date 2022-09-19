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
import com.example.ui.base.BaseFragmentNew
import com.example.ui.views.FinishRegisterDialog
import com.example.util.AuthBackground
import javax.inject.Inject
import javax.inject.Provider

class AuthorizationFragment : BaseFragmentNew<FragmentAuthorizationBinding>(),
    BackgroundImageFragment, AuthorizationContract.View {

    private var showFinishRegister = false
    override val isLightStatus = false

    @InjectPresenter
    lateinit var presenter: AuthorizationPresenter

    @Inject
    lateinit var presenterProvider: Provider<AuthorizationPresenter>

    @ProvidePresenter
    fun providePresenter(): AuthorizationPresenter = presenterProvider.get().apply {
        navArgs<AuthorizationFragmentArgs>().value.also {
            this@AuthorizationFragment.showFinishRegister = it.showFinishRegister ?: false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
//        ibFacebook.setOnClickListener { presenter.authFb() }
//        ibVk.setOnClickListener { presenter.authVk() }
//        ibOk.setOnClickListener { presenter.authOk() }
            ibEmail.setOnClickListener { presenter.onEmailClick() }
            ibLogin.setOnClickListener { presenter.onLoginClick() }
            if (showFinishRegister)
                FinishRegisterDialog(requireContext())
                    .setSelectCallback {

                    }
        }

    }

    override fun showLogin() {
        findNavController().navigate(AuthorizationFragmentDirections.loginToLoginEmailAction(""))
    }

    override fun showEmailRegistration() {
        findNavController().navigate(AuthorizationFragmentDirections.authorizationFragmentToRegisterEmailNewFragment())
    }

    override fun showSnRegistration(snUser: SnUser) {
        findNavController().navigate(
            AuthorizationFragmentDirections.authorizationFragmentToRegisterSnFragment(
                snUser
            )
        )
    }

    override fun getFragmentBackgroundDrawable(): Drawable? {
        return AuthBackground.get(resources)
    }

    override fun layout() = R.layout.fragment_authorization
}

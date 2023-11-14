package com.example.ui.auth.authorization

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.R
import com.example.data.models.SnUser
import com.example.databinding.FragmentAuthorizationBinding
import com.example.interfaces.BackgroundImageFragment
import com.example.ui.auth.login.LoginFragmentArgs
import com.example.ui.base.BaseFragment
import com.example.ui.views.FinishRegisterDialog
import com.example.util.AuthBackground
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class AuthorizationFragment : BaseFragment<FragmentAuthorizationBinding>(),
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
        val args = LoginFragmentArgs.Builder("").build().toBundle()
        findNavController().navigate(R.id.login_fragment, args)
    }

    override fun showEmailRegistration() {
        findNavController().navigate(R.id.register_email_new_fragment)
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

package com.example.ui.userprofile.passwordconfirm

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.databinding.FragmentConfirmPasswordBinding
import com.example.ui.base.BaseFragmentNew
import com.example.ui.userprofile.phoneconfirm.PhoneConfirmFragment.Companion.FROM_PROFILE
import com.example.ui.views.toolbar.SimpleTitleToolbar
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class PasswordConfirmFragment : BaseFragmentNew<FragmentConfirmPasswordBinding>(),
    PasswordConfirmContract.View, SimpleTitleToolbar {


    val args: PasswordConfirmFragmentArgs by navArgs()

    override fun layout() = R.layout.fragment_confirm_password

    @InjectPresenter
    lateinit var presenter: PasswordConfirmPresenter

    @Inject
    lateinit var presenterProvider: Provider<PasswordConfirmPresenter>

    @ProvidePresenter
    fun providePresenter(): PasswordConfirmPresenter = presenterProvider.get().apply {
        phone = args.phone
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarTitle(getString(R.string.status_profile_title_set))
        mBinding.apply {
            etPassword.onTextChanged { tilPassword.error = null }
            btnSave.setOnClickListener {
                val password = etPassword.text?.toString()
                if (!password.isNullOrEmpty()) presenter.onClickConfirmPassword(password)
            }
        }
    }

    override fun showConfirmPasswordError() {
        mBinding.tilPassword.error = getString(R.string.password_confirm_wrong_password)
    }

    override fun showPhoneConfirm(phone: String, password: String) {
        hideKeyboard()
        findNavController().navigate(
            PasswordConfirmFragmentDirections.passwordConfirmToPhoneConfirm(
                phone,
                password,
                FROM_PROFILE
            )
        )
    }
}
package com.example.ui.userprofile.common.password.reset

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.R
import com.example.databinding.FragmentResetPasswordBinding
import com.example.ui.base.BaseFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class ResetPasswordFragment() : BaseFragment<FragmentResetPasswordBinding>(),
    ResetPasswordContract.View {

    @InjectPresenter
    lateinit var presenter: ResetPasswordPresenter

    @Inject
    lateinit var presenterProvider: Provider<ResetPasswordPresenter>

    @ProvidePresenter
    fun providePresenter(): ResetPasswordPresenter = presenterProvider.get().apply {
        val args = ResetPasswordFragmentArgs.fromBundle(requireArguments())
        recoverCode = args.code ?: ""
        userId = args.userId ?: ""
        loginType = args.loginType ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            passwordView.setPasswordValidCallback {
                presenter.onChangePasswordText(it.password, it.isValid)
            }
            btnReset.setOnClickListener {
                presenter.onRecoveryPasswordClick()
            }
            btnClose.setOnClickListener {
                navigateUp()
            }
        }
    }

    override fun enableBtnResetPassword(enable: Boolean) {
        mBinding.btnReset.isEnabled = enable
    }

    override fun showPasswordError(show: Boolean) {
        mBinding.passwordView.showErrors(show)
    }

    override fun showCustomLoading() = mBinding.btnReset.showProgressLoading(true)
    override fun hideCustomLoading() = mBinding.btnReset.showProgressLoading(false)

    override fun layout(): Int = R.layout.fragment_reset_password
}
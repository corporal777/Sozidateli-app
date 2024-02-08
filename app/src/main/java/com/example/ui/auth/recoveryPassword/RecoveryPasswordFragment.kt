package com.example.ui.auth.recoveryPassword

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.R
import com.example.databinding.FragmentRecoveryPasswordBinding
import com.example.ui.base.BaseFragment
import com.example.ui.userprofile.edit.password.confirm.PhoneConfirmPasswordFragmentArgs
import com.example.ui.userprofile.edit.password.confirm.EmailConfirmPasswordDialog
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class RecoveryPasswordFragment : BaseFragment<FragmentRecoveryPasswordBinding>(),
    RecoveryPasswordContract.View {

    @InjectPresenter
    lateinit var presenter: RecoveryPasswordPresenter

    @Inject
    lateinit var presenterProvider: Provider<RecoveryPasswordPresenter>

    @ProvidePresenter
    fun providePresenter(): RecoveryPasswordPresenter = presenterProvider.get().apply {
        email = RecoveryPasswordFragmentArgs.fromBundle(requireArguments()).email ?: ""
    }

    private var emailConfirmDialog : EmailConfirmPasswordDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            etLogin.initInput {
                presenter.onChangeEmailText(it.toString())
            }
            btnRecovery.setOnClickListener {
                presenter.onRecoveryClick()
                hideKeyboard()
            }
            ibClose.setOnClickListener { navigateUp() }
        }

    }

    override fun setEmail(email: String) {
        mBinding.etLogin.setText(email)
    }

    override fun enableRecoveryBtn(isEnable: Boolean) {
        mBinding.btnRecovery.apply { isEnabled = isEnable }
    }

    override fun showEmailError(show: Boolean) {
        mBinding.tvEmailError.isVisible = show
        mBinding.etLogin.apply {
            if (show) showTextError(getString(R.string.recovery_password_user_not_found_error))
            else showError(false)
        }
    }

    override fun showEmailRecovery(email: String, userId: String) {
        EmailConfirmPasswordDialog(requireContext(), email)
            .setOnDismissCallback { navigateUp() }
            .setOnSendAgainCallback { presenter.onSendEmailAgainClick() }
            .apply { if (emailConfirmDialog == null) emailConfirmDialog = this }
            .show()
        showEmailSendTimer()
    }

    override fun showEmailSendTimer() {
        if (emailConfirmDialog != null) emailConfirmDialog!!.starTimer()
    }

    override fun showPhoneRecovery(phone: String, userId: String) {
        val args = PhoneConfirmPasswordFragmentArgs.Builder(phone, userId).build().toBundle()
        findNavController().navigate(R.id.passwordConfirmFragment, args)
    }

    override fun showCustomLoading() = mBinding.btnRecovery.showProgressLoading(true)
    override fun hideCustomLoading() = mBinding.btnRecovery.showProgressLoading(false)

    override fun layout() = R.layout.fragment_recovery_password
}

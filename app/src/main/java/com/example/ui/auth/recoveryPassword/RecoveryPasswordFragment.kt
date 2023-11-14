package com.example.ui.auth.recoveryPassword

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.util.Linkify
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.toSpannable
import androidx.navigation.fragment.findNavController
import com.example.R
import com.example.databinding.FragmentRecoveryPasswordBinding
import com.example.ui.base.BaseFragment
import com.example.ui.views.ConfirmCodeDialog
import com.example.ui.views.NewPasswordDialog
import com.example.ui.views.RegisterDataType
import com.example.ui.views.dialogs.MessageDialogWithBrownButton
import com.example.util.URLSpanNoUnderline
import com.example.util.getEmailFilter
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import onTextChanged
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

    private var dialog: ConfirmCodeDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            etEmail.apply {
                filters = getEmailFilter()
                onTextChanged { it?.toString()?.let { text -> presenter.onChangeEmailText(text) } }
            }

            btnRecovery.setOnClickListener {
                presenter.onRecoveryClick()
                hideKeyboard()
            }
            ibClose.setOnClickListener { presenter.onCloseClick() }
        }

    }

    override fun setEmail(email: String) {
        mBinding.etEmail.setText(email)
    }

    override fun enableRecoveryBtn(isEnable: Boolean) {
        mBinding.btnRecovery.apply { isEnabled = isEnable }
    }

    override fun showEmailError(show: Boolean) {
        mBinding.tilEmail.apply {
            if (show) showError(getString(R.string.auth_error_wrong_email))
            else showError(null)
        }
    }

    override fun showRecoveryNotification(email: String, userId: String) {
        if (presenter.loginType == "email") {
            val message = SpannableStringBuilder(
                getString(R.string.recovery_confirm_email_message).format(email)
            ).apply {
                val supportEmail = getString(R.string.support_email).toSpannable()
                Linkify.addLinks(supportEmail, Linkify.EMAIL_ADDRESSES)
                append(" ")
                append(supportEmail)
                append(".")
            }
            MessageDialogWithBrownButton(requireContext(), message, false)
                .setSelectCallback { findNavController().navigateUp() }
        } else {
            dialog = ConfirmCodeDialog(email, requireContext(), RegisterDataType.PHONE)
            dialog.let { d ->
                d?.setSendAgainCallback { presenter.sendCodeAgain() }
                d?.setConfirmCallback { setPassword(it, userId) }
            }
        }
    }

    override fun setTimeLeft(seconds: Int) {
        if (dialog != null) dialog?.setTimeLeft(seconds)
    }

    private fun setPassword(code: String, userId: String) {
        NewPasswordDialog(requireActivity()).setSelectCallback {
            presenter.onSetPassword(code, it, userId)
        }
    }

    override fun showWrongEmailError() {
        val type = if (presenter.loginType == "email") "E-mail" else "телефон"
        val message = getString(R.string.recovery_password_wrong_email_error, type)
        showErrorMessage(false, message)
    }

    override fun showPasswordSuccessUpdated() = showToast(getString(R.string.profile_password_change_complete))
    override fun showCustomLoading() = mBinding.btnRecovery.showProgressLoading(true)
    override fun hideCustomLoading() = mBinding.btnRecovery.showProgressLoading(false)

    override fun layout() = R.layout.fragment_recovery_password
}

package com.example.ui.auth.recoveryPassword

import android.os.Bundle
import android.text.InputFilter
import android.text.SpannableStringBuilder
import android.text.util.Linkify
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.toSpannable
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.databinding.FragmentRecoveryPasswordBinding
import com.example.ui.base.BaseFragment
import com.example.ui.views.ConfirmCodeDialog
import com.example.ui.views.NewPasswordDialog
import com.example.ui.views.RegisterDataType
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class RecoveryPasswordFragment : BaseFragment<FragmentRecoveryPasswordBinding>(),
    RecoveryPasswordContract.View {

    @InjectPresenter
    lateinit var presenter: RecoveryPasswordPresenter

    @Inject
    lateinit var presenterProvider: Provider<RecoveryPasswordPresenter>
    private var dialog: ConfirmCodeDialog? = null

    private val emailFilter = arrayOf(InputFilter { source, _, _, _, _, _ ->
        source.toString().filter {
            it.isLetter() || it.isDigit() || it == '.' || it == '@' || it == '_' || it == '+'
        }
    })

    @ProvidePresenter
    fun providePresenter(): RecoveryPasswordPresenter = presenterProvider.get().apply {
        arguments?.let {
            email = RecoveryPasswordFragmentArgs.fromBundle(it).email ?: ""
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            etEmail.apply {
                filters = emailFilter
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
        mBinding.tilEmail.error = if (show) getString(R.string.auth_error_wrong_email) else null
    }

    override fun showRecoveryNotification(email: String, userId: String) {
        if (presenter.loginType == "email") {
            val supportEmail = getString(R.string.support_email).toSpannable()
            Linkify.addLinks(supportEmail, Linkify.EMAIL_ADDRESSES)

            val message = SpannableStringBuilder(
                getString(R.string.recovery_confirm_email_message).format(email)
            )
                .append(" ")
                .append(supportEmail)
                .append(".")

            AlertDialog.Builder(requireContext())
                .setMessage(message)
                .setPositiveButton(R.string.ok) { _, _ -> presenter.onUserUnderstand() }
                .setOnCancelListener { presenter.onUserUnderstand() }
                .setOnDismissListener { presenter.onUserUnderstand() }
                .show()
                .apply {
                    findViewById<TextView>(android.R.id.message)?.let {
                        it.movementMethod = BetterLinkMovementMethod.getInstance()
                    }
                }
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
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.error_title)
            .setMessage(
                requireContext().resources.getString(
                    R.string.recovery_password_wrong_email_error,
                    type
                )
            )
            .setPositiveButton(R.string.ok) { _, _ ->
                // do nothing
            }
            .show()
    }

    override fun showPasswordSuccessUpdated() {
        showToast(getString(R.string.profile_password_change_complete))
    }

    override fun layout() = R.layout.fragment_recovery_password
}

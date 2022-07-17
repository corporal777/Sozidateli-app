package com.example.ui.auth.recoveryPassword

import android.os.Bundle
import android.text.InputFilter
import android.text.SpannableStringBuilder
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.toSpannable
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.databinding.FragmentRecoveryPasswordBinding
import com.example.ui.base.BaseFragment
import com.example.ui.base.BaseFragmentNew
import com.example.ui.main.MainActivity
import com.example.ui.views.AddPhoneEmailDialog
import com.example.ui.views.NewPasswordDialog
import com.example.ui.views.RegisterDataType
import com.example.util.AuthValidateUtil
import com.example.util.SimpleTextWatcher
import kotlinx.android.synthetic.main.dialog_password_recovery.view.*
import kotlinx.android.synthetic.main.fragment_recovery_password.*
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import onFocusChanged
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class RecoveryPasswordFragment : BaseFragmentNew<FragmentRecoveryPasswordBinding>(), RecoveryPasswordContract.View {

    @InjectPresenter
    lateinit var presenter: RecoveryPasswordPresenter

    @Inject
    lateinit var presenterProvider: Provider<RecoveryPasswordPresenter>

    private val emailFilter = arrayOf(InputFilter { source, _, _, _, _, _ ->
        source.toString().filter {
            it.isLetter() || it.isDigit() || it == '.' || it == '@' || it == '_'
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
            btnRecovery.setOnClickListener { presenter.onRecoveryClick() }
//            etEmail.addTextChangedListener(SimpleTextWatcher().setOnTextChangeRunnable { charSequence, _, _, _ ->
//                presenter.onChangeEmailText(charSequence.toString())
//            })

            etEmail.apply {
                filters = emailFilter
                onTextChanged { it?.toString()?.let { text -> presenter.onChangeEmailText(text) } }
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

    override fun showRecoveryNotification(email: String) {
        if (presenter.loginType == "email") {
            val supportEmail = getString(R.string.support_email).toSpannable()
            Linkify.addLinks(supportEmail, Linkify.EMAIL_ADDRESSES)

            val message = SpannableStringBuilder(getString(R.string.recovery_confirm_email_message).format(email))
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
            val dialog = AddPhoneEmailDialog(requireActivity(), RegisterDataType.CODE)
            dialog.setPhoneForCode(email)
            dialog.setSelectCallback {
                if (it.type == RegisterDataType.CODE) {
                    setPassword(it.value)
                    dialog.hideDialog()
                }
            }
            dialog.setSendCodeCallback {

            }
        }
    }

    private fun setPassword(code: String) {
        NewPasswordDialog(requireActivity())
                .setSelectCallback {
                    presenter.onSetPassword(/*email,*/ code, it)
                }
    }

    override fun showWrongEmailError() {
        val type = if (presenter.loginType == "email") "E-mail" else "телефон"
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.error_title)
                .setMessage(requireContext().resources.getString(R.string.recovery_password_wrong_email_error, type))
                .setPositiveButton(R.string.ok) { _, _ ->
                    // do nothing
                }
                .show()
    }

    override fun layout() = R.layout.fragment_recovery_password
}

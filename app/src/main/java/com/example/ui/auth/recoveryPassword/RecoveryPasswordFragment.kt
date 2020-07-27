package com.example.ui.auth.recoveryPassword

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.util.Linkify
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.toSpannable
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseFragment
import com.example.util.SimpleTextWatcher
import kotlinx.android.synthetic.main.fragment_recovery_password.*
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import javax.inject.Inject
import javax.inject.Provider

class RecoveryPasswordFragment : BaseFragment(), RecoveryPasswordContract.View {

    @InjectPresenter
    lateinit var presenter: RecoveryPasswordPresenter

    @Inject
    lateinit var presenterProvider: Provider<RecoveryPasswordPresenter>

    @ProvidePresenter
    fun providePresenter(): RecoveryPasswordPresenter = presenterProvider.get().apply {
        arguments?.let {
            email = RecoveryPasswordFragmentArgs.fromBundle(it).email ?: ""
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnRecovery.setOnClickListener { presenter.onRecoveryClick() }
        etEmail.addTextChangedListener(SimpleTextWatcher().setOnTextChangeRunnable { charSequence, _, _, _ ->
            presenter.onChangeEmailText(charSequence.toString())
        })

        ibClose.setOnClickListener { presenter.onCloseClick() }
    }

    override fun setEmail(email: String) {
        etEmail.setText(email)
    }

    override fun enableRecoveryBtn(isEnable: Boolean) {
        btnRecovery.apply { isEnabled = isEnable }
    }

    override fun showEmailError(show: Boolean) {
        tilEmail.error = if (show) getString(R.string.auth_error_wrong_email) else null
    }

    override fun showRecoveryNotification(email: String) {
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
    }

    override fun layout() = R.layout.fragment_recovery_password
}

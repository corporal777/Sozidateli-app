package com.example.ui.auth.recoveryPassword

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseFragment
import com.example.util.SimpleTextWatcher
import kotlinx.android.synthetic.main.fragment_recovery_password.*
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
    }

    override fun setEmail(email: String) {
        etEmail.setText(email)
    }

    override fun showHelpDialog(email: String) {
        AlertDialog.Builder(requireContext())
                .setMessage(getString(R.string.recovery_confirm_email_message).format(email))
                .setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
                .show()
    }

    override fun enableRecoveryBtn(isEnable: Boolean) {
        btnRecovery.apply {
            isEnabled = isEnable

            val background: Int
            val textColor: Int
            if (isEnable) {
                background = R.drawable.background_btn_auth
                textColor = Color.WHITE
            } else {
                background = R.drawable.background_disabled_btn_login
                textColor = ContextCompat.getColor(context, R.color.disabled_color)
            }

            setBackgroundResource(background)
            setTextColor(textColor)
        }
    }

    override fun isShowToolbar() = true



    override fun layout() = R.layout.fragment_recovery_password
}

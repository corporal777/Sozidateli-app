package com.example.ui.auth.confirm

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.navigation.fragment.navArgs
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_email_confirm.*
import javax.inject.Inject
import javax.inject.Provider

class EmailConfirmFragment : BaseFragment(), EmailConfirmContract.View {

    @InjectPresenter
    lateinit var presenter: EmailConfirmPresenter

    @Inject
    lateinit var presenterProvider: Provider<EmailConfirmPresenter>

    @ProvidePresenter
    fun providePresenter(): EmailConfirmPresenter = presenterProvider.get().apply {
        navArgs<EmailConfirmFragmentArgs>().value.also {
            email = it.email
            password = it.password ?: ""
            snUser = it.snUser
        }
    }

    private val timerMessage by lazy {
        getString(R.string.auth_register_confirm_email_timer)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val supportEmail = getString(R.string.support_email)
        val description = getString(R.string.auth_register_confirm_email_message).format(supportEmail)
        tvMessage.text = description
        btnResend.apply {
            setTextColor(ColorStateList(
                    arrayOf(intArrayOf(android.R.attr.state_enabled), intArrayOf(-android.R.attr.state_enabled)),
                    intArrayOf(ContextCompat.getColor(requireContext(), R.color.colorAccent), ContextCompat.getColor(requireContext(), R.color.action_button_disabled_text_color))
            ))
            setOnClickListener { presenter.onResendClick() }
        }

        ibClose.setOnClickListener { presenter.onCloseClick() }
    }

    override fun setTimeLeft(seconds: Int) {
        val quantity = resources.getQuantityString(R.plurals.seconds_timer, seconds, seconds)
        tvTimer.text = String.format(timerMessage, quantity)
    }

    override fun setCanResend(canResend: Boolean) {
        btnResend.isEnabled = canResend
        tvTimer.isInvisible = canResend
    }

    override fun layout() = R.layout.fragment_email_confirm
}

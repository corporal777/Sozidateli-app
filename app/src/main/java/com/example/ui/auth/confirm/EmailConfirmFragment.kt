package com.example.ui.auth.confirm

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.navigation.fragment.navArgs
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.databinding.FragmentEmailConfirmBinding
import com.example.ui.base.BaseFragment
import com.example.ui.views.FinishRegisterDialog
import com.example.util.Utils
import kotlinx.android.synthetic.main.fragment_email_confirm.*
import javax.inject.Inject
import javax.inject.Provider

class EmailConfirmFragment : BaseFragment<FragmentEmailConfirmBinding>(), EmailConfirmContract.View {

    @InjectPresenter
    lateinit var presenter: EmailConfirmPresenter

    @Inject
    lateinit var presenterProvider: Provider<EmailConfirmPresenter>

    @ProvidePresenter
    fun providePresenter(): EmailConfirmPresenter = presenterProvider.get().apply {
    }

    private val timerMessage by lazy {
        getString(R.string.auth_register_confirm_email_timer)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showDialog()
        val agreementText = SpannableString(getString(R.string.register_confirm_email_text).format(presenter.email)).apply {
            val linkStart = 3
            val linkEnd = 3 + presenter.email.length
            setSpan(StyleSpan(Typeface.BOLD), linkStart, linkEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        }
        tvMessage.text = agreementText
        btnResend.apply {
            setTextColor(ColorStateList(
                    arrayOf(intArrayOf(android.R.attr.state_enabled), intArrayOf(-android.R.attr.state_enabled)),
                    intArrayOf(ContextCompat.getColor(requireContext(), R.color.colorAccent), ContextCompat.getColor(requireContext(), R.color.action_button_disabled_text_color))
            ))
            setOnClickListener { presenter.onResendClick() }
        }

        ibClose.setOnClickListener { presenter.onCloseClick() }
    }

    private fun showDialog() {
        FinishRegisterDialog(requireContext())
                .setSelectCallback {

                }
    }

    override fun setTimeLeft(seconds: Int) {
        //val quantity = resources.getQuantityString(R.plurals.seconds_timer, seconds, seconds)
        val quantity = Utils.timerFormatter(seconds, requireContext())
        tvTimer.text = String.format(timerMessage, quantity)
    }

    override fun setCanResend(canResend: Boolean) {
        btnResend.isEnabled = canResend
        tvTimer.isInvisible = canResend
    }

    override fun layout() = R.layout.fragment_email_confirm
}

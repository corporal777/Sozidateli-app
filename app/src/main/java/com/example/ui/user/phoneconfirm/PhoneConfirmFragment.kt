package com.example.ui.user.phoneconfirm

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_confirm_phone.*
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class PhoneConfirmFragment : BaseFragment(), PhoneConfirmContract.View, ToolbarFragment {

    override val title: String? by lazy {
        getString(R.string.status_profile_title_set)
    }

    override fun layout() = R.layout.fragment_confirm_phone

    private val args: PhoneConfirmFragmentArgs by navArgs()

    private val timerMessage by lazy { getString(R.string.phone_confirm_timer) }

    @InjectPresenter
    lateinit var presenter: PhoneConfirmPresenter

    @Inject
    lateinit var presenterProvider: Provider<PhoneConfirmPresenter>

    @ProvidePresenter
    fun providePresenter(): PhoneConfirmPresenter = presenterProvider.get().apply {
        phone = args.phone
        password = args.password
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnResend.setOnClickListener { presenter.onResendClick() }
        btnSave.setOnClickListener {
            val code = etCode.text?.toString()
            if (!code.isNullOrEmpty()) presenter.onCodeSendClick(code)
        }
        etCode.onTextChanged { tilCode.error = null }
    }

    override fun setCanResend(canResend: Boolean) {
        btnResend.isEnabled = canResend
    }

    override fun setTimeLeft(time: String?) {
        if (time == null) {
            tvTimer.isVisible = false
        } else {
            tvTimer.text = String.format(timerMessage, time)
            tvTimer.isVisible = true
        }
    }

    override fun setPhone(phone: String) {
        tvDescription.text = getString(R.string.phone_confirm_description, phone)
    }

    override fun showSendSmsError() {
        showToast(R.string.phone_confirm_code_send_error)
    }

    override fun showWrongCodeError() {
        tilCode.error = getString(R.string.phone_confirm_wrong_code)
    }

    override fun onPhoneConfirmationComplete() {
        findNavController().navigateUp()
    }
}
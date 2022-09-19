package com.example.ui.userprofile.phoneconfirm

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.databinding.FragmentConfirmPhoneBinding
import com.example.ui.base.BaseFragmentNew
import com.example.ui.main.MainActivity
import com.example.ui.views.toolbar.SimpleTitleToolbar
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class PhoneConfirmFragment : BaseFragmentNew<FragmentConfirmPhoneBinding>(),
    PhoneConfirmContract.View, SimpleTitleToolbar {

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
        screenType = args.screenType ?: FROM_OTHER
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarTitleAndIcon(getString(R.string.status_profile_title_set))
        mBinding.apply {
            btnResend.setOnClickListener { presenter.onResendClick() }
            btnSave.setOnClickListener {
                val code = etCode.text?.toString()
                if (!code.isNullOrEmpty()) {
                    (requireActivity() as MainActivity).startEditPhoneListener(true)
                    presenter.onCodeSendClick(code)
                } else (requireActivity() as MainActivity).startEditPhoneListener(true)
            }
            etCode.onTextChanged { tilCode.error = null }
        }
    }

    override fun setCanResend(canResend: Boolean) {
        mBinding.btnResend.isEnabled = canResend
    }

    override fun setTimeLeft(time: String?) {
        if (time == null) {
            mBinding.tvTimer.isVisible = false
        } else {
            mBinding.tvTimer.text = String.format(timerMessage, time)
            mBinding.tvTimer.isVisible = true
        }
    }

    override fun setPhone(phone: String) {
        mBinding.tvDescription.text = getString(R.string.phone_confirm_description, phone)
    }

    override fun showSendSmsError() {
        showToast(R.string.phone_confirm_code_send_error)
    }

    override fun showWrongCodeError() {
        mBinding.tilCode.error = getString(R.string.phone_confirm_wrong_code)
        (requireActivity() as MainActivity).startEditPhoneListener(false)
    }

    override fun onPhoneConfirmationComplete() {
        findNavController().navigateUp()
    }

    companion object {
        const val FROM_PROFILE = 1
        const val FROM_OTHER = 2
    }
}
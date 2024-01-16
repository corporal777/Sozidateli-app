package com.example.ui.userprofile.edit.confirm

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.util.Linkify
import android.view.View
import androidx.core.text.toSpannable
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.example.R
import com.example.databinding.BottomSheetConfirmPhoneBinding
import com.example.ui.base.bottomSheet.BaseBottomSheetFragment
import com.example.ui.main.MainActivity
import com.example.ui.views.expandableTextView.CustomTypefaceSpan
import com.example.util.Utils
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class ConfirmEmailPhoneFragment(val phone: String) :
    BaseBottomSheetFragment<BottomSheetConfirmPhoneBinding>(), ConfirmEmailPhoneContract.View {


    private val timerEmailMessage by lazy { getString(R.string.auth_register_confirm_email_timer_two) }
    private val timerPhoneMessage by lazy { getString(R.string.auth_register_confirm_phone_timer) }

    private var confirmEmailPhone: () -> Unit = {}

    @InjectPresenter(tag = CONFIRM_PHONE_FRAGMENT_TAG)
    lateinit var presenter: ConfirmEmailPhonePresenter

    @Inject
    lateinit var presenterProviderEmail: Provider<ConfirmEmailPhonePresenter>

    @ProvidePresenter(tag = CONFIRM_PHONE_FRAGMENT_TAG)
    fun providePresenter(): ConfirmEmailPhonePresenter = presenterProviderEmail.get().apply {
        initLoginType(phone)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            etCode.onTextChanged {
                if (presenter.loginType == "email") setCodeError(it.toString().length != 6)
                else setCodeError(it.toString().length != 4)
                btnConfirm.isEnabled = !it.isNullOrEmpty()
            }
            btnConfirm.apply {
                isEnabled = false
                setOnClickListener {
                    hideKeyboard(it)
                    (requireActivity() as MainActivity).setIgnoreTokenListener(true)
                    presenter.confirmEmailPhone(phone, etCode.text.toString())
                }
            }
            btnResend.setOnClickListener { presenter.sendCodeAgain() }
            btnClose.setOnClickListener { dismiss() }
        }
    }

    override fun setContentType(type: String) {
        mBinding.apply {
            if (type == "email") {
                tvBottomSheetLabel.text = getString(R.string.status_profile_confirm_email_title)
                etCode.setHint(R.string.code_email_input_label)

                tvPhoneDescription.text = getEmailFormattedDescription(
                    getString(R.string.code_email_dialog_text, phone),
                    phone
                )

                tvPhoneInformation.apply {
                    val supportEmail = getString(R.string.support_email)
                    val message =
                        getString(R.string.code_dialog_text_information).format(supportEmail)
                            .toSpannable()
                    Linkify.addLinks(message, Linkify.EMAIL_ADDRESSES)
                    text = message
                    movementMethod = BetterLinkMovementMethod.getInstance()
                }
            } else {
                tvBottomSheetLabel.text = getString(R.string.status_profile_title_set_new)
                btnResend.text = getString(R.string.send_call_again)
                etCode.setHint(R.string.code_phone_input_label)
                tvPhoneInformation.isVisible = false

                tvPhoneDescription.text = getPhoneFormattedDescription(
                    "Введите последние 4 цифры номера входящего звонка на номер:",
                    phone
                )
            }

            focusOnInput(etCode, true)
        }
    }

    override fun setButtonSendAgain(enable: Boolean) {
        mBinding.btnResend.isEnabled = enable
    }

    override fun setTimeLeft(time: Int) {
        val quantity = Utils.timerFormatter(time, requireContext())
        val visible = time <= 0
        val desc = if (presenter.loginType == "email")
            String.format(timerEmailMessage, quantity)
        else String.format(timerPhoneMessage, quantity)

        mBinding.tvTimer.apply {
            isInvisible = visible
            text = desc
        }
    }

    override fun setEmailPhoneIsConfirmed() {
        confirmEmailPhone.invoke()
        (requireActivity() as MainActivity).setIgnoreTokenListener(false)
        dismiss()
    }

    override fun setCodeError(show: Boolean) {
        if (show) mBinding.tilCode.error = getString(R.string.auth_error_code)
        else mBinding.tilCode.error = null
    }

    fun setConfirmCallback(block: () -> Unit): ConfirmEmailPhoneFragment {
        confirmEmailPhone = block
        return this
    }



    private fun getEmailFormattedDescription(text: String, login: String): SpannableString {
        return SpannableString(text).apply {
            val font =
                Typeface.createFromAsset(
                    requireContext().assets,
                    "fonts/sf_pro_display_bold.ttf"
                )
            setSpan(
                CustomTypefaceSpan("", font),
                9,
                9 + login.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun getPhoneFormattedDescription(
        text: String,
        login: String
    ): SpannableStringBuilder {
        val phone = SpannableString(login).apply {
            val font =
                Typeface.createFromAsset(
                    requireContext().assets,
                    "fonts/sf_pro_display_bold.ttf"
                )
            setSpan(
                CustomTypefaceSpan("", font),
                0,
                login.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return SpannableStringBuilder(text).append("\n").append("\n").append(phone)
    }


    fun show(fragmentManager: FragmentManager) {
        show(fragmentManager, "confirm_email_phone")
    }

    companion object {
        const val CONFIRM_PHONE_FRAGMENT_TAG = "confirm_phone_tag"
    }

    override fun layout(): Int = R.layout.bottom_sheet_confirm_phone
}
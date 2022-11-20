package com.example.ui.userprofile.read.settings.confirm_phone_email

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.util.Linkify
import android.view.View
import androidx.core.text.toSpannable
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.databinding.BottomSheetConfirmPhoneBinding
import com.example.ui.base.bottomSheet.BaseBottomSheetFragment
import com.example.ui.main.MainActivity
import com.example.ui.views.expandableTextView.CustomTypefaceSpan
import com.example.util.Utils
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class ConfirmEmailPhoneFragment(val phone: String) :
    BaseBottomSheetFragment<BottomSheetConfirmPhoneBinding>(), ConfirmEmailPhoneContract.View {


    private var confirmEmailPhone: () -> Unit = {}

    @InjectPresenter(type = PresenterType.WEAK, tag = CONFIRM_PHONE_FRAGMENT_TAG)
    lateinit var presenterEmail: ConfirmEmailPhonePresenter

    @Inject
    lateinit var presenterProviderEmail: Provider<ConfirmEmailPhonePresenter>

    @ProvidePresenter(type = PresenterType.WEAK, tag = CONFIRM_PHONE_FRAGMENT_TAG)
    fun providePresenter(): ConfirmEmailPhonePresenter = presenterProviderEmail.get().apply {
        this.mobilePhone = phone
        loginType = if (Utils.isPhone(phone) && !Utils.isContainLetters(phone)) {
            "phone"
        } else {
            "email"
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            etCode.onTextChanged {
                setCodeError(it.toString().length != 6)
                btnConfirm.isEnabled = !it.isNullOrEmpty()
            }
            btnConfirm.apply {
                isEnabled = false
                setOnClickListener {
                    hideKeyboard(it)
                    (requireActivity() as MainActivity).setIgnoreTokenListener(true)
                    presenterEmail.confirmEmailPhone(phone, etCode.text.toString())
                }
            }
            btnResend.apply {
                isEnabled = false
                setOnClickListener {
                    presenterEmail.sendCode()
                }
            }
            btnClose.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun setContentType(type: String) {
        mBinding.apply {
            if (type == "email") {
                tvBottomSheetLabel.text = getString(R.string.status_profile_confirm_email_title)
                etCode.setHint(R.string.code_email_input_label)

                tvPhoneDescription.text = getFormattedDescription(
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
                etCode.setHint(R.string.code_phone_input_label)
                tvPhoneInformation.isVisible = false

                tvPhoneDescription.text = getFormattedDescription(
                    getString(R.string.code_phone_dialog_text, phone),
                    phone
                )
            }

            contentContainer.visibility = View.VISIBLE
            focusOnInput(etCode, true)
        }
    }

    override fun setButtonSendAgain(enable: Boolean) {
        mBinding.btnResend.isEnabled = enable
    }

    override fun setTimeLeft(time: Int) {
        val quantity = Utils.timerFormatterNew(time, requireContext())
        mBinding.tvTimer.apply {
            if (time <= 0) {
                isInvisible = true
            } else {
                isInvisible = false
                text = String.format(
                    getString(R.string.auth_register_confirm_email_timer_two),
                    quantity
                )
            }
        }
    }

    override fun setEmailPhoneIsConfirmed() {
        confirmEmailPhone.invoke()
        (requireActivity() as MainActivity).setIgnoreTokenListener(false)
        dismiss()
    }

    override fun setCodeError(show: Boolean) {
        if (show) {
            mBinding.tilCode.error = getString(R.string.auth_error_code)
        } else {
            mBinding.tilCode.error = null
        }
    }

    override fun showProgressLoading() {
        mBinding.progressView.showProgressBar()
    }

    override fun hideProgressLoading() {
        mBinding.progressView.hideProgressBar()
    }

    fun setConfirmCallback(block: () -> Unit): ConfirmEmailPhoneFragment {
        confirmEmailPhone = block
        return this
    }

    companion object {
        const val CONFIRM_PHONE_FRAGMENT_TAG = "confirm_phone_tag"
    }

    private fun getFormattedDescription(text: String, login: String): SpannableString {
        return SpannableString(text).apply {
            val font =
                Typeface.createFromAsset(requireContext().assets, "fonts/sf_pro_display_bold.ttf")
            setSpan(
                CustomTypefaceSpan("", font),
                9,
                9 + login.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }


    override fun layout(): Int = R.layout.bottom_sheet_confirm_phone
}
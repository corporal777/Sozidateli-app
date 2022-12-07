package com.example.ui.auth.register.email.finishregister.newbuild

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.InputFilter
import android.text.SpannableStringBuilder
import android.text.util.Linkify
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.databinding.FragmentFinishRegisterNewBinding
import com.example.ui.base.BaseFragmentNew
import com.example.ui.main.MainActivity
import com.example.ui.views.AddPhoneEmailDialog
import com.example.util.AuthValidateUtil
import com.example.util.Utils
import com.example.util.initSwitch
import com.example.util.removeFirstAndLastSpaces
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import onFocusChanged
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class FinishRegisterNewFragment : BaseFragmentNew<FragmentFinishRegisterNewBinding>(),
    FinishRegisterNewContract.View {

    private val timerMessage by lazy {
        getString(R.string.auth_register_confirm_email_timer_two)
    }

    private val filter = arrayOf(InputFilter { source, _, _, _, _, _ ->
        source.toString().filter {
            it.isLetter() || it == '-' || it == ' '
        }
    })

    private val emailFilter = arrayOf(InputFilter { source, _, _, _, _, _ ->
        source.toString().filter {
            it.isLetter() || it.isDigit() || it == '.' || it == '@' || it == '_' || it == '+'
        }
    })

    @InjectPresenter
    lateinit var presenter: FinishRegisterNewPresenter

    @Inject
    lateinit var presenterProviderFinish: Provider<FinishRegisterNewPresenter>

    @ProvidePresenter
    fun providePresenter(): FinishRegisterNewPresenter = presenterProviderFinish.get().apply {
        val args = requireArguments().let { FinishRegisterNewFragmentArgs.fromBundle(it) }
        firstName = args.name
        lastName = args.lastName
        middleName = args.middleName ?: ""
        noMiddleNameChecked = args.isNoMiddleName
        phone = args.phone ?: ""
        email = args.email
        loginType = if (email.isNullOrEmpty()) "phone" else "email"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            ivClose.setOnClickListener { findNavController().navigateUp() }
            scNoMiddleName.initSwitch(presenter.noMiddleNameChecked) {
                presenter.onNoMiddleNameChecked(it)
            }
            etFirstName.apply {
                filters = filter
                onTextChanged { it?.toString()?.let { text -> presenter.onChangeNameText(text) } }
                onFocusChanged { hasFocus ->
                    if (!hasFocus) {
                        setText(removeFirstAndLastSpaces(text.toString()))
                    }
                }
            }
            etLastName.apply {
                filters = filter
                onTextChanged {
                    it?.toString()?.let { text -> presenter.onChangeLastNameText(text) }
                }
                onFocusChanged { hasFocus ->
                    if (!hasFocus) {
                        setText(removeFirstAndLastSpaces(text.toString()))
                    }
                }
            }
            etMiddleName.apply {
                filters = filter
                onTextChanged {
                    it?.toString()?.let { text -> presenter.onChangeMiddleNameText(text) }
                }
                onFocusChanged { hasFocus ->
                    if (!hasFocus) {
                        setText(removeFirstAndLastSpaces(text.toString()))
                    }
                }
            }
            etEmail.apply {
                filters = emailFilter
                onTextChanged {
                    tilEmail.error = null
                    it?.toString()?.let { text -> presenter.onChangeEmailText(text) }
                }
                onFocusChanged { hasFocus ->
                    if (!hasFocus) {
                        if (AuthValidateUtil.isDigits(text.toString())) {
                            if (!AuthValidateUtil.isValidPhone(text.toString())) {
                                showWrongPhoneError(true)
                            }
                        } else {
                            if (!AuthValidateUtil.isValidEmail(text.toString())) {
                                showWrongEmailError(true)
                            }
                        }
                    }
                }
            }
            etCode.onTextChanged {
                tilCode.error = null
                it?.toString()?.let { text -> presenter.onChangeCodeText(text) }
            }
            btnResend.apply {
                setTextColor(
                    ColorStateList(
                        arrayOf(
                            intArrayOf(android.R.attr.state_enabled),
                            intArrayOf(-android.R.attr.state_enabled)
                        ),
                        intArrayOf(
                            ContextCompat.getColor(requireContext(), R.color.colorAccent),
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.input_text_color_disabled_new
                            )
                        )
                    )
                )
                setOnClickListener { presenter.sendCodeAgain() }
            }
            ibCancel.setOnClickListener {
                hideKeyboard()
                (requireActivity() as MainActivity).setIgnoreTokenListener(true)
                presenter.logout()
            }
            ibRegister.setOnClickListener {
                hideKeyboard()
                if (etCode.text?.length != AddPhoneEmailDialog.CODE_SIZE) {
                    tilCode.error = resources.getString(R.string.auth_error_no_code)
                } else {
                    (requireActivity() as MainActivity).setIgnoreTokenListener(true)
                    presenter.onHandleAuthLink()
                }
            }
        }
    }

    override fun setData(
        email: String?,
        firstName: String?,
        lastName: String?,
        middleName: String?
    ) {
        mBinding.apply {
            etEmail.setText(email)

            etFirstName.setText(firstName)
            etLastName.setText(lastName)
            if (middleName == "-" || middleName.isNullOrEmpty()) {
                etMiddleName.isEnabled = false
                scNoMiddleName.isChecked = true
            } else {
                etMiddleName.setText(middleName)
                scNoMiddleName.isChecked = false
            }
        }
    }

    override fun setTimeLeft(seconds: Int) {
        val quantity = Utils.timerFormatter(seconds, requireContext())
        mBinding.tvTimer.text = String.format(timerMessage, quantity)
    }

    override fun setCanResend(canResend: Boolean) {
        mBinding.btnResend.isEnabled = canResend
        mBinding.tvTimer.isVisible = !canResend
    }

    override fun enableRegisterBtn(isEnable: Boolean) {
        mBinding.ibRegister.apply { isEnabled = isEnable }
    }

    override fun enableMiddleNameInput(enable: Boolean) {
        mBinding.etMiddleName.isEnabled = enable
        if (!enable) {
            mBinding.etMiddleName.setText("")
            presenter.onChangeMiddleNameText("")
        }
    }

    override fun codeError() {
        mBinding.tilCode.error = getString(R.string.auth_error_code)
    }

    override fun setDescriptionText(canShow: Boolean) {
        mBinding.lnText.isVisible = canShow
        if (canShow){
            mBinding.apply {
                when (presenter.loginType) {
                    "phone" -> {
                        tvDescription.isVisible = false
                        tvText.text = getString(R.string.code_phone_dialog_text, presenter.phone)
                    }
                    "email" -> {
                        tvText.text = getString(R.string.code_email_dialog_text, presenter.email)
                        tvDescription.apply {
                            isVisible = true
                            val supportEmail = getString(R.string.support_email)
                            val message =
                                getString(R.string.code_dialog_text_information).format(supportEmail)
                                    .toSpannable()
                            Linkify.addLinks(message, Linkify.EMAIL_ADDRESSES)
                            text = message
                            movementMethod = BetterLinkMovementMethod.getInstance()
                        }
                    }
                }
            }
        }
    }

    override fun showWrongEmailError(canShow: Boolean) {
        mBinding.tilEmail.error = if (canShow) getString(R.string.auth_error_wrong_email) else null
    }
    override fun showWrongPhoneError(canShow: Boolean) {
        mBinding.tilEmail.error =
            if (canShow) getString(R.string.invalid_phone_number_second_error) else null
    }

    override fun openHome() {
        (requireActivity() as MainActivity).setIgnoreTokenListener(false)
        findNavController().navigate(FinishRegisterNewFragmentDirections.registerToMail(true))
    }

    override fun logout() {
        (requireActivity() as MainActivity).setIgnoreTokenListener(false)
        findNavController().navigate(
            R.id.register_email_new_fragment, null, NavOptions.Builder()
                .setPopUpTo(R.id.main_navigation, true)
                .build()
        )
    }


    override fun layout() = R.layout.fragment_finish_register_new

}

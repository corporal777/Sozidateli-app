package com.example.ui.auth.register.email.finishregister.newbuild

import android.content.res.ColorStateList
import android.os.Bundle
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
import com.example.ui.views.ConfirmPhoneDialog
import com.example.util.*
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
        if (args.email.isNullOrEmpty()){
            login = args.phone ?: ""
            loginType = "phone"
        } else {
            login = args.email
            loginType = "email"
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            ivClose.setOnClickListener { findNavController().navigateUp() }
            scNoMiddleName.initSwitch(presenter.noMiddleNameChecked) {
                presenter.onNoMiddleNameChecked(it)
            }
            etFirstName.apply {
                filters = getNameFilter()
                onTextChanged { it?.toString()?.let { text -> presenter.onChangeNameText(text) } }
                onFocusChanged { hasFocus ->
                    if (!hasFocus) {
                        setText(removeFirstAndLastSpaces(text.toString()))
                    }
                }
            }
            etLastName.apply {
                filters = getNameFilter()
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
                filters = getNameFilter()
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
                filters = getEmailFilter()
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
                setOnClickListener { presenter.checkEmailPhoneUnique() }
            }
            ibCancel.setOnClickListener {
                hideKeyboard()
                ignoreTokenListener(true)
                presenter.logout()
            }
            ibRegister.setOnClickListener {
                hideKeyboard()
                if (etCode.text?.length != AddPhoneEmailDialog.CODE_SIZE) {
                    tilCode.error = resources.getString(R.string.auth_error_no_code)
                } else {
                    ignoreTokenListener(true)
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
        if (canShow) {
            mBinding.apply {
                when (presenter.loginType) {
                    "phone" -> {
                        tvDescription.isVisible = false
                        tvText.text = getString(R.string.code_phone_dialog_text, presenter.login)
                    }
                    "email" -> {
                        tvText.text = getString(R.string.code_email_dialog_text, presenter.login)
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

    override fun showEmailPhoneNotUnique(email: String, loginType: String) {
        val message = if (loginType == "email") getString(R.string.confirm_email_text, email)
        else getString(R.string.confirm_phone_text, email)
        ConfirmPhoneDialog(
            requireContext(),
            message,
            getString(R.string.event_register_no_form_negative),
            getString(R.string.confirm_phone_positive)

        ).setSelectCallback {
            if (it) {
                presenter.sendCodeAgain()
            }
        }

    }

    override fun openHome() {
        ignoreTokenListener(false)
        findNavController().navigate(FinishRegisterNewFragmentDirections.registerToMail(true))
    }

    override fun logout() {
        ignoreTokenListener(false)
        findNavController().navigate(
            R.id.register_email_new_fragment, null, NavOptions.Builder()
                .setPopUpTo(R.id.main_navigation, true)
                .build()
        )
    }

    override fun ignoreTokenListener(ignore: Boolean) {
        (requireActivity() as MainActivity).setIgnoreTokenListener(ignore)
    }


    override fun layout() = R.layout.fragment_finish_register_new

}

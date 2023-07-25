package com.example.ui.auth.register.email.finish

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.util.Linkify
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.databinding.FragmentFinishRegisterBinding
import com.example.ui.base.BaseFragment
import com.example.ui.main.MainActivity
import com.example.ui.views.ConfirmPhoneDialog
import com.example.util.*
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import onBackPressedCallback
import onFocusChanged
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class FinishRegisterFragment : BaseFragment<FragmentFinishRegisterBinding>(),
    FinishRegisterContract.View {

    private val timerEmailMessage by lazy { getString(R.string.auth_register_confirm_email_timer_two) }
    private val timerPhoneMessage by lazy { getString(R.string.auth_register_confirm_phone_timer) }

    @InjectPresenter
    lateinit var presenter: FinishRegisterPresenter

    @Inject
    lateinit var presenterProviderFinish: Provider<FinishRegisterPresenter>

    @ProvidePresenter
    fun providePresenter(): FinishRegisterPresenter = presenterProviderFinish.get().apply {
        val args = requireArguments().let { FinishRegisterFragmentArgs.fromBundle(it) }
        firstName = args.name
        lastName = args.lastName
        middleName = args.middleName ?: ""
        noMiddleNameChecked = args.isNoMiddleName
        login = args.email

        if (Utils.isPhone(login) && !Utils.isContainLetters(login))
            loginType = "phone"
        else loginType = "email"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressedCallback(true){
            presenter.onCloseClick()
        }

        mBinding.apply {
            ivClose.setOnClickListener { presenter.onCloseClick() }
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
                presenter.onCloseClick()
            }
            ibRegister.setOnClickListener {
                hideKeyboard()
                if (presenter.isConfirmCodeValid(etCode.text?.length ?: 0)) {
                    presenter.onHandleAuthLink()
                } else tilCode.error = resources.getString(R.string.auth_error_no_code)
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
        if (presenter.loginType == "email") {
            mBinding.tvTimer.text = String.format(timerEmailMessage, quantity)
        } else {
            mBinding.tvTimer.text = String.format(timerPhoneMessage, quantity)
        }
    }

    override fun setCanResend(canResend: Boolean) {
        mBinding.btnResend.apply {
            isEnabled = canResend
            text = if (presenter.loginType == "email") getString(R.string.send_code_again)
            else getString(R.string.send_call_again)
        }
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

    override fun setDescriptionText(loginType: String, login: String) {
        when (loginType) {
            "phone" -> {
                mBinding.etCode.hint = getString(R.string.auth_error_no_call)
                mBinding.tvText.text = getString(R.string.call_code_phone_dialog_text)
            }
            "email" -> {
                mBinding.etCode.hint = getString(R.string.auth_error_no_code)

                val supportEmail = getString(R.string.support_email)
                val message = getString(R.string.code_dialog_text_information).format(supportEmail).toSpannable()
                Linkify.addLinks(message, Linkify.EMAIL_ADDRESSES)

                val descriptionText = SpannableStringBuilder(
                    getString(R.string.code_email_dialog_text, login)
                )
                    .append("\n")
                    .append(message)

                mBinding.tvText.apply {
                    text = descriptionText
                    movementMethod = BetterLinkMovementMethod.getInstance()
                }
            }
        }

    }
    override fun showHideDescriptionText(canShow: Boolean) {
        mBinding.tvText.isVisible = canShow
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
        findNavController().navigate(FinishRegisterFragmentDirections.registerToMail(true))
    }

    override fun logout() {
        if (!findNavController().popBackStack(R.id.authorization_fragment, false)) {
            findNavController().navigate(R.id.authorization_fragment, null, navOptions {
                popUpTo(R.id.main_navigation) { inclusive = true }
            })
        }
    }

    override fun connectToSocket() {
        (requireActivity() as MainActivity).connectToSocket()
    }

    override fun layout() = R.layout.fragment_finish_register

}

package com.example.ui.auth.register.email.newbuild

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.SnUser
import com.example.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_register_email_new.*
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class RegisterEmailNewFragment : BaseFragment(), RegisterEmailNewContract.View {

    override fun layout() = R.layout.fragment_register_email_new

    @InjectPresenter
    lateinit var presenter: RegisterEmailNewPresenter

    @Inject
    lateinit var presenterProvider: Provider<RegisterEmailNewPresenter>

    @ProvidePresenter
    fun providePresenter(): RegisterEmailNewPresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivClose.setOnClickListener { presenter.onClickClose() }

        etFirstName.onTextChanged { it?.toString()?.let { text -> presenter.onChangeFirstNameText(text) } }
        etLastName.onTextChanged { it?.toString()?.let { text -> presenter.onChangeLastNameText(text) } }
        etMiddleName.onTextChanged { it?.toString()?.let { text -> presenter.onChangeMiddleNameText(text) } }
        etEmail.onTextChanged { it?.toString()?.let { text -> presenter.onChangeEmailText(text) } }
        etPassword.onTextChanged { it?.toString()?.let { text -> presenter.onChangePasswordText(text) } }
        etPasswordConfirm.onTextChanged { it?.toString()?.let { text -> presenter.onChangePasswordConfirmText(text) } }
        scNoMiddleName.setOnCheckedChangeListener { _, checked -> presenter.onNoMiddleNameChecked(checked) }

        ibRegister.setOnClickListener {
            presenter.onClickRegister(
                    etEmail.text?.toString(),
                    etFirstName.text?.toString(),
                    etLastName.text?.toString(),
                    etPassword.text?.toString(),
                    etPasswordConfirm.text?.toString(),
            )
        }

        etMobilePhone.apply {
            onTextChanged {
                val phone = "+7" + it.toString()
                presenter.onChangePhoneText(/*it?.toString() ?: ""*/phone)
            }
            addTextChangedListener(PhoneNumberFormattingTextWatcher())
        }

        btnPhoneConfirm.setOnClickListener { presenter.onPhoneConfirmClick() }
    }

    override fun setData(
            email: String?,
            firstName: String?,
            lastName: String?,
            middleName: String?,
            noMiddleNameChecked: Boolean,
            password: String?,
            passwordConfirm: String?,
            phone: String?,
            phoneVerified: Boolean
    ) {
        etEmail.setText(email)
        etFirstName.setText(firstName)
        etLastName.setText(lastName)
        etPassword.setText(password)
        etPasswordConfirm.setText(passwordConfirm)

        updatePhoneConfirmationStatus(phoneVerified)
    }

    override fun updatePhoneConfirmationStatus(confirmed: Boolean) {
        btnPhoneConfirm.isVisible = !confirmed
        tvPhoneConfirmed.isVisible = confirmed
    }

    override fun showPhoneConfirm(phone: String) {
        findNavController().navigate(RegisterEmailNewFragmentDirections.emailRegisterToPhoneConfirmFragment(phone, ""))
    }

    override fun phoneConfirmEnabled(enabled: Boolean) {
        btnPhoneConfirm.isEnabled = enabled
        btnPhoneConfirm.isVisible = enabled
    }

    override fun enableMiddleNameInput(enable: Boolean) {
        etMiddleName.isEnabled = enable
        if (!enable) {
            etMiddleName.setText("")
            presenter.onChangeMiddleNameText("")
        }
    }

    override fun showFirstNameError(show: Boolean) {
        tilFirstName.error = if (show) getString(R.string.auth_error_no_first_name) else null
    }

    override fun showLastNameError(show: Boolean) {
        tilLastName.error = if (show) getString(R.string.auth_error_no_last_name) else null
    }

    override fun showEmailError(show: Boolean) {
        tilEmail.error = if (show) getString(R.string.auth_error_wrong_email) else null
    }

    override fun showPasswordError(show: Boolean) {
        tilPassword.error = if (show) getString(
                if (etPassword.text.isNullOrEmpty()) R.string.auth_error_no_password
                else R.string.auth_error_short_password
        ) else null
    }

    override fun showPasswordConfirmError(show: Boolean) {
        tilPasswordConfirm.error = if (show) getString(R.string.auth_error_password_do_not_match) else null
    }

    override fun showAgreementError(show: Boolean) {
        tvAgreeError.isInvisible = !show
    }

    override fun showWrongPhoneError(show: Boolean) {
        tilMobilePhone.apply {
            error = if (show) getString(R.string.invalid_phone_number_error) else null
        }
    }

    override fun enableRegisterBtn(isEnable: Boolean) {
        ibRegister.apply { isEnabled = isEnable }
    }

    override fun showEmailConfirmation(email: String, password: String) {
        findNavController().navigate(RegisterEmailNewFragmentDirections.emailRegisterToEmailConfirm(email, password, null))
    }

    override fun showSnRegistration(snUser: SnUser) {
        findNavController().navigate(RegisterEmailNewFragmentDirections.emailRegisterToSnRegister(snUser))
    }
}

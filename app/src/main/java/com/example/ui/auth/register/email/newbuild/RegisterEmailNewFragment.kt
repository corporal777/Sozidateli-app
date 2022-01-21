package com.example.ui.auth.register.email.newbuild

import android.R.attr
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.SnUser
import com.example.ui.base.BaseFragment
import com.example.ui.views.ConfirmPhoneDialog
import kotlinx.android.synthetic.main.fragment_register_email_new.*
import onTextChanged
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Provider


class RegisterEmailNewFragment : BaseFragment(), RegisterEmailNewContract.View {

    override fun layout() = R.layout.fragment_register_email_new

    private val filter = arrayOf(InputFilter { source, _, _, _, _, _ ->
        source.toString().filter {
            it.isLetter() || it == '-'
        }
    })

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
        etFirstName.filters = filter
        etLastName.onTextChanged { it?.toString()?.let { text -> presenter.onChangeLastNameText(text) } }
        etLastName.filters = filter
        etMiddleName.onTextChanged { it?.toString()?.let { text -> presenter.onChangeMiddleNameText(text) } }
        etMiddleName.filters = filter
        etEmail.onTextChanged { it?.toString()?.let { text -> presenter.onChangeEmailText(text, requireContext()) } }
        etEmailAgain.onTextChanged { it?.toString()?.let { text -> presenter.onChangeEmailAgainText(text) } }
        /*etPassword.onTextChanged { it?.toString()?.let { text -> presenter.onChangePasswordText(text) } }
        etPasswordConfirm.onTextChanged { it?.toString()?.let { text -> presenter.onChangePasswordConfirmText(text) } }*/
        scNoMiddleName.setOnCheckedChangeListener { _, checked -> presenter.onNoMiddleNameChecked(checked) }
        password.setShowAgree(true)

        password.setHyperlinkClickCallback {
            showUserAgreement()
        }
        password.setPasswordValidCallback {
            presenter.onChangeNewPasswordText(it.password ?: "", it.isValid)
        }
        password.setChangedSelectionCallback {
            presenter.onClickAgree(it)
        }
        ibRegister.setOnClickListener {
            /*if (etMobilePhone.getNumberWithoutCode() == "" || etMobilePhone.getIsValid()etMobilePhone.text.toString() == "" || etMobilePhone.text.toString().isValidPhoneNumber(requireContext())) {*/
                presenter.onClickRegister(
                        etEmail.text?.toString(),
                        etFirstName.text?.toString(),
                        etLastName.text?.toString(),
                        password.etPassword.text?.toString(),//etPassword.text?.toString(),
                        //etPasswordConfirm.text?.toString(),
                        password.cbAgree.isChecked
                )
            /*} else {
                showWrongPhoneError(true)
            }*/
        }
        /*etMobilePhone.apply {
            getPhoneCallback {
                presenter.onChangePhoneText(it)
            }
        }
        etMobilePhone.apply {
            onTextChanged {
                presenter.onChangePhoneText(it?.toString() ?: "")
            }
            addTextChangedListener(PhoneNumberFormattingTextWatcher())
        }*/
    }

    override fun showEmailNotUnique(email: String, firstName: String, lastName: String, password: String, middleName: String?, phone: String?) {
        ConfirmPhoneDialog(requireContext(), getString(R.string.confirm_email_text, email), getString(R.string.confirm_phone_positive),
                getString(R.string.event_register_no_form_negative))
                .setSelectCallback {
                    if (!it) {
                        presenter.register(email, firstName, lastName, password, middleName, phone)
                    } else {
                        //findNavController().navigate(RegisterEmailNewFragmentDirections.actionRegisterEmailNewFragmentToRecoveryPasswordFragment(email))
                    }
                }
    }

    override fun showPhoneNotUnique(email: String, firstName: String, lastName: String, password: String, middleName: String?, phone: String?) {
        ConfirmPhoneDialog(requireContext(), getString(R.string.confirm_phone_text, phone), getString(R.string.confirm_phone_positive),
                getString(R.string.event_register_no_form_negative))
                .setSelectCallback {
                    if (!it) {
                        presenter.register(email, firstName, lastName, password, middleName, phone)
                    } else {
                        //findNavController().navigate(RegisterEmailNewFragmentDirections.actionRegisterEmailNewFragmentToRecoveryPasswordFragment(email))
                    }
                }
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
            phoneVerified: Boolean,
            isAgree: Boolean
    ) {
        etEmail.setText(email)
        etFirstName.setText(firstName)
        etLastName.setText(lastName)
        /*etPassword.setText(password)
        etPasswordConfirm.setText(passwordConfirm)*/
        this.password.etPassword.setText(password)
        this.password.setAgreeSelection(isAgree)
        updatePhoneConfirmationStatus(phoneVerified)
    }

    override fun updatePhoneConfirmationStatus(confirmed: Boolean) {
        //btnPhoneConfirm.isVisible = !confirmed
        //tvPhoneConfirmed.isVisible = confirmed
    }

    override fun changeFieldType(type: String, isValid: Boolean) {
        tilEmailAgain.isVisible = type == "email" && isValid
    }

    override fun showPhoneConfirm(phone: String) {
        findNavController().navigate(RegisterEmailNewFragmentDirections.emailRegisterToPhoneConfirmFragment(phone, "", null))
    }

    override fun phoneConfirmEnabled(enabled: Boolean) {
        //btnPhoneConfirm.isEnabled = enabled
        //btnPhoneConfirm.isVisible = enabled
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
        /*tilPassword.error = if (show) getString(
                if (etPassword.text.isNullOrEmpty()) R.string.auth_error_no_password
                else R.string.auth_error_short_password_length
        ) else null*/
    }

    override fun showPasswordConfirmError(show: Boolean) {
        //tilPasswordConfirm.error = if (show) getString(R.string.auth_error_password_do_not_match) else null
    }

    override fun showEmailAgainError(show: Boolean) {
        tilEmailAgain.error = if (show) getString(R.string.auth_error_email_do_not_match) else null
    }

    override fun showAgreementError(show: Boolean) {
        tvAgreeError.isInvisible = !show
    }

    override fun showWrongPhoneError(show: Boolean) {
        /*etMobilePhone.showError(show)
        tilMobilePhone.apply {
            error = if (show) getString(R.string.invalid_phone_number_second_error) else null
        }*/
    }

    override fun enableRegisterBtn(isEnable: Boolean) {
        //btnPhoneConfirm.apply { isEnabled = isEnable }
        ibRegister.apply { isEnabled = isEnable }
    }

    override fun showFinishRegister(name: String, lastName: String, middleName: String?, phone: String?, email: String, code: String, userPhoneConfirmed: Boolean, isNoMiddleName: Boolean, nameEditable: Boolean) {
        findNavController().navigate(R.id.register_email_finish_fragment, bundleOf("code" to code,
                "name" to name, "lastName" to lastName, "email" to email, "phone" to phone, "middleName" to middleName,
                "isConfirmed" to userPhoneConfirmed, "isNoMiddleName" to isNoMiddleName, "nameEditable" to nameEditable), NavOptions.Builder()
                .setPopUpTo(R.id.main_navigation, true)
                .build())
        //findNavController().navigate(RegisterEmailNewFragmentDirections.registerToFinishRegister(code, name, lastName, email, phone, middleName))
    }

    override fun showEmailConfirmation(email: String, password: String) {
        findNavController().navigate(RegisterEmailNewFragmentDirections.actionRegisterEmailNewFragmentToAuthorizationFragment().setShowFinishRegister(true))
        //findNavController().navigate(RegisterEmailNewFragmentDirections.emailRegisterToEmailConfirm(email, password, null))
    }

    override fun showSnRegistration(snUser: SnUser) {
        findNavController().navigate(RegisterEmailNewFragmentDirections.emailRegisterToSnRegister(snUser))
    }

    private fun showUserAgreement() {
        try {
            val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.auth_agree_address)))
            startActivity(viewIntent)
        } catch (e: Throwable) {
            Toast.makeText(requireContext(), R.string.error_title, Toast.LENGTH_LONG).show()
        }
    }
}

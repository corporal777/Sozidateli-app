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
import com.example.databinding.FragmentRegisterEmailNewBinding
import com.example.ui.base.BaseFragment
import com.example.ui.base.BaseFragmentNew
import com.example.ui.views.ConfirmPhoneDialog
import com.example.ui.views.dialogs_new.CustomProgressDialog
import com.example.util.*
import kotlinx.android.synthetic.main.fragment_register_email_new.*
import onFocusChanged
import onTextChanged
import java.lang.StringBuilder
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Provider


class RegisterEmailNewFragment : BaseFragmentNew<FragmentRegisterEmailNewBinding>(),
    RegisterEmailNewContract.View {

    override fun layout() = R.layout.fragment_register_email_new
    private lateinit var mProgressDialog: CustomProgressDialog

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
    lateinit var presenter: RegisterEmailNewPresenter

    @Inject
    lateinit var presenterProvider: Provider<RegisterEmailNewPresenter>

    @ProvidePresenter
    fun providePresenter(): RegisterEmailNewPresenter = presenterProvider.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.deviceModel = getDeviceName()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mProgressDialog = CustomProgressDialog(requireContext())
        mBinding.apply {
            ivClose.setOnClickListener { presenter.onClickClose() }

            etFirstName.apply {
                filters = filter
                onTextChanged {
                    it?.toString()?.let { text -> presenter.onChangeFirstNameText(text) }
                }
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
                    it?.toString()
                        ?.let { text -> presenter.onChangeEmailText(text, requireContext()) }
                }
                onFocusChanged { hasFocus ->
                    if (!hasFocus) {
                        if (AuthValidateUtil.isDigits(text.toString())){
                            if (!AuthValidateUtil.isValidPhone(text.toString())){
                                showWrongPhoneError(true)
                            }
                        }else {
                            if (!AuthValidateUtil.isValidEmail(text.toString())){
                                showEmailError(true)
                            }
                        }
                    }
                }
            }
            etEmailAgain.apply {
                filters = emailFilter
                onTextChanged {
                    it?.toString()?.let { text -> presenter.onChangeEmailAgainText(text) }
                }
                onFocusChanged { hasFocus ->
                    if (!hasFocus) {
                        if (!text.isNullOrEmpty()) {
                            if (text.toString() != mBinding.etEmail.text.toString()) {
                                showEmailAgainError(true)
                            }
                        }
                    }
                }
            }


            scNoMiddleName.setOnCheckedChangeListener { _, checked ->
                presenter.onNoMiddleNameChecked(
                    checked
                )
            }
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
                val name = removeAllDoubleSpaces(etFirstName.text.toString())
                val lastName = removeAllDoubleSpaces(etLastName.text.toString())

                /*if (etMobilePhone.getNumberWithoutCode() == "" || etMobilePhone.getIsValid()etMobilePhone.text.toString() == "" || etMobilePhone.text.toString().isValidPhoneNumber(requireContext())) {*/
                presenter.onClickRegister(
                    etEmail.text?.toString(),
                    name,
                    lastName,
                    password.etPassword.text?.toString(),//etPassword.text?.toString(),
                    //etPasswordConfirm.text?.toString(),
                    password.cbAgree.isChecked
                )
            }
        }
    }

    override fun showEmailNotUnique(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
        middleName: String?,
        phone: String?
    ) {
        ConfirmPhoneDialog(
            requireContext(),
            getString(R.string.confirm_email_text, email),
            getString(R.string.confirm_phone_positive),
            getString(R.string.event_register_no_form_negative)
        )
            .setSelectCallback {
                if (!it) {
                    showAlertLoadingDialog()
                    presenter.register(email, firstName, lastName, password, middleName, phone)
                } else {
                    //findNavController().navigate(RegisterEmailNewFragmentDirections.actionRegisterEmailNewFragmentToRecoveryPasswordFragment(email))
                }
            }
    }

    override fun showPhoneNotUnique(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
        middleName: String?,
        phone: String?
    ) {
        ConfirmPhoneDialog(
            requireContext(),
            getString(R.string.confirm_phone_text, phone),
            getString(R.string.confirm_phone_positive),
            getString(R.string.event_register_no_form_negative)
        )
            .setSelectCallback {
                if (!it) {
                    showAlertLoadingDialog()
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
        mBinding.etEmail.setText(email)
        mBinding.etFirstName.setText(firstName)
        mBinding.etLastName.setText(lastName)
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
        mBinding.tilEmailAgain.isVisible = type == "email" && isValid
    }

    override fun showPhoneConfirm(phone: String) {
        findNavController().navigate(
            RegisterEmailNewFragmentDirections.emailRegisterToPhoneConfirmFragment(
                phone,
                "",
                null
            )
        )
    }

    override fun phoneConfirmEnabled(enabled: Boolean) {
        //btnPhoneConfirm.isEnabled = enabled
        //btnPhoneConfirm.isVisible = enabled
    }

    override fun enableMiddleNameInput(enable: Boolean) {
        mBinding.etMiddleName.isEnabled = enable
        if (!enable) {
            mBinding.etMiddleName.setText("")
            presenter.onChangeMiddleNameText("")
        }
    }

    override fun showFirstNameError(show: Boolean) {
        mBinding.tilFirstName.error =
            if (show) getString(R.string.auth_error_no_first_name) else null
    }

    override fun showLastNameError(show: Boolean) {
        mBinding.tilLastName.error = if (show) getString(R.string.auth_error_no_last_name) else null
    }

    override fun showEmailError(show: Boolean) {
        mBinding.tilEmail.error = if (show) getString(R.string.auth_error_wrong_email) else null
    }

    override fun showWrongPhoneError(show: Boolean) {
        mBinding.tilEmail.error = if (show) getString(R.string.invalid_phone_number_second_error) else null
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
        mBinding.tilEmailAgain.error =
            if (show) getString(R.string.auth_error_email_do_not_match) else null
    }

    override fun showAgreementError(show: Boolean) {
        mBinding.tvAgreeError.isInvisible = !show
    }



    override fun enableRegisterBtn(isEnable: Boolean) {
        //btnPhoneConfirm.apply { isEnabled = isEnable }
        mBinding.ibRegister.apply { isEnabled = isEnable }
    }

    override fun showFinishRegister(
        name: String,
        lastName: String,
        middleName: String?,
        phone: String?,
        email: String,
        code: String,
        userPhoneConfirmed: Boolean,
        isNoMiddleName: Boolean,
        nameEditable: Boolean
    ) {
        findNavController().navigate(
            R.id.register_email_finish_fragment, bundleOf(
                "code" to code,
                "name" to name,
                "lastName" to lastName,
                "email" to email,
                "phone" to phone,
                "middleName" to middleName,
                "isConfirmed" to userPhoneConfirmed,
                "isNoMiddleName" to isNoMiddleName,
                "nameEditable" to nameEditable
            ), NavOptions.Builder()
                .setPopUpTo(R.id.main_navigation, true)
                .build()
        )
        //findNavController().navigate(RegisterEmailNewFragmentDirections.registerToFinishRegister(code, name, lastName, email, phone, middleName))
    }

    override fun showEmailConfirmation(email: String, password: String) {
        findNavController().navigate(
            RegisterEmailNewFragmentDirections.actionRegisterEmailNewFragmentToAuthorizationFragment()
                .setShowFinishRegister(true)
        )
    }

    override fun showSnRegistration(snUser: SnUser) {
        findNavController().navigate(
            RegisterEmailNewFragmentDirections.emailRegisterToSnRegister(
                snUser
            )
        )
    }

    private fun showUserAgreement() {
        try {
            val viewIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.auth_agree_address)))
            startActivity(viewIntent)
        } catch (e: Throwable) {
            Toast.makeText(requireContext(), R.string.error_title, Toast.LENGTH_LONG).show()
        }
    }


    override fun showAlertLoadingDialog() {
        mProgressDialog.showDialog()
    }

    override fun hideAlertLoadingDialog() {
        mProgressDialog.hideDialog()
    }
}

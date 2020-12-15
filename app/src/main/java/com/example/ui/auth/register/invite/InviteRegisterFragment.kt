package com.example.ui.auth.register.invite

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.extensions.showChangeEmailCompleteDialog
import com.example.ui.base.BaseFragment
import com.example.util.ClickableSpan
import kotlinx.android.synthetic.main.fragment_invite_register.*
import kotlinx.android.synthetic.main.fragment_invite_register.btnPhoneConfirm
import kotlinx.android.synthetic.main.fragment_invite_register.etEmail
import kotlinx.android.synthetic.main.fragment_invite_register.etFirstName
import kotlinx.android.synthetic.main.fragment_invite_register.etLastName
import kotlinx.android.synthetic.main.fragment_invite_register.etMiddleName
import kotlinx.android.synthetic.main.fragment_invite_register.etMobilePhone
import kotlinx.android.synthetic.main.fragment_invite_register.etPassword
import kotlinx.android.synthetic.main.fragment_invite_register.etPasswordConfirm
import kotlinx.android.synthetic.main.fragment_invite_register.ibRegister
import kotlinx.android.synthetic.main.fragment_invite_register.ivClose
import kotlinx.android.synthetic.main.fragment_invite_register.scNoMiddleName
import kotlinx.android.synthetic.main.fragment_invite_register.tilEmail
import kotlinx.android.synthetic.main.fragment_invite_register.tilFirstName
import kotlinx.android.synthetic.main.fragment_invite_register.tilLastName
import kotlinx.android.synthetic.main.fragment_invite_register.tilMobilePhone
import kotlinx.android.synthetic.main.fragment_invite_register.tilPassword
import kotlinx.android.synthetic.main.fragment_invite_register.tilPasswordConfirm
import kotlinx.android.synthetic.main.fragment_invite_register.tvPhoneConfirmed
import kotlinx.android.synthetic.main.fragment_register_email_new.*
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class InviteRegisterFragment : BaseFragment(), InviteRegisterContract.View {

    @InjectPresenter
    lateinit var presenter: InviteRegisterPresenter

    @Inject
    lateinit var presenterProvider: Provider<InviteRegisterPresenter>

    @ProvidePresenter
    fun providePresenter(): InviteRegisterPresenter = presenterProvider.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            presenter.onSaveEmailText(InviteRegisterFragmentArgs.fromBundle(it).email)
            presenter.onSaveCode(InviteRegisterFragmentArgs.fromBundle(it).code)
        }
    }

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
        cbAgree.setOnCheckedChangeListener { _, checked -> presenter.onAgreeChecked(checked) }

        val agreementText = SpannableString(getString(R.string.auth_agree_user_agreement)).apply {
            val linkStart = 11
            val linkEnd = length
            setSpan(ClickableSpan(drawUnderline = false) {
                presenter.onClickUserAgreement()
            }, linkStart, linkEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        }
        tvAgree.apply {
            text = agreementText
            movementMethod = LinkMovementMethod.getInstance()
        }

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
                presenter.onChangePhoneText(it.toString())
            }
            addTextChangedListener(PhoneNumberFormattingTextWatcher())
        }
        etMobilePhone.setText("+7")
        btnPhoneConfirm.setOnClickListener { presenter.onPhoneConfirmClick() }
    }

    override fun enableMiddleNameInput(enable: Boolean) {
        etMiddleName.isEnabled = enable
        if (!enable) {
            etMiddleName.setText("")
            presenter.onChangeMiddleNameText("")
        }
    }

    override fun updateFieldsInUI(firstName: String, lastName: String, middleName: String, email: String) {
        etFirstName.setText(firstName)
        etLastName.setText(lastName)
        etMiddleName.setText(middleName)
        etEmail.setText(email)
    }

    override fun showWrongPhoneError(show: Boolean) {
        tilMobilePhone.apply {
            error = if (show) getString(R.string.invalid_phone_number_error) else null
        }
    }

    override fun showPhoneConfirm(phone: String) {
        findNavController().navigate(InviteRegisterFragmentDirections.emailRegisterToPhoneConfirmFragment(phone, "", null))
    }

    override fun phoneConfirmEnabled(enabled: Boolean) {
        btnPhoneConfirm.isEnabled = enabled
        btnPhoneConfirm.isVisible = enabled
    }

    override fun updatePhoneConfirmationStatus(confirmed: Boolean) {
        btnPhoneConfirm.isVisible = !confirmed
        tvPhoneConfirmed.isVisible = confirmed
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun enableRegisterBtn(isEnable: Boolean) {
        ibRegister.apply { isEnabled = isEnable }
    }

    override fun showPasswordConfirmError(show: Boolean) {
        tilPasswordConfirm.error = if (show) getString(R.string.auth_error_password_do_not_match) else null
    }

    override fun showPasswordError(show: Boolean) {
        tilPassword.error = if (show) getString(
                if (etPassword.text.isNullOrEmpty()) R.string.auth_error_no_password
                else R.string.auth_error_short_password
        ) else null
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

    override fun showUserAgreement() {
        try {
            val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.auth_agree_address)))
            startActivity(viewIntent)
        } catch (e: Throwable) {
            Toast.makeText(requireContext(), R.string.error_title, Toast.LENGTH_LONG).show()
        }
    }

    override fun showEmailDialog(email: String) {
        showChangeEmailCompleteDialog(email)
    }

    override fun layout(): Int = R.layout.fragment_invite_register
}
package com.example.ui.auth.register.invite

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.extensions.showChangeEmailCompleteDialog
import com.example.ui.auth.register.email.finishregister.FinishRegisterFragmentDirections
import com.example.ui.base.BaseFragment
import com.example.ui.main.MainActivity
import kotlinx.android.synthetic.main.fragment_finish_register.*
import kotlinx.android.synthetic.main.fragment_finish_register.ibCancel
import kotlinx.android.synthetic.main.fragment_invite_register.*
import kotlinx.android.synthetic.main.fragment_invite_register.etEmail
import kotlinx.android.synthetic.main.fragment_invite_register.etFirstName
import kotlinx.android.synthetic.main.fragment_invite_register.etLastName
import kotlinx.android.synthetic.main.fragment_invite_register.etMiddleName
import kotlinx.android.synthetic.main.fragment_invite_register.ibRegister
import kotlinx.android.synthetic.main.fragment_invite_register.ivClose
import kotlinx.android.synthetic.main.fragment_invite_register.scNoMiddleName
import kotlinx.android.synthetic.main.fragment_invite_register.tilEmail
import kotlinx.android.synthetic.main.fragment_invite_register.tilFirstName
import kotlinx.android.synthetic.main.fragment_invite_register.tilLastName
import kotlinx.android.synthetic.main.fragment_register_email_new.*
import kotlinx.android.synthetic.main.fragment_register_email_new.password
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class InviteRegisterFragment : BaseFragment(), InviteRegisterContract.View {

    private val filter = arrayOf(InputFilter { source, _, _, _, _, _ ->
        source.toString().filter {
            it.isLetter() || it == '-'
        }
    })

    @InjectPresenter
    lateinit var presenter: InviteRegisterPresenter

    @Inject
    lateinit var presenterProvider: Provider<InviteRegisterPresenter>

    @ProvidePresenter
    fun providePresenter(): InviteRegisterPresenter = presenterProvider.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            presenter.onSaveEmailText(InviteRegisterFragmentArgs.fromBundle(it).email,
                    InviteRegisterFragmentArgs.fromBundle(it).name,
                    InviteRegisterFragmentArgs.fromBundle(it).lastName,
                    InviteRegisterFragmentArgs.fromBundle(it).middleName,
                    InviteRegisterFragmentArgs.fromBundle(it).invite)
            presenter.onSaveCode(InviteRegisterFragmentArgs.fromBundle(it).code)
            (requireActivity() as MainActivity).setIgnoreTokenListener(true)
            presenter.getData()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivClose.setOnClickListener { presenter.onClickClose() }

        etFirstName.onTextChanged { it?.toString()?.let { text -> presenter.onChangeFirstNameText(text) } }
        etFirstName.filters = filter
        etLastName.onTextChanged { it?.toString()?.let { text -> presenter.onChangeLastNameText(text) } }
        etLastName.filters = filter
        etMiddleName.onTextChanged { it?.toString()?.let { text -> presenter.onChangeMiddleNameText(text) } }
        etMiddleName.filters = filter
        //etEmail.onTextChanged { it?.toString()?.let { text -> presenter.onChangeEmailText(text) } }
        //etPassword.onTextChanged { it?.toString()?.let { text -> presenter.onChangePasswordText(text) } }
        //etPasswordConfirm.onTextChanged { it?.toString()?.let { text -> presenter.onChangePasswordConfirmText(text) } }
        scNoMiddleName.setOnCheckedChangeListener { _, checked -> presenter.onNoMiddleNameChecked(checked) }
        //cbAgree.setOnCheckedChangeListener { _, checked -> presenter.onAgreeChecked(checked) }

        password.setShowAgree(true)

        password.setHyperlinkClickCallback {
            showUserAgreement()
        }
        password.setPasswordValidCallback {
            presenter.onChangePasswordText(it.password?: "", it.isValid)
        }
        password.setChangedSelectionCallback {
            presenter.onAgreeChecked(it)
        }
        /*val agreementText = SpannableString(getString(R.string.auth_agree_user_agreement)).apply {
            val linkStart = 11
            val linkEnd = length
            setSpan(ClickableSpan(drawUnderline = false) {
                presenter.onClickUserAgreement()
            }, linkStart, linkEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        }
        tvAgree.apply {
            text = agreementText
            movementMethod = LinkMovementMethod.getInstance()
        }*/

        ibRegister.setOnClickListener {
            presenter.onClickRegister(
                    etEmail.text?.toString(),
                    etFirstName.text?.toString(),
                    etLastName.text?.toString(),
                    password.etPassword.text?.toString(),
                    password.cbAgree.isChecked
                    /*etPassword.text?.toString(),
                    etPasswordConfirm.text?.toString(),*/
            )
        }
        ibCancel.setOnClickListener {
            (requireActivity() as MainActivity).setIgnoreTokenListener(true)
            presenter.logout()
        }

        ibRegistered.setOnClickListener {
            findNavController().navigate(InviteRegisterFragmentDirections.actionToInviteRegisterToLoginFragment("").setIsRegistered(true).setInviteId(presenter.invite?: 0))
            /*findNavController().navigate(R.id.login_fragment, bundleOf("isRegistered" to true, "inviteId" to presenter.invite),
                    NavOptions.Builder()
                            .setPopUpTo(R.id.main_navigation, true)
                            .build())*/
        }

        /*etMobilePhone.apply {
            getPhoneCallback {
                presenter.onChangePhoneText(it)
            }
        }
        etMobilePhone.apply {
            onTextChanged {
                presenter.onChangePhoneText(it.toString())
            }
            addTextChangedListener(PhoneNumberFormattingTextWatcher())
        }
        etMobilePhone.setText("+7")
        btnPhoneConfirm.setOnClickListener { presenter.onPhoneConfirmClick() }*/
    }

    override fun blockTokenListener() {
        (requireActivity() as MainActivity).setIgnoreTokenListener(true)
    }

    override fun unblockTokenListener() {
        (requireActivity() as MainActivity).setIgnoreTokenListener(false)
    }

    override fun logedout() {
        (requireActivity() as MainActivity).setIgnoreTokenListener(false)
        findNavController().navigate(R.id.register_email_new_fragment, null, NavOptions.Builder()
                .setPopUpTo(R.id.main_navigation, true)
                .build())
    }

    override fun openHome() {
        (requireActivity() as MainActivity).setIgnoreTokenListener(false)
        findNavController().navigate(InviteRegisterFragmentDirections.inviteRegisterToMail(true))
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
        /*etMobilePhone.showError(show)
        tilMobilePhone.apply {
            error = if (show) getString(R.string.invalid_phone_number_error) else null
        }*/
    }

    override fun showPhoneConfirm(phone: String) {
        findNavController().navigate(InviteRegisterFragmentDirections.emailRegisterToPhoneConfirmFragment(phone, "", null))
    }

    override fun phoneConfirmEnabled(enabled: Boolean) {
        /*btnPhoneConfirm.isEnabled = enabled
        btnPhoneConfirm.isVisible = enabled*/
    }

    override fun updatePhoneConfirmationStatus(confirmed: Boolean) {
        /*btnPhoneConfirm.isVisible = !confirmed
        tvPhoneConfirmed.isVisible = confirmed*/
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun enableRegisterBtn(isEnable: Boolean) {
        ibRegister.apply { isEnabled = isEnable }
    }

    override fun showPasswordConfirmError(show: Boolean) {
        //tilPasswordConfirm.error = if (show) getString(R.string.auth_error_password_do_not_match) else null
    }

    override fun showPasswordError(show: Boolean) {
        /*tilPassword.error = if (show) getString(
                if (etPassword.text.isNullOrEmpty()) R.string.auth_error_no_password
                else R.string.auth_error_short_password
        ) else null*/
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
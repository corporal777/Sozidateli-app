package com.example.ui.auth.register.email.newbuild

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.R
import com.example.data.models.SnUser
import com.example.databinding.FragmentRegisterEmailNewBinding
import com.example.ui.base.BaseFragment
import com.example.ui.views.ConfirmPhoneDialog
import com.example.ui.views.CustomSpannableString
import com.example.util.AuthValidateUtil
import com.example.util.getNameFilter
import com.example.util.removeFirstAndLastSpaces
import com.example.util.showCustomTabsBrowser
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import onBackPressedCallback
import onFocusChanged
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider


class RegisterEmailFragment : BaseFragment<FragmentRegisterEmailNewBinding>(),
    RegisterEmailContract.View {

    override fun layout() = R.layout.fragment_register_email_new

    @InjectPresenter
    lateinit var presenter: RegisterEmailPresenter

    @Inject
    lateinit var presenterProvider: Provider<RegisterEmailPresenter>

    @ProvidePresenter
    fun providePresenter(): RegisterEmailPresenter = presenterProvider.get()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressedCallback(true) {
            presenter.onClickClose()
        }
        mBinding.apply {
            ivClose.setOnClickListener { presenter.onClickClose() }

            etFirstName.apply {
                filters = getNameFilter()
                onTextChanged {
                    it?.toString()?.let { text -> presenter.onChangeFirstNameText(text) }
                }
                onFocusChanged { hasFocus ->
                    if (!hasFocus) setText(removeFirstAndLastSpaces(text.toString()))
                }
            }
            etLastName.apply {
                filters = getNameFilter()
                onTextChanged {
                    it?.toString()?.let { text -> presenter.onChangeLastNameText(text) }
                }
                onFocusChanged { hasFocus ->
                    if (!hasFocus) setText(removeFirstAndLastSpaces(text.toString()))
                }
            }
            etMiddleName.apply {
                filters = getNameFilter()
                onTextChanged {
                    it?.toString()?.let { text -> presenter.onChangeMiddleNameText(text) }
                }
                onFocusChanged { hasFocus ->
                    if (!hasFocus) setText(removeFirstAndLastSpaces(text.toString()))
                }
            }
            etEmail.apply {
                onTextChanged {
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
                                showEmailError(true)
                            }
                        }
                    }
                }
            }

            scNoMiddleName.setOnCheckedChangeListener { _, checked ->
                presenter.onNoMiddleNameChecked(checked)
            }
            passwordView.apply {
                setPasswordValidCallback {
                    presenter.onChangePasswordText(it.password ?: "", it.isValid)
                }
            }

            tvAgree.apply {
                text = CustomSpannableString(getString(R.string.auth_agree_user_agreement)).apply {
                    val linkStart = 11
                    val linkEnd = length
                    setClickSpanWithLength(tvAgree, linkStart, linkEnd) {
                        showUserAgreement()
                    }
                }
                highlightColor = ContextCompat.getColor(context, R.color.profile_id_text)
                movementMethod = LinkMovementMethod.getInstance()
            }
            cbAgree.setOnCheckedChangeListener { _, isChecked ->
                presenter.onClickAgree(isChecked)
            }
            ibRegister.setOnClickListener {
                hideKeyboard()
                presenter.onClickRegister()
            }
        }
    }

    override fun showAgreementSelection(isValid: Boolean) {
        mBinding.llAgree.isVisible = isValid
    }

    override fun showEmailNotUnique(email: String) {
        ConfirmPhoneDialog(
            requireContext(),
            getString(R.string.confirm_email_text, email),
            getString(R.string.event_register_no_form_negative),
            getString(R.string.confirm_phone_positive)
        ).setSelectCallback { if (it) presenter.register(false) }
    }

    override fun showPhoneNotUnique(email: String) {
        ConfirmPhoneDialog(
            requireContext(),
            getString(R.string.confirm_phone_text, email),
            getString(R.string.event_register_no_form_negative),
            getString(R.string.confirm_phone_positive)

        ).setSelectCallback { if (it) presenter.register(false) }
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
        mBinding.tilEmail.error =
            if (show) getString(R.string.invalid_phone_number_second_error) else null
    }

    override fun showAgreementError(show: Boolean) {
        mBinding.tvAgreeError.isInvisible = !show
    }

    override fun enableRegisterBtn(isEnable: Boolean) {
        mBinding.ibRegister.apply { isEnabled = isEnable }
    }


    override fun showFinishRegister(
        name: String,
        lastName: String,
        middleName: String?,
        email: String,
        code: String,
        isNoMiddleName: Boolean
    ) {
        findNavController().navigate(
            R.id.fragment_finish_register, bundleOf(
                "code" to code,
                "name" to name,
                "lastName" to lastName,
                "email" to email,
                "middleName" to middleName,
                "isNoMiddleName" to isNoMiddleName,
            ), NavOptions.Builder()
                .setPopUpTo(R.id.main_navigation, true)
                .build()
        )
    }


    override fun showSnRegistration(snUser: SnUser) {}

    private fun showUserAgreement() =
        showCustomTabsBrowser(requireContext(), getString(R.string.auth_agree_address))

    override fun showCustomLoading() {
        mBinding.apply {
            ibRegister.showProgressLoading(true)
        }
    }

    override fun hideCustomLoading(){
        mBinding.apply {
            ibRegister.showProgressLoading(false)
        }
    }

}

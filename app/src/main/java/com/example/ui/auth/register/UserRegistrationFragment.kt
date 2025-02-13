package com.example.ui.auth.register

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.app.R
import com.example.app.databinding.FragmentRegistrationUserBinding
import com.example.extensions.defaultDateFormatter
import com.example.extensions.formatToDefaultServerDate
import com.example.extensions.getClickablePrivacyPolitics
import com.example.extensions.initAsDatePicker
import com.example.extensions.onFocusChanged
import com.example.extensions.onTextChanged
import com.example.ui.base.BaseToolbarFragment
import com.example.ui.views.dialogs.DefaultAlertDialog
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.DATE_FORMAT_SHORT_MONTH_FULL_YEAR
import com.example.util.DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR
import com.example.util.Utils.validatePhoneBeforeSend
import com.example.util.changeTitleTextColor
import com.example.util.getColor
import com.example.util.initInput
import com.example.util.setTint
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Provider

class UserRegistrationFragment : BaseToolbarFragment<FragmentRegistrationUserBinding>(),
    UserRegistrationContract.View {

    @InjectPresenter
    lateinit var presenter: UserRegistrationPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserRegistrationPresenter>

    @ProvidePresenter
    fun providePresenter(): UserRegistrationPresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            etLastName.apply {
                onFocusChanged { hasFocus -> if (!hasFocus) presenter.onCheckLastNameValid() }
                onTextChanged { presenter.onChangeLastNameText(it.toString()) }
            }
            etFirstName.apply {
                onFocusChanged { hasFocus -> if (!hasFocus) presenter.onCheckFirstNameValid() }
                onTextChanged { presenter.onChangeFirstNameText(it.toString()) }
            }
            etMiddleName.apply {
                onFocusChanged { hasFocus -> if (!hasFocus) presenter.onCheckMiddleNameValid() }
                onTextChanged { presenter.onChangeMiddleNameText(it.toString()) }
                scNoMiddleName.setOnCheckedChangeListener { _, isChecked ->
                    presenter.onMiddleNameIsAbsent(isChecked)
                }
            }
            etPhone.onInputTextChanged {
                presenter.onChangeLoginText(it.toString())
            }
            etBirthday.apply {
                val maxDate = Calendar.getInstance().apply { add(Calendar.YEAR, -16) }.time
                tilBirthday.initAsDatePicker(null, null, maxDate) { y, m, d ->
                    String.format(DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR, d, m + 1, y)
                }
                onTextChanged { presenter.onChangeBirthdayText(it.toString()) }
            }
            passwordView.setPasswordValidCallback {
                presenter.onChangePasswordText(it.password, it.isValid)
            }
            viewAgreement.apply {
                getTextView().apply { text = getClickablePrivacyPolitics(requireContext()) }
                setOnCheckedListener { presenter.onChangeUserAgreement(it) }
            }
            btnSave.setOnClickListener {
                presenter.registerUser(true)
            }
        }
    }

    override fun setData(lastName: String?, firstName: String?, middleName: String?, isMiddleNameAbsent: Boolean, phone: String?, birthday: String?, password: String?, isAgree: Boolean) {
        mBinding.apply {
            etLastName.setText(lastName)
            etFirstName.setText(firstName)
            etMiddleName.apply {
                setText(middleName)
                isEnabled = !isMiddleNameAbsent
            }
            scNoMiddleName.isChecked = isMiddleNameAbsent
            etPhone.setPhoneText(phone)
            etBirthday.setText(birthday)
            passwordView.setPasswords(password)
            viewAgreement.setChecked(isAgree)
        }
    }
    override fun showLastNameError(show: Boolean, error: String?) {
        mBinding.tilLastName.showCustomError(show)
        mBinding.tvTitleLastName.apply {
            changeTitleTextColor(show)
            text = error ?: getString(R.string.user_profile_last_name)
        }
    }

    override fun showFirstNameError(show: Boolean, error: String?) {
        mBinding.tilFirstName.showCustomError(show)
        mBinding.tvTitleFirstName.apply {
            changeTitleTextColor(show)
            text = error ?: getString(R.string.user_profile_name)
        }
    }

    override fun showMiddleNameError(show: Boolean, error: String?) {
        mBinding.tilMiddleName.showCustomError(show)
        mBinding.tvTitleMiddleName.apply {
            changeTitleTextColor(show)
            text = error ?: getString(R.string.user_profile_middle_name)
        }
    }

    override fun enableMiddleNameInput(enable: Boolean) {
        mBinding.etMiddleName.isEnabled = enable
        if (!enable) {
            mBinding.etMiddleName.setText("")
            presenter.onChangeMiddleNameText("")
        }
    }

    override fun showLoginError(show: Boolean) {
        mBinding.tvTitlePhone.changeTitleTextColor(show)
        mBinding.tilPhone.showCustomError(show)
    }

    override fun showBirthdayError(show: Boolean) {
        mBinding.tvTitleBirthday.changeTitleTextColor(show)
        mBinding.tilBirthday.showIconError(show)
    }

    override fun showPasswordError(show: Boolean) {
        mBinding.passwordView.showErrors(show)
    }

    override fun showUserAgreementError(show: Boolean) {
        mBinding.viewAgreement.showError(show)
    }

    override fun enableRegisterBtn(isEnable: Boolean) {
        mBinding.btnSave.isSelected = isEnable
    }

    override fun showPhoneIsNotUnique(login: String) {
        val message =
            if (presenter.getLoginType() == "phone") getString(R.string.confirm_phone_text, login)
            else getString(R.string.confirm_email_text, login)

        DefaultAlertDialog(
            requireContext(),
            null,
            message,
            getString(R.string.confirm_phone_positive),
            getString(R.string.event_register_no_form_negative)
        ).setSelectCallback { presenter.registerUser(false) }
    }

    override fun showCodeConfirmation(login: String) {
        if (presenter.getLoginType() == "phone") {
            findNavController().navigate(
                R.id.phoneCodeConfirmFragment,
                bundleOf("phone" to validatePhoneBeforeSend(login), "fromRegister" to true)
            )
        } else {
            findNavController().navigate(
                R.id.emailCodeConfirmFragment,
                bundleOf("email" to login, "fromRegister" to true)
            )
        }
    }

    override fun showCustomLoading() = mBinding.btnSave.showProgressLoading(true)
    override fun hideCustomLoading() = mBinding.btnSave.showProgressLoading(false)


    override fun binding() = FragmentRegistrationUserBinding::class.java
    override fun layout(): Int = R.layout.fragment_registration_user
    override val title: CharSequence by lazy { getString(R.string.auth_register) }
    override fun scrollingView(): View = mBinding.scrollViewContent
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {
        toolbarContent.getBackButton().setTint(R.color.main_brown_color_new)
    }


}
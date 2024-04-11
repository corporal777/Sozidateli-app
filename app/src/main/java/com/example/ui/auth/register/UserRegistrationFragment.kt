package com.example.ui.auth.register

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.R
import com.example.databinding.FragmentRegistrationUserBinding
import com.example.extensions.getClickablePrivacyPolitics
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.ConfirmPhoneDialog
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.ClickableSpan
import com.example.util.Utils.validatePhoneBeforeSend
import com.example.util.setTint
import com.example.util.showCustomTabsBrowser
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import com.example.extensions.removeUrlUnderline
import javax.inject.Inject
import javax.inject.Provider

class UserRegistrationFragment : BaseFragment<FragmentRegistrationUserBinding>(),
    UserRegistrationContract.View, ToolbarFragment {

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
                initFocused { hasFocus -> if (!hasFocus) presenter.onCheckLastNameValid() }
                initInput { presenter.onChangeLastNameText(it.toString()) }
            }
            etFirstName.apply {
                initFocused { hasFocus -> if (!hasFocus) presenter.onCheckFirstNameValid() }
                initInput { presenter.onChangeFirstNameText(it.toString()) }
            }
            etMiddleName.apply {
                initFocused { hasFocus -> if (!hasFocus) presenter.onCheckMiddleNameValid() }
                initInput { presenter.onChangeMiddleNameText(it.toString()) }
                scNoMiddleName.setOnCheckedChangeListener { _, isChecked ->
                    presenter.onMiddleNameIsAbsent(isChecked)
                }
            }
            etLogin.apply {
                initInput {
                    presenter.onChangeLoginText(it.toString())
                }
            }
            etBirthday.apply {
                initAsDateTimePicker(null) {
                    presenter.onChangeBirthdayText(it.toString())
                }
            }
            passwordView.setPasswordValidCallback {
                presenter.onChangePasswordText(it.password, it.isValid)
            }
            viewAgreement.apply {
                getTextView().apply {
                    text = getClickablePrivacyPolitics(requireContext())
                    highlightColor = ContextCompat.getColor(requireContext(), R.color.profile_id_text)
                    movementMethod = LinkMovementMethod.getInstance()
                    removeUrlUnderline()
                }
                setOnCheckedListener {
                    presenter.onChangeUserAgreement(it)
                }
            }
            btnSave.setOnClickListener {
                presenter.registerUser(true)
            }
        }
    }
    override fun showLastNameError(show: Boolean, error: String?) {
        if (error.isNullOrEmpty()) mBinding.etLastName.showError(show)
        else mBinding.etLastName.showTextError(error)
    }

    override fun showFirstNameError(show: Boolean, error: String?) {
        if (error.isNullOrEmpty()) mBinding.etFirstName.showError(show)
        else mBinding.etFirstName.showTextError(error)
    }

    override fun showMiddleNameError(show: Boolean, error: String?) {
        if (error.isNullOrEmpty()) mBinding.etMiddleName.showError(show)
        else mBinding.etMiddleName.showTextError(error)
    }

    override fun enableMiddleNameInput(enable: Boolean) {
        mBinding.etMiddleName.isEnabled = enable
        if (!enable) {
            mBinding.etMiddleName.setText("")
            presenter.onChangeMiddleNameText("")
        }
    }

    override fun showLoginError(show: Boolean) {
        mBinding.etLogin.showError(show)
    }

    override fun showBirthdayError(show: Boolean) {
        mBinding.etBirthday.showError(show)
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

        ConfirmPhoneDialog(
            requireContext(),
            message,
            getString(R.string.event_register_no_form_negative),
            getString(R.string.confirm_phone_positive)
        ).setSelectCallback { if (it) presenter.registerUser(false) }
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

    override fun layout(): Int = R.layout.fragment_registration_user
    override val title: CharSequence by lazy { getString(R.string.auth_register) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {
        toolbarContent.getBackButton().setTint(R.color.main_brown_color_new)
    }


}
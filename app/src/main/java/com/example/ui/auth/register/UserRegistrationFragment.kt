package com.example.ui.auth.register

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.R
import com.example.databinding.FragmentRegistrationUserBinding
import com.example.interfaces.ToolbarFragment
import com.example.ui.auth.confirm.ConfirmPhoneCodeFragmentArgs
import com.example.ui.base.BaseFragment
import com.example.ui.views.ConfirmPhoneDialog
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.setTint
import com.example.util.showCustomTabsBrowser
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class UserRegistrationFragment : BaseFragment<FragmentRegistrationUserBinding>(),
    UserRegistrationContract.View, ToolbarFragment {

    private lateinit var toolbarContent: ToolbarContent

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
            etMobilePhone.apply {
                initInput {
                    presenter.onChangeMobilePhoneText(it.toString())
                }
            }
            passwordView.setPasswordValidCallback {
                presenter.onChangePasswordText(it.password, it.isValid)
            }
            viewAgreement.apply {
                setClickableText(context.getString(R.string.auth_user_agreement), 52) {
                    showCustomTabsBrowser(context, getString(R.string.auth_agree_address))
                }
                setOnCheckedListener {
                    presenter.onChangeUserAgreement(it)
                }
            }
            btnRegister.setOnClickListener {
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

    override fun showPasswordError(show: Boolean) {
        mBinding.passwordView.showErrors(show)
    }

    override fun showMobilePhoneError(show: Boolean) {
        mBinding.etMobilePhone.showError(show)
    }

    override fun showUserAgreementError(show: Boolean) {
        mBinding.viewAgreement.showError(show)
    }

    override fun enableRegisterBtn(isEnable: Boolean) {
        mBinding.btnRegister.isSelected = isEnable
    }

    override fun showPhoneIsNotUnique(phone: String) {
        ConfirmPhoneDialog(
            requireContext(),
            getString(R.string.confirm_phone_text, phone),
            getString(R.string.event_register_no_form_negative),
            getString(R.string.confirm_phone_positive)

        ).setSelectCallback { if (it) presenter.registerUser(false) }
    }

    override fun showPhoneCodeConfirmation(phone: String) {
        val args = ConfirmPhoneCodeFragmentArgs.Builder(phone).build().toBundle()
        findNavController().navigate(R.id.phoneCodeConfirmFragment, args)
    }

    override fun showCustomLoading() = mBinding.btnRegister.showProgressLoading(true)
    override fun hideCustomLoading() = mBinding.btnRegister.showProgressLoading(false)

    override fun changeAppBarHeader(value: Float) {
        if (value <= 0){
            toolbarContent.getToolbarTitleView().alpha = 0f
            mBinding.tvRegisterTitle.alpha = 1f
        } else {
            toolbarContent.getToolbarTitleView().alpha = 0 + (value / 30)
            mBinding.tvRegisterTitle.alpha = 1 - (value / 8)
        }
    }

    override fun layout(): Int = R.layout.fragment_registration_user
    override val title: CharSequence by lazy { getString(R.string.auth_register) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: Int) { presenter.onScrollChange(scroll) }
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {
        this.toolbarContent = toolbarContent.apply {
            getBackButton().setTint(R.color.main_brown_color_new)
        }
    }


}
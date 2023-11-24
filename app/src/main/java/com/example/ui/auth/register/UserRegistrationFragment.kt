package com.example.ui.auth.register

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.example.R
import com.example.databinding.FragmentRegistrationUserBinding
import com.example.interfaces.ToolbarFragment
import com.example.ui.auth.register.email.newbuild.RegisterEmailContract
import com.example.ui.auth.register.email.newbuild.RegisterEmailPresenter
import com.example.ui.base.BaseFragment
import com.example.ui.views.CustomSpannableString
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.setTint
import com.example.util.showCustomTabsBrowser
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import setOnClickListener
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
                    presenter.onNoMiddleNameChecked(isChecked)
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
                presenter.registerUser()
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
        mBinding.etMiddleName.setInputEnabled(enable)
        if (!enable) {
            mBinding.etMiddleName.setText("")
            presenter.onChangeMiddleNameText("")
        }
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

    override fun layout(): Int = R.layout.fragment_registration_user
    override val title: CharSequence = ""
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: (value: Int) -> Unit) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {
        toolbarContent.getBackButton().setTint(R.color.main_brown_color_new)
    }
}
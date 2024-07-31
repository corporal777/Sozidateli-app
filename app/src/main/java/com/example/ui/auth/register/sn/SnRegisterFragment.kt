package com.example.ui.auth.register.sn

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.R
import com.example.databinding.FragmentRegisterSnBinding
import com.example.interfaces.ToolbarFragment
import com.example.ui.auth.confirm.email.ConfirmEmailCodeFragmentArgs
import com.example.ui.base.BaseFragment
import com.example.ui.views.dialogs.DefaultAlertDialog
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.setTint
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class SnRegisterFragment : BaseFragment<FragmentRegisterSnBinding>(), SnRegisterContract.View,
    ToolbarFragment {

    @InjectPresenter
    lateinit var presenter: SnRegisterPresenter

    @Inject
    lateinit var presenterProvider: Provider<SnRegisterPresenter>

    @ProvidePresenter
    fun providePresenter(): SnRegisterPresenter = presenterProvider.get().apply {
        snUser = SnRegisterFragmentArgs.fromBundle(requireArguments()).snUser
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            etLastName.initInput { presenter.onChangeLastName(it.toString()) }
            etFirstName.initInput { presenter.onChangeFirstName(it.toString()) }
            etMiddleName.apply {
                initInput { presenter.onChangeMiddleName(it.toString()) }
                scNoMiddleName.setOnCheckedChangeListener { _, isChecked ->
                    presenter.onMiddleNameIsAbsent(isChecked)
                }
            }
            etPhone.initInput { presenter.onChangeMobilePhone(it.toString()) }
            etBirthday.initAsDateTimePicker(null) {
                presenter.onChangeBirthday(it.toString())
            }
            etEmail.initInput { presenter.onChangeEmail(it.toString()) }
            btnContinue.setOnClickListener {
                presenter.onClickContinue(true)
            }
        }
    }

    override fun setUserData(
        lastName: String?,
        firstName: String?,
        middleName: String?,
        middleNameIsAbsent: Boolean,
        mobilePhone: String?,
        email: String?,
        birthday: String?
    ) {
        mBinding.apply {
            etLastName.setText(lastName)
            etFirstName.setText(firstName)
            etMiddleName.setText(middleName)
            scNoMiddleName.isChecked = middleNameIsAbsent
            etPhone.setText(mobilePhone ?: "")
            etEmail.setText(email)
            etBirthday.setText(birthday)
        }
    }

    override fun setMiddleNameAbsent(isAbsent: Boolean) {
        mBinding.etMiddleName.isEnabled = !isAbsent
        if (isAbsent) {
            mBinding.etMiddleName.setText("")
            presenter.onChangeMiddleName("")
        }
    }

    override fun showLastNameError(show: Boolean) = mBinding.etLastName.showError(show)
    override fun showFirstNameError(show: Boolean) = mBinding.etFirstName.showError(show)
    override fun showMiddleNameError(show: Boolean) = mBinding.etMiddleName.showError(show)
    override fun showPhoneError(show: Boolean) = mBinding.etPhone.showError(show)
    override fun showBirthdayError(show: Boolean) = mBinding.etBirthday.showError(show)
    override fun showEmailError(show: Boolean) {
        if (show) mBinding.etEmail.showTextError("Введите корректный e-mail")
        else mBinding.etEmail.showError(false)
    }

    override fun enableContinueButton(isEnable: Boolean) =
        mBinding.btnContinue.run { isSelected = isEnable }

    override fun showCustomLoading() = mBinding.btnContinue.showProgressLoading(true)
    override fun hideCustomLoading() = mBinding.btnContinue.showProgressLoading(false)


    override fun showEmailIsNotUnique(email: String?) {
        val message = getString(R.string.confirm_email_text, email)
        DefaultAlertDialog(
            requireContext(),
            null,
            message,
            getString(R.string.confirm_phone_positive),
            getString(R.string.event_register_no_form_negative)
        ).setSelectCallback { presenter.onClickContinue(false) }
    }

    override fun showEmailConfirmation(email: String) {
        findNavController().navigate(
            R.id.emailCodeConfirmFragment,
            bundleOf("email" to email, "fromRegister" to true)
        )
    }


    override fun layout() = R.layout.fragment_register_sn
    override val title: CharSequence by lazy { getString(R.string.auth_register_sn) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {
        toolbarContent.getBackButton().setTint(R.color.main_brown_color_new)
    }
}

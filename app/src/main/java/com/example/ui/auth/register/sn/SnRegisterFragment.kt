package com.example.ui.auth.register.sn

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.app.R
import com.example.app.databinding.FragmentRegisterSnBinding
import com.example.data.models.AuthResponse
import com.example.extensions.initAsDatePicker
import com.example.extensions.onTextChanged
import com.example.ui.base.BaseToolbarFragment
import com.example.ui.views.dialogs.DefaultAlertDialog
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR
import com.example.util.changeTitleTextColor
import com.example.util.getColor
import com.example.util.setTint
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Provider

class SnRegisterFragment : BaseToolbarFragment<FragmentRegisterSnBinding>(), SnRegisterContract.View{

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
            etLastName.onTextChanged { presenter.onChangeLastName(it.toString()) }
            etFirstName.onTextChanged { presenter.onChangeFirstName(it.toString()) }
            etMiddleName.apply {
                onTextChanged { presenter.onChangeMiddleName(it.toString()) }
                scNoMiddleName.setOnCheckedChangeListener { _, isChecked ->
                    presenter.onMiddleNameIsAbsent(isChecked)
                }
            }
            etPhone.onInputTextChanged { presenter.onChangeMobilePhone(it.toString()) }
            etBirthday.apply {
                val maxDate = Calendar.getInstance().apply { add(Calendar.YEAR, -16) }.time
                tilBirthday.initAsDatePicker(null, null, maxDate) { y, m, d ->
                    String.format(DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR, d, m + 1, y)
                }
                onTextChanged { presenter.onChangeBirthday(it.toString()) }
            }
            etEmail.onTextChanged { presenter.onChangeEmail(it.toString()) }
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
            etMiddleName.apply {
                setText(middleName)
                isEnabled = !middleNameIsAbsent
            }
            scNoMiddleName.isChecked = middleNameIsAbsent
            etPhone.setPhoneText(mobilePhone)
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

    override fun showLastNameError(show: Boolean) {
        mBinding.tvTitleLastName.changeTitleTextColor(show)
        mBinding.tilLastName.showCustomError(show)
    }

    override fun showFirstNameError(show: Boolean){
        mBinding.tvTitleFirstName.changeTitleTextColor(show)
        mBinding.tilFirstName.showCustomError(show)
    }

    override fun showMiddleNameError(show: Boolean) {
        mBinding.tvTitleMiddleName.changeTitleTextColor(show)
        mBinding.tilMiddleName.showCustomError(show)
    }

    override fun showPhoneError(show: Boolean) {
        mBinding.tvTitlePhone.changeTitleTextColor(show)
        mBinding.tilPhone.showCustomError(show)
    }

    override fun showBirthdayError(show: Boolean) {
        mBinding.tvTitleBirthday.changeTitleTextColor(show)
        mBinding.tilBirthday.showIconError(show)
    }

    override fun showEmailError(show: Boolean) {
        mBinding.tvTitleEmail.apply {
            changeTitleTextColor(show)
            text = if (show && !mBinding.etEmail.getText().isNullOrEmpty()) "Введите корректный e-mail"
            else getString(R.string.email_for_communication_text)
        }
        mBinding.tilEmail.showCustomError(show)

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

    override fun showEmailConfirmation(email: String?, auth: AuthResponse) {
        findNavController().navigate(
            R.id.emailCodeConfirmFragment,
            bundleOf("email" to email, "auth" to auth)
        )
    }


    override fun layout() = R.layout.fragment_register_sn
    override fun binding() = FragmentRegisterSnBinding::class.java
    override val title: CharSequence by lazy { getString(R.string.auth_register_sn) }
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {
        toolbarContent.getBackButton().setTint(R.color.main_brown_color_new)
    }
    override fun scrollingView(): View = mBinding.svContent
}

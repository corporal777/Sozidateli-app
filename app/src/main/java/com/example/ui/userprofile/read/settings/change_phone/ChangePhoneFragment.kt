package com.example.ui.userprofile.read.settings.change_phone

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.FieldDetails
import com.example.databinding.BottomSheetChangePhoneBinding
import com.example.ui.base.bottomSheet.BaseBottomSheetFragment
import com.example.ui.main.MainActivity
import com.example.ui.userprofile.read.settings.change_phone.confirm_phone.ConfirmPhoneFragment
import com.example.ui.views.ConfirmPhoneDialog
import com.example.util.initSwitch
import isValidPhoneNumber
import javax.inject.Inject
import javax.inject.Provider

class ChangePhoneFragment(
    val phone: FieldDetails?
) : BaseBottomSheetFragment<BottomSheetChangePhoneBinding>(),
    ChangePhoneContract.View {


    @InjectPresenter(type = PresenterType.WEAK, tag = CHANGE_PHONE_FRAGMENT_TAG)
    lateinit var presenter: ChangePhonePresenter

    @Inject
    lateinit var presenterProvider: Provider<ChangePhonePresenter>

    @ProvidePresenter(type = PresenterType.WEAK, tag = CHANGE_PHONE_FRAGMENT_TAG)
    fun providePresenter(): ChangePhonePresenter = presenterProvider.get().apply {
        this.mobilePhone = phone?.value ?: ""
        this.oldMobilePhone = phone?.value ?: ""
        this.isConfirmed = phone?.isConfirmed ?: false
        this.isVisible = phone?.isVisible ?: false
        this.phoneField = phone

    }

    private var confirmPhoneDialog: ConfirmPhoneFragment? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            btnPhoneConfirm.setOnClickListener {
                val phone = etMobilePhone.getFullNumberWithPlus()
                if (validatePhone(phone)) {
                    if (phone.isValidPhoneNumber(requireContext())) {
                        hideKeyboard(it)
                        presenter.onConfirmPhoneClick(phone)
                    } else {
                        etMobilePhone.showError(true)
                    }
                }
            }
            btnSave.setOnClickListener {
                val phone = etMobilePhone.getFullNumberWithPlus()
                if (validatePhone(phone)) {
                    if (phone.isValidPhoneNumber(requireContext())) {
                        hideKeyboard(it)
                        presenter.checkPhoneIsUnique(phone)
                    } else {
                        etMobilePhone.showError(true)
                    }
                }
            }
            btnClose.setOnClickListener {
                dismiss()
            }
        }

    }

    override fun setUserPhone(phone: String?) {
        mBinding.btnSave.isEnabled = !phone.isNullOrEmpty()
        mBinding.etMobilePhone.apply {
            focusOnInput(getEditTextLayout(), true)
            setPhone(phone ?: "")
            if (!phone.isNullOrEmpty()){
                getEditTextLayout().setSelection(phone.length + 1)
            }
            getPhoneCallback {
                presenter.setNewPhone(it)
                presenter.setNewPhoneIsConfirmed()
            }
            getPhoneCallbackWithoutPlus {
                mBinding.btnSave.isEnabled = validatePhone(it)
            }
        }
    }

    override fun setUserPhoneIsConfirmed(isConfirmed: Boolean) {
        mBinding.apply {
            tvPhoneConfirmed.isVisible = isConfirmed
            btnPhoneConfirm.isVisible = !isConfirmed
        }
    }

    override fun setUserPhoneIsVisible(isVisible: Boolean) {
        mBinding.apply {
            scMobilePhone.initSwitch(isVisible) {
                presenter.setNewPhoneIsVisible(it)
            }
        }
    }

    override fun setPhoneIsUpdatedSuccessfully() {
        if (confirmPhoneDialog != null) {
            confirmPhoneDialog?.dismiss()
            confirmPhoneDialog = null
        }
        showToast(getString(R.string.phone_mobile_is_updated_successfully))
        dismiss()
    }

    override fun showPhoneNotUnique(phone: String, type: ChangePhonePresenter.ConfirmType) {
        ConfirmPhoneDialog(
            requireContext(),
            getString(R.string.confirm_phone_text, phone),
            getString(R.string.revoke),
            getString(R.string.confirm_phone_positive)
        )
            .setSelectCallback {
                if (it) {
                    if (type == ChangePhonePresenter.ConfirmType.CONFIRM) {
                        presenter.onSendCodeClick()
                    } else {
                        presenter.updatePhoneData()
                    }
                }
            }
    }

    override fun showConfirmPhoneDialog(phone: String) {
        if (confirmPhoneDialog == null) {
            confirmPhoneDialog = ConfirmPhoneFragment(phone)
            confirmPhoneDialog?.show(
                requireActivity().supportFragmentManager,
                "confirm_phone_dialog"
            )
        }
        presenter.startTimerForResendCode(phone)
        confirmPhoneDialog?.setResendCallback {
            presenter.onSendCodeClick()
        }
        confirmPhoneDialog?.setConfirmCallback {
            (requireActivity() as MainActivity).setIgnoreTokenListener(true)
            presenter.onConfirmCodeClick(it)
        }
    }

    override fun setTimerForResendConfirmCode(seconds: Int) {
        confirmPhoneDialog?.setCodeResend(seconds)
    }


    private fun validatePhone(phone: String): Boolean {
        if (phone.isNullOrBlank()) {
            mBinding.etMobilePhone.showEmptyError(true)
            return false
        } else {
            mBinding.etMobilePhone.showEmptyError(false)
        }
        return true
    }


    companion object {
        const val CHANGE_PHONE_FRAGMENT_TAG = "change_phone_tag"
    }

    override fun layout(): Int = R.layout.bottom_sheet_change_phone


}
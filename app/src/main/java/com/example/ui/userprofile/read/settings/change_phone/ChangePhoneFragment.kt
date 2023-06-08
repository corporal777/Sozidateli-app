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
import com.example.ui.userprofile.read.settings.confirm_phone_email.ConfirmEmailPhoneFragment
import com.example.ui.views.ConfirmPhoneDialog
import com.example.ui.views.SetPasswordDialog
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

    private lateinit var passwordDialog: SetPasswordDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            btnPhoneConfirm.setOnClickListener {
                val phone = etMobilePhone.getFullNumberWithPlus()
                if (validatePhone(phone)) {
                    hideKeyboard(it)
                    presenter.withUpdate = false
                    presenter.checkPhoneIsUnique(phone)
                }
            }
            btnSave.setOnClickListener {
                val phone = etMobilePhone.getFullNumberWithPlus()
                if (validatePhone(phone)) {
                    hideKeyboard(it)
                    presenter.withUpdate = true
                    presenter.onSaveNewPhoneClick(phone)
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
            setCursorPosition()
            getPhoneCallback {
                mBinding.btnSave.isEnabled = validatePhone(it)
                presenter.setNewPhone(it)
                presenter.setNewPhoneIsConfirmed(it)
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

    override fun showPhoneIsUpdatedSuccessfully() {
        showToast(getString(R.string.phone_mobile_is_updated_successfully))
        dismiss()
    }

    override fun showEnterPassword(phone: String) {
        passwordDialog = SetPasswordDialog(requireActivity())
            .setSelectCallback {
                presenter.checkPassword(it, phone)
            }
    }

    override fun hideEnterPassword() {
        passwordDialog.hideDialog()
    }

    override fun showPhoneNotUnique(phone: String) {
        ConfirmPhoneDialog(
            requireContext(),
            getString(R.string.confirm_phone_text, phone),
            getString(R.string.revoke),
            getString(R.string.confirm_phone_positive)
        )
            .setSelectCallback {
                if (it) {
                    presenter.onShowPhoneConfirm(phone)
                }
            }
    }

    override fun showPhoneConfirmation(phone: String) {
        val confirmPhoneDialog = ConfirmEmailPhoneFragment(phone)
        confirmPhoneDialog.show(requireActivity().supportFragmentManager, "confirm_phone_dialog")
        confirmPhoneDialog.setConfirmCallback {
            setUserPhoneIsConfirmed(true)
            presenter.isConfirmed = true
            if (presenter.isWithUpdate()) {
                presenter.updatePhoneData()
            }
        }
    }

    private fun validatePhone(phone: String): Boolean {
        var isValid = true
        if (phone.isNullOrEmpty() || phone.length < 3) {
            mBinding.etMobilePhone.showEmptyError(true)
            isValid = false
        } else if (!phone.isValidPhoneNumber(requireContext())) {
            mBinding.etMobilePhone.showError(true)
            isValid = false
        } else {
            mBinding.etMobilePhone.showError(false)
            isValid = true
        }
        return isValid
    }


    companion object {
        const val CHANGE_PHONE_FRAGMENT_TAG = "change_phone_tag"
    }

    override fun layout(): Int = R.layout.bottom_sheet_change_phone


}
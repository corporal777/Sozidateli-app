package com.example.ui.userprofile.edit.phone

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.R
import com.example.data.models.FieldDetails
import com.example.databinding.FragmentChangePhoneBinding
import com.example.ui.auth.confirm.phone.ConfirmPhoneCodeFragmentArgs
import com.example.ui.base.BaseFragment
import com.example.ui.base.bottomSheet.BaseBottomSheetFragment
import com.example.ui.userprofile.edit.confirm.ConfirmEmailPhoneFragment
import com.example.ui.views.ConfirmPhoneDialog
import com.example.ui.views.SetPasswordDialog
import com.example.util.Utils
import com.example.util.initSwitch
import isValidPhoneNumber
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class ChangePhoneFragment : BaseFragment<FragmentChangePhoneBinding>(), ChangePhoneContract.View {

    @InjectPresenter
    lateinit var presenter: ChangePhonePresenter

    @Inject
    lateinit var presenterProvider: Provider<ChangePhonePresenter>

    @ProvidePresenter
    fun providePresenter(): ChangePhonePresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            etPhone.initInput {
                presenter.onChangePhone(it.toString())
            }
            btnConfirm.apply {
                setButtonTextColor(R.color.text_color_repeat_code_button)
                setOnClickListener {
                    hideKeyboard(it)
                    presenter.onShowConfirmClick(true)
                }
            }
            btnSave.apply {
                setButtonTextColor(R.color.text_color_repeat_code_button)
                setOnClickListener {
                    hideKeyboard(it)
                    presenter.onSavePhoneClick(true)
                }
            }
            btnClose.setOnClickListener {
                navigateUp()
            }
        }
    }

    override fun setUserPhone(phone: String?, isVisible: Boolean) {
        mBinding.apply {
            etPhone.setText(phone)
            scPhone.initSwitch(isVisible) {
                presenter.onChangePhoneVisible(it)
            }
        }
    }

    override fun enableBtnSave(enabled: Boolean) = mBinding.btnSave.run { isEnabled = enabled }
    override fun enableBtnConfirm(isConfirmed: Boolean) = mBinding.run {
        btnConfirm.isVisible = !isConfirmed
        etPhone.showIcon(isConfirmed)
    }

    override fun showCustomLoading() = mBinding.btnSave.showProgressLoading(true)
    override fun hideCustomLoading() = mBinding.btnSave.showProgressLoading(false)

    override fun showPhoneNotUnique(phone: String) {
        ConfirmPhoneDialog(
            requireContext(),
            getString(R.string.confirm_phone_text, phone),
            getString(R.string.revoke),
            getString(R.string.confirm_phone_positive)
        ).setSelectCallback {
            if (it) presenter.onSavePhoneClick(false)
        }
    }

    override fun showPhoneConfirmation(phone: String, withAdd: Boolean) {
        findNavController().navigate(
            R.id.phoneCodeConfirmFragment,
            bundleOf("phone" to phone, "fromRegister" to false),
            if (!withAdd) navOptions { popUpTo(R.id.changePhoneFragment) { inclusive = true } }
            else null
        )
    }

    override fun animationType(): AnimType = AnimType.FADE
    override fun layout(): Int = R.layout.fragment_change_phone
}
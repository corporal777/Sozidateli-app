package com.example.ui.userprofile.edit.email

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.example.R
import com.example.databinding.BottomSheetChangeEmailBinding
import com.example.ui.base.bottomSheet.BaseBottomSheetFragment
import com.example.ui.userprofile.edit.confirm.ConfirmEmailPhoneFragment
import com.example.ui.views.ConfirmPhoneDialog
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class ChangeEmailFragment(
    val email: String?
) : BaseBottomSheetFragment<BottomSheetChangeEmailBinding>(),
    ChangeEmailContract.View {


    @InjectPresenter(tag = CHANGE_EMAIL_FRAGMENT_TAG)
    lateinit var presenter: ChangeEmailPresenter

    @Inject
    lateinit var presenterProvider: Provider<ChangeEmailPresenter>

    @ProvidePresenter(tag = CHANGE_EMAIL_FRAGMENT_TAG)
    fun providePresenter(): ChangeEmailPresenter = presenterProvider.get().apply {
        this.currentEmail = email?:""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        focusOnInput(mBinding.etNewEmail, true)
        mBinding.apply {
            etNewEmail.doAfterTextChanged { tilNewEmail.error = null }
            btnClose.setOnClickListener {
                dismiss()
            }
            btnSave.setOnClickListener {
                hideKeyboard(it)
                presenter.checkEmailIsUnique(etNewEmail.text.toString())
            }

        }
    }


    override fun setCurrentEmail(currentEmail: String) {
        if (!currentEmail.isNullOrEmpty()) {
            mBinding.tvCurrentEmail.setText(currentEmail)
        } else {
            mBinding.apply {
                tvCurrentEmail.isVisible = false
                tvCurrentLogin.isVisible = false
            }
        }

    }

    override fun showEmailNotValid(email: String) {
        mBinding.tilNewEmail.error = getString(R.string.new_login_invalid)
    }

    override fun showEmailNotUnique(email: String) {
        ConfirmPhoneDialog(
            requireContext(), getString(R.string.confirm_email_text, email),
            getString(R.string.revoke), getString(R.string.confirm_phone_positive)
        )
            .setSelectCallback {
                if (it) {
                    presenter.onShowEmailConfirm(email)
                }
            }
    }

    override fun showEmailConfirm(email: String) {
        val confirmEmail = ConfirmEmailPhoneFragment(email)
        confirmEmail.show(requireActivity().supportFragmentManager, "confirm_email_dialog")
        confirmEmail.setConfirmCallback {
            presenter.updateEmail(email)
        }
    }

    override fun showChangeEmailComplete() {
        showToast(getString(R.string.email_change_confirm_success))
        dismiss()
    }


    companion object {
        const val CHANGE_EMAIL_FRAGMENT_TAG = "change_email_tag"
    }

    override fun layout(): Int = R.layout.bottom_sheet_change_email

}
package com.example.ui.userprofile.common.email

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.R
import com.example.databinding.FragmentChangeEmailBinding
import com.example.ui.base.BaseFragment
import com.example.ui.views.dialogs.DefaultAlertDialog
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class ChangeEmailFragment : BaseFragment<FragmentChangeEmailBinding>(), ChangeEmailContract.View {

    @InjectPresenter
    lateinit var presenter: ChangeEmailPresenter

    @Inject
    lateinit var presenterProvider: Provider<ChangeEmailPresenter>

    @ProvidePresenter
    fun providePresenter(): ChangeEmailPresenter = presenterProvider.get().apply {
        currentEmail = ChangeEmailFragmentArgs.fromBundle(requireArguments()).email
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            etNewEmail.initInput {
                presenter.onChangeEmailText(it.toString())
            }
            btnClose.setOnClickListener {
                navigateUp()
            }
            btnSave.setOnClickListener {
                hideKeyboard(it)
                presenter.onCheckEmailIsUnique()
            }

        }
    }


    override fun setCurrentEmail(currentEmail: String?) {
        mBinding.etCurrentEmail.apply {
            isVisible = !currentEmail.isNullOrEmpty()
            setText(currentEmail)
        }
    }

    override fun showEmailError(show: Boolean) {
        mBinding.etNewEmail.showError(show)
    }


    override fun showEmailNotUnique(email: String) {
        DefaultAlertDialog(
            requireContext(),
            null,
            getString(R.string.confirm_email_text, email),
            getString(R.string.confirm_phone_positive),
            getString(R.string.event_register_no_form_negative)
        ).setSelectCallback { presenter.onShowEmailConfirm() }
    }

    override fun showEmailConfirm(email: String) {
        findNavController().navigate(
            R.id.emailCodeConfirmFragment,
            bundleOf("email" to email, "fromRegister" to false),
            navOptions { popUpTo(R.id.changeEmailFragment) { inclusive = true } }
        )
    }

    override fun enableBtnSave(enable: Boolean) = mBinding.btnSave.run { isSelected = enable }
    override fun showCustomLoading() = mBinding.btnSave.showProgressLoading(true)
    override fun hideCustomLoading() = mBinding.btnSave.showProgressLoading(false)

    override fun animationType(): AnimType = AnimType.FADE
    override fun layout(): Int = R.layout.fragment_change_email
}
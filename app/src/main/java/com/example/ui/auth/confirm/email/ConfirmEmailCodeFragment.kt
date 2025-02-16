package com.example.ui.auth.confirm.email

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.example.app.R
import com.example.app.databinding.FragmentEmailCodeConfirmBinding
import com.example.ui.base.BaseToolbarFragment
import com.example.ui.main.MainActivity
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.setTint
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class ConfirmEmailCodeFragment : BaseToolbarFragment<FragmentEmailCodeConfirmBinding>(),
    ConfirmEmailCodeContract.View {

    @InjectPresenter
    lateinit var presenter: ConfirmEmailCodePresenter

    @Inject
    lateinit var presenterProviderFinish: Provider<ConfirmEmailCodePresenter>

    @ProvidePresenter
    fun providePresenter(): ConfirmEmailCodePresenter = presenterProviderFinish.get().apply {
        val args = ConfirmEmailCodeFragmentArgs.fromBundle(requireArguments())
        email = args.email
        authResponse = args.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            emailCodeView.doOnCodeChanged {
                presenter.onChangeCode(it.code)
            }
            btnSendAgain.apply {
                setButtonTextColor(R.color.text_color_repeat_code_button)
                setOnClickListener { presenter.onSendCodeAgain() }
            }
            btnConfirm.setOnClickListener {
                hideKeyboard()
                presenter.onConfirmEmail()
            }
        }
    }

    override fun setEmail(email: String?) {
        mBinding.tvEmail.text = email
    }

    override fun setTimeLeft(seconds: Int) {
        if (seconds > 0) mBinding.btnSendAgain.setButtonText("Отправить повторно · 0:$seconds")
        else mBinding.btnSendAgain.setButtonText("Отправить повторно")
    }

    override fun setCanSendAgain(canSend: Boolean) = mBinding.btnSendAgain.run { isEnabled = canSend }
    override fun setConfirmButton(enabled: Boolean) = mBinding.btnConfirm.run { isEnabled = enabled }

    override fun showCodeError(show: Boolean) {
        mBinding.emailCodeView.showError(show)
        mBinding.tvCodeError.isVisible = show
    }

    override fun setFinishRegister(isFinish: Boolean) {
        (requireActivity() as MainActivity).setFinishRegister(isFinish)
    }

    override fun navigateUp() {
        setFragmentResult("confirm", bundleOf("email" to presenter.email))
        super.navigateUp()
    }

    override fun showCustomLoading(type: Int) {
        mBinding.apply {
            if (type == 1) btnConfirm.showProgressLoading(true)
            else btnSendAgain.showProgressLoading(true)
        }
    }

    override fun hideCustomLoading(type: Int) {
        mBinding.apply {
            if (type == 1) btnConfirm.showProgressLoading(false)
            else btnSendAgain.showProgressLoading(false)
        }
    }


    override fun binding() = FragmentEmailCodeConfirmBinding::class.java
    override fun layout(): Int = R.layout.fragment_email_code_confirm
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {
        toolbarContent.getBackButton().setTint(R.color.main_brown_color_new)
    }
}
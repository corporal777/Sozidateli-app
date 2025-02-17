package com.example.ui.userprofile.common.password.confirm

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.app.R
import com.example.app.databinding.FragmentPhoneCodeConfirmBinding
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseVBFragment
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.setTint
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class PhoneConfirmPasswordFragment : BaseVBFragment<FragmentPhoneCodeConfirmBinding>(),
    PhoneConfirmPasswordContract.View, ToolbarFragment {

    @InjectPresenter
    lateinit var presenter: PhoneConfirmPasswordPresenter

    @Inject
    lateinit var presenterProvider: Provider<PhoneConfirmPasswordPresenter>

    @ProvidePresenter
    fun providePresenter(): PhoneConfirmPasswordPresenter = presenterProvider.get().apply {
        navArgs<PhoneConfirmPasswordFragmentArgs>().value.also {
            mobilePhone = it.phone
            userId = it.userId
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            phoneCodeView.doOnCodeChanged {
                presenter.onChangeCode(it.code)
            }
            btnCallAgain.apply {
                setButtonTextColor(R.color.text_color_repeat_code_button)
                setOnClickListener {
                    presenter.onSendCallAgain()
                }
            }
            btnConfirm.setOnClickListener {
                hideKeyboard()
                presenter.onConfirmMobilePhone()
            }
        }
    }

    override fun setMobilePhone(mobilePhone: String?) {
        mBinding.tvMobilePhone.text = mobilePhone
    }

    override fun setTimeLeft(seconds: Int) {
        if (seconds > 0) mBinding.btnCallAgain.setButtonText("Позвонить повторно · 0:$seconds")
        else mBinding.btnCallAgain.setButtonText("Позвонить повторно")
    }

    override fun setCanCallAgain(canCall: Boolean) = mBinding.btnCallAgain.run { isEnabled = canCall }
    override fun setConfirmButton(enabled: Boolean) = mBinding.btnConfirm.run { isEnabled = enabled }

    override fun showCodeError(show: Boolean) {
        mBinding.phoneCodeView.showError(show)
        mBinding.tvCodeError.isVisible = show
    }


    override fun showCustomLoading(type: Int) {
        mBinding.apply {
            if (type == 1) btnConfirm.showProgressLoading(true)
            else btnCallAgain.showProgressLoading(true)
        }
    }

    override fun hideCustomLoading(type: Int) {
        mBinding.apply {
            if (type == 1) btnConfirm.showProgressLoading(false)
            else btnCallAgain.showProgressLoading(false)
        }
    }


    override fun showResetPasswordFragment(code: String, userId: String) {
        findNavController().navigate(
            R.id.resetPasswordFragment,
            bundleOf("loginType" to "phone", "code" to code, "userId" to userId)
        )
    }

    override fun binding() = FragmentPhoneCodeConfirmBinding::class.java
    override fun layout(): Int = R.layout.fragment_phone_code_confirm
    override val title: CharSequence = ""
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {
        toolbarContent.getBackButton().setTint(R.color.main_brown_color_new)
    }
}
package com.example.ui.userprofile.read.settings.change_phone.confirm_phone

import android.os.Bundle
import android.view.View
import androidx.core.view.isInvisible
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.databinding.BottomSheetConfirmPhoneBinding
import com.example.ui.base.bottomSheet.BaseBottomSheetFragment
import com.example.ui.userprofile.read.settings.change_phone.ChangePhoneContract
import com.example.ui.userprofile.read.settings.change_phone.ChangePhoneFragment
import com.example.ui.userprofile.read.settings.change_phone.ChangePhonePresenter
import com.example.util.Utils
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class ConfirmPhoneFragment(val phone: String) :
    BaseBottomSheetFragment<BottomSheetConfirmPhoneBinding>(), ConfirmPhoneContract.View {


    private var clickConfirmCode: (code: String) -> Unit = {}
    private var clickResendCode: () -> Unit = {}

    @InjectPresenter(type = PresenterType.WEAK, tag = CONFIRM_PHONE_FRAGMENT_TAG)
    lateinit var presenter: ConfirmPhonePresenter

    @Inject
    lateinit var presenterProvider: Provider<ConfirmPhonePresenter>

    @ProvidePresenter(type = PresenterType.WEAK, tag = CONFIRM_PHONE_FRAGMENT_TAG)
    fun providePresenter(): ConfirmPhonePresenter = presenterProvider.get().apply {
        this.mobilePhone = phone
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            tvPhone.text = phone
            btnResend.isEnabled = false
            etCode.apply {
                onTextChanged {
                    btnConfirm.isEnabled = !it.isNullOrEmpty()
                }
            }
            btnConfirm.apply {
                btnConfirm.isEnabled = false
                setOnClickListener {
                    hideKeyboard(it)
                    clickConfirmCode.invoke(etCode.text.toString())
                }
            }
            btnClose.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun setContentVisible(canShow: Boolean) {
        mBinding.contentContainer.apply {
            visibility = if (canShow){
                View.VISIBLE
            }else {
                View.INVISIBLE
            }
        }
        focusOnInput(mBinding.etCode, canShow)
    }


    override fun setResendButtonEnable(seconds: Int) {
        val quantity = Utils.timerFormatter(seconds, requireContext())
        mBinding.apply {
            if (seconds <= 0) {
                btnResend.apply {
                    isEnabled = true
                    setOnClickListener {
                        presenter.sendCode()
                    }
                }
                mBinding.tvTimer.isInvisible = true
            } else {
                btnResend.isEnabled = false
                mBinding.tvTimer.isInvisible = false
                mBinding.tvTimer.text =
                    String.format(
                        getString(R.string.auth_register_confirm_email_timer_two),
                        quantity
                    )
            }
        }
    }

    override fun setCanStartTimer() {
        presenter.startTimerForResendCode()
    }

    fun setCodeResend(seconds: Int) {
        val quantity = Utils.timerFormatter(seconds, requireContext())
        mBinding.apply {
            if (seconds <= 0) {
                btnResend.apply {
                    isEnabled = true
                    setOnClickListener {
                        clickResendCode.invoke()
                    }
                }
                mBinding.tvTimer.isInvisible = true
            } else {
                btnResend.isEnabled = false
                mBinding.tvTimer.isInvisible = false
                mBinding.tvTimer.text =
                    String.format(
                        getString(R.string.auth_register_confirm_email_timer_two),
                        quantity
                    )
            }
        }
    }

    override fun showProgressLoading() {
        mBinding.progressView.showProgressBar()
    }

    override fun hideProgressLoading() {
        mBinding.progressView.hideProgressBar()
    }

    fun setResendCallback(block: () -> Unit): ConfirmPhoneFragment {
        clickResendCode = block
        return this
    }

    fun setConfirmCallback(block: (code: String) -> Unit): ConfirmPhoneFragment {
        clickConfirmCode = block
        return this
    }

    companion object {
        const val CONFIRM_PHONE_FRAGMENT_TAG = "confirm_phone_tag"
    }

    override fun layout(): Int = R.layout.bottom_sheet_confirm_phone
}
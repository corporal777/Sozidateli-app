package com.example.ui.user.phoneconfirm

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_confirm_phone.*
import javax.inject.Inject
import javax.inject.Provider

class PhoneConfirmFragment : BaseFragment(), PhoneConfirmContract.View, ToolbarFragment {

    override val title: String? = null

    override fun layout() = R.layout.fragment_confirm_phone

    private val args: PhoneConfirmFragmentArgs by navArgs()

    private val timerMessage by lazy { getString(R.string.phone_confirm_timer) }

    @InjectPresenter
    lateinit var presenter: PhoneConfirmPresenter

    @Inject
    lateinit var presenterProvider: Provider<PhoneConfirmPresenter>

    @ProvidePresenter
    fun providePresenter(): PhoneConfirmPresenter = presenterProvider.get().apply {
        phone = args.phone
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnResend.setOnClickListener { presenter.onResendClick() }
    }

    override fun setCanResend(canResend: Boolean) {
        btnResend.isEnabled = canResend
    }

    override fun setTimeLeft(time: String?) {
        if (time == null) {
            tvTimer.isVisible = false
        } else {
            tvTimer.text = String.format(timerMessage, time)
            tvTimer.isVisible = false
        }
    }

    override fun setPhone(phone: String) {
        tvPhone.text = phone
    }
}
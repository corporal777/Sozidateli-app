package com.example.ui.qrscanner.auth

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.view.isInvisible
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.app.R
import com.example.app.databinding.FragmentAuthWebsiteBinding
import com.example.data.models.QrAuthResponse
import com.example.extensions.longToTime
import com.example.interfaces.BackgroundImageFragment
import com.example.ui.base.BaseVBFragment
import com.example.ui.views.loading.CustomLoadingButton
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class AuthWebsiteFragment : BaseVBFragment<FragmentAuthWebsiteBinding>(), BackgroundImageFragment,
    AuthWebsiteContract.View {

    @InjectPresenter
    lateinit var mPresenter: AuthWebsitePresenter

    @Inject
    lateinit var presenterProvider: Provider<AuthWebsitePresenter>

    @ProvidePresenter
    fun providePresenter(): AuthWebsitePresenter = presenterProvider.get().apply {
        val args = AuthWebsiteFragmentArgs.fromBundle(requireArguments())
        token = args.qrCode
        socketId = args.socketId
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            btnConfirm.setOnClickListener { mPresenter.onConfirmAuthClick(btnConfirm) }
            btnDoNotConfirm.setOnClickListener { mPresenter.onNotConfirmAuthClick(btnDoNotConfirm) }
        }
    }

    override fun setEnterData(data: QrAuthResponse) {
        mBinding.apply {
            tvDevice.text = "Устройство:   " + data.device
            tvIPAddress.text = "IP адрес:   " + data.ipAddress
            tvTime.text = "Время:   " + longToTime(data.timeStamp)
        }
    }

    override fun showEventList() {
        findNavController().navigate(
            R.id.recommendations_fragment, null, NavOptions.Builder()
                .setPopUpTo(R.id.main_navigation, true)
                .build()
        )
    }


    override fun showContent() {
        mBinding.clContent.isInvisible = false
    }

    override fun hideContent() {
        mBinding.clContent.isInvisible = true
    }

    override fun showCustomLoading(view: CustomLoadingButton) = view.run {
        showProgressLoading(true)
    }

    override fun hideCustomLoading(view: CustomLoadingButton) = view.run {
        showProgressLoading(false)
    }

    override fun getFragmentBackgroundDrawable(): Drawable? = null
    override val isLightStatus = false

    override fun binding() = FragmentAuthWebsiteBinding::class.java
    override fun layout() = R.layout.fragment_auth_website
}

package com.example.ui.qrscanner.auth

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.QrAuthResponse
import com.example.databinding.FragmentAuthWebsiteBinding
import com.example.extensions.longToTime
import com.example.interfaces.BackgroundImageFragment
import com.example.ui.base.BaseFragmentNew
import com.example.ui.views.dialogs_new.MessageDialogWithBrownButton
import com.example.util.AuthBackground
import javax.inject.Inject
import javax.inject.Provider

class AuthWebsiteFragment : BaseFragmentNew<FragmentAuthWebsiteBinding>(), BackgroundImageFragment,
    AuthWebsiteContract.View {

    override val isLightStatus = false

    private val mArgs: AuthWebsiteFragmentArgs by navArgs()
    private var mCode = ""


    @InjectPresenter
    lateinit var mPresenter: AuthWebsitePresenter

    @Inject
    lateinit var presenterProvider: Provider<AuthWebsitePresenter>

    @ProvidePresenter
    fun providePresenter(): AuthWebsitePresenter = presenterProvider.get().apply {
        mArgs.let {
            mCode = it.qrCode
            val mTokenFromCode = getTokenFromQrCode(mCode)
            this.mToken = mTokenFromCode
        }
    }


    override fun setEnterData(data: QrAuthResponse) {
        mBinding.apply {
            tvDevice.text = data.mDevice
            tvIPAddress.text = data.mIPAddress
            tvTime.text = longToTime(data.mTimeStamp)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            btnConfirm.setOnClickListener { mPresenter.onConfirmEnterToWebsiteClick() }
            btnDoNotConfirm.setOnClickListener { mPresenter.onDoNotConfirmToEnterWebsiteClick() }
        }
    }


    override fun showSuccessEnterMessage() {
        val message = "Авторизация прошла успешно"
        MessageDialogWithBrownButton(requireContext(), message).setSelectCallback {
            showEventList()
        }
    }

    override fun showErrorEnterMessage() {
        val message = "Не удалось подтвердить вход"
        MessageDialogWithBrownButton(requireContext(), message).setSelectCallback { }
    }

    override fun showEventList() {
        findNavController().navigate(
            R.id.recommendations_fragment, null, NavOptions.Builder()
                .setPopUpTo(R.id.main_navigation, true)
                .build()
        )
    }

    override fun showContent() {
        mBinding.apply {
            tvJoinMessage.isVisible = true
            llInfo.isVisible = true
            btnConfirm.isVisible = true
            btnDoNotConfirm.isVisible = true
        }
    }

    override fun hideContent() {
        mBinding.apply {
            tvJoinMessage.isVisible = false
            llInfo.isVisible = false
            btnConfirm.isVisible = false
            btnDoNotConfirm.isVisible = false
        }
    }


    private fun getTokenFromQrCode(str: String): String {
        val mIndex = StringBuilder(str).indexOf("=")
        return StringBuilder(str).substring(mIndex + 1)
    }

    override fun getFragmentBackgroundDrawable(): Drawable? {
        return AuthBackground.get(resources)
    }

    override fun layout() = R.layout.fragment_auth_website
}

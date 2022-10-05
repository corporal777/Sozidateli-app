package com.example.ui.qrscanner.auth

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isInvisible
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

    @InjectPresenter
    lateinit var mPresenter: AuthWebsitePresenter

    @Inject
    lateinit var presenterProvider: Provider<AuthWebsitePresenter>

    @ProvidePresenter
    fun providePresenter(): AuthWebsitePresenter = presenterProvider.get().apply {
        mArgs.let {
            this.mToken = it.qrCode
        }
    }


    override fun setEnterData(data: QrAuthResponse) {
        mBinding.apply {
            tvDevice.text = data.device
            tvIPAddress.text = data.ipAddress
            tvTime.text = longToTime(data.timeStamp)
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
        mBinding.clContent.isInvisible = false
    }

    override fun hideContent() {
        mBinding.clContent.isInvisible = true
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

package com.example.ui.qrscanner.auth

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.view.isInvisible
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
import com.example.ui.base.BaseFragment
import com.example.util.AuthBackground
import javax.inject.Inject
import javax.inject.Provider

class AuthWebsiteFragment : BaseFragment<FragmentAuthWebsiteBinding>(), BackgroundImageFragment,
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
            token = it.qrCode
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

    override fun getFragmentBackgroundDrawable(): Drawable? {
        return AuthBackground.get(resources)
    }

    override fun layout() = R.layout.fragment_auth_website
}

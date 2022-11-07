package com.example.ui.splash

import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.databinding.FragmentSplashBinding
import com.example.extensions.dp
import com.example.ui.base.BaseFragmentNew
import com.example.ui.views.CustomProgressBar
import javax.inject.Inject
import javax.inject.Provider


class SplashFragment : BaseFragmentNew<FragmentSplashBinding>(), SplashContract.View {


    @InjectPresenter
    lateinit var presenter: SplashPresenter

    @Inject
    lateinit var presenterProvider: Provider<SplashPresenter>

    @ProvidePresenter
    fun providePresenter(): SplashPresenter = presenterProvider.get()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.ivLogo.updatePadding(top = getHeightOfNavbar() - getHeightOfStatusBar())
    }

    private fun getHeightOfNavbar(): Int {
        val resources: Resources = requireContext().resources
        val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }

    private fun getHeightOfStatusBar(): Int {
        val resources: Resources = requireContext().resources
        val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }

    override fun layout() = R.layout.fragment_splash

}
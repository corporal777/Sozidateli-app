package com.example.ui.splash

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.extensions.dp
import com.example.interfaces.BackgroundImageFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.CustomProgressView
import com.example.util.AuthBackground
import kotlinx.android.synthetic.main.fragment_splash.*
import javax.inject.Inject
import javax.inject.Provider


class SplashFragment : BaseFragment(), BackgroundImageFragment, SplashContract.View {

    override val isLightStatus = false

    @InjectPresenter
    lateinit var presenter: SplashPresenter

    @Inject
    lateinit var presenterProvider: Provider<SplashPresenter>

    @ProvidePresenter
    fun providePresenter(): SplashPresenter = presenterProvider.get()

    override fun getFragmentBackgroundDrawable() = AuthBackground.get(resources)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       val  progressBar = CustomProgressView(context)
        progressBar.setSize(35.dp)
        progressBar.setProgressColor(ContextCompat.getColor(requireContext(), R.color.main_brown_color_new))
        progress_container.addView(progressBar, 0)
    }

    override fun layout() = R.layout.fragment_splash

}
package com.example.ui.auth.welcome

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import com.example.app.R
import com.example.app.databinding.FragmentWelcomeBinding
import com.example.interfaces.BackgroundImageFragment
import com.example.ui.base.BaseVBFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class WelcomeFragment : BaseVBFragment<FragmentWelcomeBinding>(), BackgroundImageFragment,
    WelcomeContract.View {


    @InjectPresenter
    lateinit var presenter: WelcomePresenter

    @Inject
    lateinit var presenterProvider: Provider<WelcomePresenter>

    @ProvidePresenter
    fun providePresenter(): WelcomePresenter = presenterProvider.get()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun setUserName(name: String) {
        mBinding.apply {
            tvGreeting.text = getString(R.string.welcome_greeting_message, name)
            progressView.showProgressBar()
        }
    }

    override fun getFragmentBackgroundDrawable(): Drawable? = null
    override val isLightStatus = false

    override fun layout() = R.layout.fragment_welcome
    override fun binding() = FragmentWelcomeBinding::class.java
}

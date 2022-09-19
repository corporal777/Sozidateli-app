package com.example.ui.about

import android.os.Bundle
import android.text.util.Linkify.WEB_URLS
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.BuildConfig
import com.example.R
import com.example.databinding.FragmentAboutBinding
import com.example.ui.base.BaseFragmentNew
import com.example.ui.views.toolbar.SimpleTitleToolbar
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import removeUrlUnderline
import javax.inject.Inject
import javax.inject.Provider

class AboutFragment : BaseFragmentNew<FragmentAboutBinding>(), AboutContract.View, SimpleTitleToolbar {


    @InjectPresenter
    lateinit var presenter: AboutPresenter

    @Inject
    lateinit var presenterProvider: Provider<AboutPresenter>

    @ProvidePresenter
    fun providePresenter(): AboutPresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarTitleAndIcon(getString(R.string.profile_about_app))
        mBinding.apply {
            tvAppVersion.text = getString(R.string.about_version).format(BuildConfig.VERSION_NAME)
            tvDeveloperSite.apply {
                BetterLinkMovementMethod.linkify(WEB_URLS, tvDeveloperSite)
                removeUrlUnderline(textColors.defaultColor)
            }
        }

    }

    override fun layout() = R.layout.fragment_about
}
package com.example.ui.about

import android.os.Bundle
import android.text.util.Linkify.WEB_URLS
import android.view.View
import android.view.ViewGroup
import com.example.BuildConfig
import com.example.R
import com.example.databinding.FragmentAboutBinding
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.toolbar.ToolbarContent
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import moxy.presenter.ProvidePresenterTag
import removeUrlUnderline
import javax.inject.Inject
import javax.inject.Provider

class AboutFragment : BaseFragment<FragmentAboutBinding>(true), AboutContract.View,
    ToolbarFragment {

    @InjectPresenter
    lateinit var presenter: AboutPresenter

    @Inject
    lateinit var presenterProvider: Provider<AboutPresenter>

    @ProvidePresenter
    fun providePresenter(): AboutPresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startPostponedEnterTransition()

        mBinding.apply {
            tvAppVersion.text = getString(R.string.about_version).format(BuildConfig.VERSION_NAME)
            tvDeveloperSite.apply {
                BetterLinkMovementMethod.linkify(WEB_URLS, tvDeveloperSite)
                removeUrlUnderline(textColors.defaultColor)
            }
        }

    }

    override fun layout() = R.layout.fragment_about

    override val title: CharSequence by lazy { getString(R.string.profile_about_app) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: (value: Int) -> Unit) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}
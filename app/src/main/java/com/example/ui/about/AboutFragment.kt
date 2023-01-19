package com.example.ui.about

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.util.Linkify.WEB_URLS
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.BuildConfig
import com.example.R
import com.example.databinding.FragmentAboutBinding
import com.example.interfaces.ToolbarFragmentNew
import com.example.ui.base.BaseFragmentNew
import com.example.ui.views.toolbar.ToolbarContent
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import removeUrlUnderline
import javax.inject.Inject
import javax.inject.Provider

class AboutFragment : BaseFragmentNew<FragmentAboutBinding>(true), AboutContract.View,
    ToolbarFragmentNew {


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

    override val title: CharSequence by lazy {
        getString(R.string.profile_about_app)
    }
    override val actionIconHidden: Boolean = true
    override val actionIcon: Drawable? = null

    override fun actionIconClick() {}
    override fun toolbarTitleClick() {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}
package com.example.ui.about

import android.os.Bundle
import android.text.util.Linkify.WEB_URLS
import android.view.View
import android.view.ViewGroup
import com.example.app.BuildConfig
import com.example.app.R
import com.example.app.databinding.FragmentAboutBinding
import com.example.extensions.removeUrlUnderline
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseToolbarFragment
import com.example.ui.base.BaseVBFragment
import com.example.ui.views.toolbar.ToolbarContent
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class AboutFragment : BaseToolbarFragment<FragmentAboutBinding>(), AboutContract.View {

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

    override fun animationType(): AnimType = AnimType.AXIS
    override fun layout() = R.layout.fragment_about
    override fun binding() = FragmentAboutBinding::class.java
    override val title: CharSequence by lazy { getString(R.string.profile_about_app) }
}
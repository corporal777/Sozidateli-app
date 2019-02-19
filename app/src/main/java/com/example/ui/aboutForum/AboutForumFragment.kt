package com.example.ui.aboutForum

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Event
import com.example.ui.base.BaseFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_about_forum.*
import javax.inject.Inject
import javax.inject.Provider

class AboutForumFragment : BaseFragment(), AboutForumContract.View {

    @InjectPresenter
    lateinit var presenter: AboutForumPresenter

    @Inject
    lateinit var presenterProvider: Provider<AboutForumPresenter>

    @ProvidePresenter
    fun providePresenter(): AboutForumPresenter = presenterProvider.get().apply {
        val data = AboutForumFragmentArgs.fromBundle(arguments!!)
        event = data.event
    }

    override fun setData(event: Event) {
        Picasso.get().load(event.logo).placeholder(R.drawable.ic_launcher_background).into(ivLogo)
        tvInfo.text = event.info
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_about_forum
}

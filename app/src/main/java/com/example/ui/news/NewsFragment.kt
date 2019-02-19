package com.example.ui.news

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.News
import com.example.ui.base.BaseFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_about_forum.*
import javax.inject.Inject
import javax.inject.Provider

class NewsFragment : BaseFragment(), NewsContract.View {

    @InjectPresenter
    lateinit var presenter: NewsPresenter

    @Inject
    lateinit var presenterProvider: Provider<NewsPresenter>

    @ProvidePresenter
    fun providePresenter(): NewsPresenter = presenterProvider.get().apply {
        val data = NewsFragmentArgs.fromBundle(arguments!!)
        news = data.news
    }

    override fun setData(news: News) {
        Picasso.get().load(news.image).placeholder(R.drawable.ic_launcher_background).into(ivLogo)
        tvInfo.text = news.text
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_news
}

package com.example.ui.news

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.News
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_news.*
import javax.inject.Inject
import javax.inject.Provider

class NewsFragment : BaseFragment(), NewsContract.View, ToolbarFragment {
    override val title: String
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

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
        Picasso.get().load(news.picture.let { if (it.isNullOrEmpty()) null else it }).placeholder(R.drawable.ic_launcher_background).into(ivLogo)
        news.text?.let {
            tvInfo.setHtml(it)
        }
    }

    override fun layout() = R.layout.fragment_news
}

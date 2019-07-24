package com.example.ui.newsList

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.adapters.SimplePagingRecyclerViewAdapter
import com.example.adapters.ViewHolder
import com.example.data.models.News
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_news_list.*
import kotlinx.android.synthetic.main.item_news.*
import setDateCheckYearText
import javax.inject.Inject
import javax.inject.Provider

class NewsListFragment : BaseFragment(), NewsListContract.View, ToolbarFragment {
    override val title: String
        get() = getString(R.string.about_event_news)

    @InjectPresenter
    lateinit var presenter: NewsListPresenter

    @Inject
    lateinit var presenterProvider: Provider<NewsListPresenter>

    @ProvidePresenter
    fun providePresenter(): NewsListPresenter = presenterProvider.get().apply {
        val data = NewsListFragmentArgs.fromBundle(arguments!!)
        event = data.event
    }

    private val adapter: SimplePagingRecyclerViewAdapter<News> by lazy {
        object : SimplePagingRecyclerViewAdapter<News>(
                { oldItem, newItem -> oldItem.id == newItem.id },
                { oldItem, newItem -> oldItem == newItem }
        ) {
            override fun getItemLayout(itemView: Int) = R.layout.item_news

            override fun onBindItem(viewHolder: ViewHolder, item: News?, position: Int) {
                item!!
                viewHolder.apply {
                    item.public_date?.let {
                        tvDate.setDateCheckYearText(it)
                    }
                    tvNewsTitle.text = item.title
                    tvNewsText.text = item.text

                    btnReadMore.setOnClickListener { presenter.onNewsClick(item) }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = this@NewsListFragment.adapter
            if (itemDecorationCount == 0) addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(context, VERTICAL))
        }
    }

    override fun setData(news: PagedList<News>) {
        adapter.submitList(news)
    }

    override fun showNews(news: News) {
        findNavController().navigate(NewsListFragmentDirections.actionNewsListFragmentToNewsFragment(news))
    }

    override fun layout() = R.layout.fragment_news_list
}

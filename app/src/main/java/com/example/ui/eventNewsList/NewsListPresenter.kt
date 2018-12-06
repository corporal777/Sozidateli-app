package com.example.ui.eventNewsList

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Event
import com.example.data.models.News
import com.example.repository.DummyRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.SimplePagination
import javax.inject.Inject

@InjectViewState
class NewsListPresenter
@Inject constructor(
        private val dummyRepository: DummyRepository
) : BasePresenter<NewsListContract.View>(), NewsListContract.Presenter {

    lateinit var event: Event

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        SimplePagination { limit, offset -> dummyRepository.loadNews(event.id, limit, offset) }
                .create()
                .subscribe({ viewState.apply { setData(it) } }, { it.printStackTrace() })
                .call(compositeDisposable)
    }


    override fun onNewsClick(news: News) = viewState.showNews(news)
}

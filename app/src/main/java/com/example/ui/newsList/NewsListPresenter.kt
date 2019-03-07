package com.example.ui.newsList

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Event
import com.example.data.models.News
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.SimplePagination
import javax.inject.Inject

@InjectViewState
class NewsListPresenter
@Inject constructor(
        private val eventRepository: EventRepository
) : BasePresenter<NewsListContract.View>(), NewsListContract.Presenter {

    lateinit var event: Event

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showLoadingDialog()
        SimplePagination { limit, offset -> eventRepository.getEventNewsList(event.event_id.toInt(), limit, offset) }
                .build()
                .subscribe({
                    viewState.apply { setData(it) } }, { it.printStackTrace() })
                .call(compositeDisposable)
    }


    override fun onNewsClick(news: News) = viewState.showNews(news)
}

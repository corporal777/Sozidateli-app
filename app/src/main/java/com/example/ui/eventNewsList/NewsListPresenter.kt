package com.example.ui.eventNewsList

import android.arch.paging.PagedList
import android.arch.paging.RxPagedListBuilder
import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Event
import com.example.data.models.News
import com.example.repository.DummyRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationDataSourceFactory
import io.reactivex.BackpressureStrategy
import javax.inject.Inject

@InjectViewState
class NewsListPresenter
@Inject constructor(
        private val dummyRepository: DummyRepository
) : BasePresenter<NewsListContract.View>(), NewsListContract.Presenter {

    lateinit var event: Event

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        val factory = PaginationDataSourceFactory { limit, offset -> dummyRepository.loadNews(event.id, limit, offset) }

        val config = PagedList.Config.Builder()
                .setInitialLoadSizeHint(20)
                .setPageSize(20)
                .setEnablePlaceholders(false)
                .build()

        RxPagedListBuilder(factory, config)
                .buildFlowable(BackpressureStrategy.LATEST)
                .subscribe({ viewState.apply { setData(it) } }, { it.printStackTrace() })
                .call(compositeDisposable)
    }


    override fun onNewsClick(news: News) = viewState.showNews(news)
}

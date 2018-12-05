package com.example.ui.recommendations

import android.arch.paging.PagedList
import android.arch.paging.RxPagedListBuilder
import android.view.View
import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Event
import com.example.repository.DummyRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationDataSourceFactory
import io.reactivex.BackpressureStrategy
import javax.inject.Inject

@InjectViewState
class RecommendationsPresenter
@Inject constructor(
        private val dummyRepository: DummyRepository
) : BasePresenter<RecommendationsContract.View>(), RecommendationsContract.Presenter {

    private var scrollPosition = 0
    private var scrollOffset = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        val factory = PaginationDataSourceFactory { limit, offset -> dummyRepository.loadRecommendations(limit, offset) }

        val config = PagedList.Config.Builder()
                .setInitialLoadSizeHint(20)
                .setPageSize(20)
                .setEnablePlaceholders(false)
                .build()

        RxPagedListBuilder(factory, config)
                .buildFlowable(BackpressureStrategy.LATEST)
                .subscribe({
                    viewState.apply {
                        setData(it)
                    }
                }, {
                    it.printStackTrace()
                })
                .call(compositeDisposable)
    }

    override fun attachView(view: RecommendationsContract.View?) {
        super.attachView(view)
        viewState.scrollToPositionWithOffset(scrollPosition, scrollOffset)
    }

    override fun onEventClick(event: Event, vararg sharedElements: Pair<View, String>) {
        viewState.showAboutEvent(event, *sharedElements)
    }

    override fun onGoToEventClick(event: Event) {

    }

    override fun onScrollChange(position: Int, offset: Int) {
        scrollPosition = position
        scrollOffset = offset
    }
}

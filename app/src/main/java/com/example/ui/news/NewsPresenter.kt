package com.example.ui.news

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.News
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class NewsPresenter
@Inject constructor(
        private val eventRepository: EventRepository
) : BasePresenter<NewsContract.View>(), NewsContract.Presenter {

    lateinit var news: News

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        eventRepository.getNewsById(news.event_id,news.id)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.setData(it)
                },{
                    it.printStackTrace()
                }).call(compositeDisposable)
    }
}

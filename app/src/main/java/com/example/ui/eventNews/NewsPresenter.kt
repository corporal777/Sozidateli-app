package com.example.ui.eventNews

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.News
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class NewsPresenter
@Inject constructor(
) : BasePresenter<NewsContract.View>(), NewsContract.Presenter {

    lateinit var news: News

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setData(news)
    }
}

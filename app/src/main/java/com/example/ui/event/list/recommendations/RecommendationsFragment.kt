package com.example.ui.event.list.recommendations

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.ui.event.list.EventListFragment
import javax.inject.Inject
import javax.inject.Provider

class RecommendationsFragment : EventListFragment<RecommendationsPresenter>(), RecommendationsContract.View {

    @InjectPresenter(type = PresenterType.WEAK, tag = "RecommendationsPresenter")
    override lateinit var presenter: RecommendationsPresenter

    @Inject
    lateinit var presenterProvider: Provider<RecommendationsPresenter>

    @ProvidePresenter(type = PresenterType.WEAK, tag = "RecommendationsPresenter")
    fun providePresenter(): RecommendationsPresenter = presenterProvider.get()
}

package com.example.ui.event.list.favorite

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.ui.event.list.EventListFragment
import javax.inject.Inject
import javax.inject.Provider

class FavoriteEventsFragment : EventListFragment<FavoriteEventsPresenter>(), FavoriteEventsContract.View {

    @InjectPresenter
    override lateinit var presenter: FavoriteEventsPresenter

    @Inject
    lateinit var presenterProvider: Provider<FavoriteEventsPresenter>

    @ProvidePresenter
    fun providePresenter(): FavoriteEventsPresenter = presenterProvider.get()
}

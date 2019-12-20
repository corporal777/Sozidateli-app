package com.example.ui.search.user

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class SearchUserFragment : AbstractSearchUserFragment<SearchUserPresenter>() {

    @InjectPresenter
    override lateinit var presenter: SearchUserPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchUserPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchUserPresenter = presenterProvider.get()
}
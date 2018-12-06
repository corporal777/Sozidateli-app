package com.example.ui.aboutForum

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Event
import com.example.repository.DummyRepository
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class AboutForumPresenter
@Inject constructor(
        private val dummyRepository: DummyRepository
) : BasePresenter<AboutForumContract.View>(), AboutForumContract.Presenter {

    lateinit var event: Event

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setData(event)
    }
}

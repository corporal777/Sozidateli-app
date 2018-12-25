package com.example.ui.views.chatView

import com.arellomobile.mvp.InjectViewState
import com.example.repository.DummyRepository
import com.example.ui.base.BasePresenter
import com.example.ui.speakers.SpeakersContract
import javax.inject.Inject

@InjectViewState
class ChatViewPresenter @Inject constructor(
        private val dummyRepository: DummyRepository
) : BasePresenter<ChatViewContract.View>(), ChatViewContract.Presenter {


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setChatCount(3)
    }
}

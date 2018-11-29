package com.example.ui.chatList

import com.arellomobile.mvp.InjectViewState
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class ChatListPresenter
@Inject constructor(
) : BasePresenter<ChatListContract.View>(), ChatListContract.Presenter{
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState?.apply {
            setData()
        }
    }
}

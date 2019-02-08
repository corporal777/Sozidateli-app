package com.example.ui.chatList

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.UserChat
import com.example.repository.DummyRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.SimplePagination
import javax.inject.Inject

@InjectViewState
class ChatListPresenter
@Inject constructor(
        private val dummyRepository: DummyRepository
) : BasePresenter<ChatListContract.View>(), ChatListContract.Presenter {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        SimplePagination { limit, offset -> dummyRepository.loadUserChats(limit, offset) }
                .create()
                .subscribe({ viewState.apply { setData(it) } }, { it.printStackTrace() })
                .call(compositeDisposable)
    }

    override fun onChatClick(userChat: UserChat) = viewState.openChat(userChat.id)

    override fun onMenuAddChatClick() = viewState.openSearchContact()
}

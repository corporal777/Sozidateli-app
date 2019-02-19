package com.example.ui.chatList

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.UserChat
import com.example.repository.ChatRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.SimplePagination
import javax.inject.Inject

@InjectViewState
class ChatListPresenter
@Inject constructor(
        private val chatRepository: ChatRepository
) : BasePresenter<ChatListContract.View>(), ChatListContract.Presenter {

    private val pagination = SimplePagination { limit, offset -> chatRepository.loadChatList(mapOf(), limit, offset) }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        pagination.build()
                .subscribe({ viewState.apply { setData(it) } }, { it.printStackTrace() })
                .call(compositeDisposable)
    }

    override fun attachView(view: ChatListContract.View?) {
        super.attachView(view)
        pagination.invalidate()
    }

    override fun onChatClick(userChat: UserChat) = viewState.openChat(userChat.id, userChat.user.user_id.toString(), userChat.user.fullName)

    override fun onMenuAddChatClick() = viewState.openSearchContact()
}

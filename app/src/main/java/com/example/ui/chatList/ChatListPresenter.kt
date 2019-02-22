package com.example.ui.chatList

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.UserChat
import com.example.repository.ChatRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.SimplePagination
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class ChatListPresenter
@Inject constructor(
        private val chatRepository: ChatRepository,
        private val appData: AppData
) : BasePresenter<ChatListContract.View>(), ChatListContract.Presenter {

    private val pagination = SimplePagination { limit, offset -> chatRepository.loadChatList(mapOf(), limit, offset) }

    private var firstLaunch = true
    private var lastChatUnreadCount: Int? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        pagination.build()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.apply {
                        setData(it)
                        showEmptyView(it.size == 0)
                    }
                }, { it.printStackTrace() })
                .call(compositeDisposable)

        appData.onChatUnreadMessageCountChange
                .performOnBackgroundOutOnMain()
                .subscribe({
                    val lastCount = lastChatUnreadCount
                    if (lastCount != null && lastCount < it) pagination.invalidate()
                    lastChatUnreadCount = it
                }, {})
                .call(compositeDisposable)
    }

    override fun attachView(view: ChatListContract.View?) {
        super.attachView(view)
        if (firstLaunch) firstLaunch = false
        else pagination.invalidate()
    }

    override fun onChatClick(userChat: UserChat) = viewState.openChat(userChat.id, userChat.user.user_id.toString(), userChat.user.fullName)

    override fun onMenuAddChatClick() = viewState.openSearchContact()
}

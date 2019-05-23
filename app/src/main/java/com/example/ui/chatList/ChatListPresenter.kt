package com.example.ui.chatList

import android.util.SparseArray
import android.util.SparseIntArray
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.UserChat
import com.example.holders.UserChatItem
import com.example.repository.ChatRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationDataSourceFactory
import io.reactivex.BackpressureStrategy
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class ChatListPresenter
@Inject constructor(
        private val chatRepository: ChatRepository
) : BasePresenter<ChatListContract.View>(), ChatListContract.Presenter {

    private val pagination = PaginationDataSourceFactory { limit, offset -> chatRepository.loadChatList(mapOf(), limit, offset) }.map { chat ->
        UserChatItem(
                chat,
                { onChatClick(it) },
                { onChatOnScreen(it) },
                { onChatGoneFromScreen(it) }
        ).apply {
            unreadMessageCount = chatUnreadMessageCount.get(chat.id)
        }
    }

    private val chatUnreadMessageSubscriptions = SparseArray<Disposable>()
    private val chatUnreadMessageCount = SparseIntArray()

    private var firstLaunch = true
    private var lastChatUnreadCount: Int? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        val config = PagedList.Config.Builder()
                .setInitialLoadSizeHint(20)
                .setPageSize(20)
                .setEnablePlaceholders(false)
                .build()

        RxPagedListBuilder(pagination, config)
                .buildFlowable(BackpressureStrategy.LATEST)
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
                    if (lastCount != null && lastCount < it) pagination.source?.invalidate()
                    lastChatUnreadCount = it
                }, {})
                .call(compositeDisposable)
    }

    override fun attachView(view: ChatListContract.View?) {
        super.attachView(view)
        if (firstLaunch) firstLaunch = false
        else pagination.source?.invalidate()
    }

    override fun onChatClick(userChat: UserChat) = viewState.openChat(userChat.id, userChat.user.user_id.toString(), userChat.user.fullName)

    override fun onMenuAddChatClick() = viewState.openSearchContact()

    override fun onChatOnScreen(chat: UserChatItem) {
        val chatId = chat.userChat.id
        val oldSubscription = chatUnreadMessageSubscriptions[chatId]
        if (oldSubscription != null && !oldSubscription.isDisposed) {
            oldSubscription.dispose()
        }

//        val changeAccept = createChatMessageCountConsumer(chat)
//        val subscription = chatRepository.subscribeChatUnreadMessageCount(chatId.toString())
//                .performOnBackgroundOutOnMain()
//                .subscribe(changeAccept, Consumer { changeAccept.accept(0) })
//        chatUnreadMessageSubscriptions.put(chatId, subscription)
//        compositeDisposable.add(subscription)
    }

    override fun onChatGoneFromScreen(chat: UserChatItem) {
       // chatUnreadMessageSubscriptions[chat.userChat.event_id]?.dispose()
    }

    private fun createChatMessageCountConsumer(chat: UserChatItem): Consumer<Int> {
        return Consumer {
            chatUnreadMessageCount.put(chat.userChat.id, it)
            if (chat.unreadMessageCount != it) chat.unreadMessageCount = it
        }
    }
}

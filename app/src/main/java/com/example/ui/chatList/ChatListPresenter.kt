package com.example.ui.chatList

import android.util.SparseArray
import android.util.SparseIntArray
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.UserChat
import com.example.events.OnSocketConnectEvent
import com.example.holders.UserChatItem
import com.example.repository.ChatRepository
import com.example.ui.base.BasePresenter
import com.example.ui.contactsSearch.ContactsSearchFragment.Companion.SEARCH_ACTION_FILTER
import com.example.ui.contactsSearch.ContactsSearchFragment.Companion.SEARCH_ACTION_INPUT
import com.example.ui.contactsSearch.ContactsSearchFragment.Companion.SEARCH_ACTION_NONE
import com.example.util.pagination.PaginationDataSourceFactory
import io.reactivex.BackpressureStrategy
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.plusAssign
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import performOnBackgroundOutOnMain
import ru.houseofapps.chat.HAChat
import ru.houseofapps.chat.models.RoomUnreadMessageCount
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class ChatListPresenter
@Inject constructor(
        private val chatRepository: ChatRepository,
        private val haChat: HAChat
) : BasePresenter<ChatListContract.View>(), ChatListContract.Presenter {

    private val chatsPagination = PaginationDataSourceFactory { limit, offset ->
        chatRepository.loadChatList(mapOf(), limit, offset)
    }.map { chat ->
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
        EventBus.getDefault().register(this)

        val config = PagedList.Config.Builder()
                .setInitialLoadSizeHint(20)
                .setPageSize(20)
                .setEnablePlaceholders(false)
                .build()

        compositeDisposable += RxPagedListBuilder(chatsPagination, config)
                .buildFlowable(BackpressureStrategy.LATEST)
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.apply {
                        setChats(it)
                        showEmptyView(it.size == 0)
                    }
                }, { it.printStackTrace() })

        compositeDisposable += appData.onChatUnreadMessageCountChange
                .performOnBackgroundOutOnMain()
                .subscribe({
                    val lastCount = lastChatUnreadCount
                    if (lastCount != null && lastCount < it) chatsPagination.source?.invalidate()
                    lastChatUnreadCount = it
                }, {})
    }

    override fun attachView(view: ChatListContract.View?) {
        super.attachView(view)
        if (firstLaunch) firstLaunch = false
        else chatsPagination.source?.invalidate()
    }

    override fun onChatClick(userChat: UserChat) = viewState.openChat(userChat.id, userChat.user.user_id.toString(), userChat.user.fullName)

    override fun onMenuAddChatClick() = viewState.openSearchContact(SEARCH_ACTION_NONE)

    override fun onEmptyChatsButtonAddChatClick() = viewState.openSearchContact(SEARCH_ACTION_NONE)

    override fun onInputClick() = viewState.openSearchContact(SEARCH_ACTION_INPUT)

    override fun onInputFilterClick() = viewState.openSearchContact(SEARCH_ACTION_FILTER)

    override fun onChatOnScreen(chat: UserChatItem) {
        val chatId = chat.userChat.id
        val oldSubscription = chatUnreadMessageSubscriptions[chatId]
        if (oldSubscription != null && !oldSubscription.isDisposed) {
            oldSubscription.dispose()
        }

        val changeAccept = createChatMessageCountConsumer(chat)
        val subscription = haChat.subscribeToUnreadMessageCountForRoom(chat.id.toString())
                .performOnBackgroundOutOnMain()
                .subscribe(changeAccept, Consumer { changeAccept.accept(RoomUnreadMessageCount(chatId.toString(), 0)) })

        chatUnreadMessageSubscriptions.put(chatId, subscription)
        compositeDisposable.add(subscription)
    }

    override fun onChatGoneFromScreen(chat: UserChatItem) {
        // chatUnreadMessageSubscriptions[chat.userChat.event_id]?.dispose()
    }

    private fun createChatMessageCountConsumer(chat: UserChatItem): Consumer<RoomUnreadMessageCount> {
        return Consumer {
            if (it.room == chat.userChat.id.toString()) {
                chatUnreadMessageCount.put(chat.userChat.id, it.count)
                if (chat.unreadMessageCount != it.count) chat.unreadMessageCount = it.count
            }
        }
    }

    @Subscribe
    fun onSocketConnect(event: OnSocketConnectEvent) {
        chatsPagination.source?.invalidate()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}

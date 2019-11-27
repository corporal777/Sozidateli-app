package com.example.ui.chatList.contacts

import android.util.SparseArray
import android.util.SparseIntArray
import androidx.core.util.set
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.ChatListDataItem
import com.example.data.models.UserChat
import com.example.data.models.user.User
import com.example.events.OnSocketConnectEvent
import com.example.extensions.buildList
import com.example.repository.ChatRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationDataSourceFactory
import com.example.util.pagination.PaginationResponse
import com.example.util.pagination.applyErrorHandler
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.plusAssign
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import performOnBackgroundOutOnMain
import ru.houseofapps.chat.HAChat
import ru.houseofapps.chat.models.RoomUnreadMessageCount
import javax.inject.Inject

@InjectViewState
class ChatListPresenter
@Inject constructor(
        private val chatRepository: ChatRepository,
        private val haChat: HAChat,
        private val appData: AppData
) : BasePresenter<ChatListContract.View>(), ChatListContract.Presenter {

    private val chatsPagination = PaginationDataSourceFactory { limit, offset ->
        chatRepository.loadChatList(limit, offset).map { response ->
            val items = response.response.chats.map { ChatListDataItem.Chat(it) }
                    .plus(response.response.favorites.map { ChatListDataItem.User(it) })
            PaginationResponse(response.response_detail?.total, items)
        }
    }
            .applyErrorHandler { viewState.showRequestErrorMessage() }
            .buildList(enablePlaceholders = true)

    private val chatUnreadMessageSubscriptions = SparseArray<Disposable>()
    private val chatUnreadMessageCounters = SparseIntArray()
    private val chatUnreadMessageConsumer = Consumer<RoomUnreadMessageCount> {
        it.room.toIntOrNull()?.also { room -> chatUnreadMessageCounters[room] = it.count }
        viewState.setChatUnreadMessageCount(it.room, it.count)
    }

    private var firstLaunch = true
    private var lastChatUnreadCount: Int? = null
    private var isScrolledToTop = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        EventBus.getDefault().register(this)

        viewState.setChatsData(List(20) { null }, emptyList())
        compositeDisposable += appData.chatMessageCountSubject
                .performOnBackgroundOutOnMain()
                .subscribe({
                    val lastCount = lastChatUnreadCount
                    if (lastCount != null && lastCount < it) chatsPagination.invalidate()
                    lastChatUnreadCount = it
                }, {})

        compositeDisposable += haChat.subscribeToUnreadMessageCount()
                .performOnBackgroundOutOnMain()
                .subscribe(chatUnreadMessageConsumer, Consumer {})

        compositeDisposable += Observable.create(chatsPagination)
                .subscribe({
                    viewState.apply {
                        checkScrollPosition()
                        dispatchChatsListUpdate(it)
                        if (isScrolledToTop) scrollToTopPosition()
                    }
                }, { it.printStackTrace() })
    }

    override fun attachView(view: ChatListContract.View?) {
        super.attachView(view)
        if (firstLaunch) firstLaunch = false
        else chatsPagination.invalidate()
    }

    private fun dispatchChatsListUpdate(data: List<ChatListDataItem?>) {
        val chats = mutableListOf<UserChat?>()
        val favorites = mutableListOf<User>()

        data.forEach {
            when (it) {
                is ChatListDataItem.Chat -> chats.add(it.userChat.apply {
                    unreadMessageCount = chatUnreadMessageCounters[id, 0]
                })
                is ChatListDataItem.User -> favorites.add(it.user)
                null -> chats.add(null)
            }
        }

        viewState.setChatsData(chats, favorites)
    }

    override fun onChatClick(userChat: UserChat) = viewState.openChat(userChat.id, userChat.user.fullName)

    override fun onUserClick(uid: Int, userName: String) {
        compositeDisposable += chatRepository.startChat(uid.toString())
                .performOnBackgroundOutOnMain()
                .subscribe({ viewState.openChat(it.chat_id, userName) }, {})
    }

    override fun onFabAddChatClick() = viewState.openSearch()

    override fun onEmptyChatsButtonAddChatClick() = viewState.openSearch()

    override fun onItemTake(position: Int) {
        chatsPagination.onItemTake(position)
    }

    override fun onChatOnScreen(chatId: Int) {
        val oldSubscription = chatUnreadMessageSubscriptions[chatId]
        if (oldSubscription != null && !oldSubscription.isDisposed) {
            oldSubscription.dispose()
        }

        val subscription = haChat.getUnreadMessageCount(chatId.toString())
                .performOnBackgroundOutOnMain()
                .subscribe(chatUnreadMessageConsumer, Consumer {
                    chatUnreadMessageConsumer.accept(RoomUnreadMessageCount(chatId.toString(), 0))
                })

        chatUnreadMessageSubscriptions.put(chatId, subscription)
        compositeDisposable.add(subscription)
    }

    override fun onChatGoneFromScreen(chatId: Int) {
        chatUnreadMessageSubscriptions[chatId]?.dispose()
    }

    override fun onChatScrollChange(isTopPosition: Boolean) {
        isScrolledToTop = isTopPosition
    }

    @Subscribe
    fun onSocketConnect(event: OnSocketConnectEvent) {
        chatsPagination.invalidate()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onRefreshRequest() {
        chatsPagination.invalidate()
    }
}

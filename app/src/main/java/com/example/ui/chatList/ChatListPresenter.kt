package com.example.ui.chatList

import android.util.SparseArray
import android.util.SparseIntArray
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.ChatListDataItem
import com.example.data.models.Speaker
import com.example.data.models.UserChat
import com.example.events.OnSocketConnectEvent
import com.example.extensions.buildList
import com.example.holders.UserChatItem
import com.example.repository.ChatRepository
import com.example.ui.base.BasePresenter
import com.example.ui.contactsSearch.ContactsSearchFragment.Companion.SEARCH_ACTION_FILTER
import com.example.ui.contactsSearch.ContactsSearchFragment.Companion.SEARCH_ACTION_INPUT
import com.example.ui.contactsSearch.ContactsSearchFragment.Companion.SEARCH_ACTION_NONE
import com.example.util.pagination.PaginationDataSourceFactory
import com.example.util.pagination.PaginationList
import com.example.util.pagination.PaginationResponse
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.plusAssign
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import performOnBackgroundOutOnMain
import ru.houseofapps.chat.HAChat
import ru.houseofapps.chat.models.RoomUnreadMessageCount
import timber.log.Timber
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class ChatListPresenter
@Inject constructor(
        private val chatRepository: ChatRepository,
        private val haChat: HAChat,
        private val appData:  AppData
) : BasePresenter<ChatListContract.View>(), ChatListContract.Presenter {

    private val chatsPagination = PaginationDataSourceFactory { limit, offset ->
        chatRepository.loadChatList(limit, offset).map { response ->
            val items = response.response.chats.map { ChatListDataItem.Chat(it) }
                    .plus(response.response.favorites.map { ChatListDataItem.User(it) })
            PaginationResponse(response.response_detail?.total, items)
        }
    }.buildList()

    private val invitesPagination = PaginationDataSourceFactory { limit, offset ->
        chatRepository.loadInvitesList(limit, offset).map { response ->
            PaginationResponse(response.totalCount, response.data.map { ChatListDataItem.Invite(it) })
        }
    }.buildList()

    private val bansPagination = PaginationDataSourceFactory { limit, offset ->
        chatRepository.loadChatList(limit, offset).map { response ->
            val items = response.response.chats.map { ChatListDataItem.Chat(it) }
                    .plus(response.response.favorites.map { ChatListDataItem.User(it) })
            PaginationResponse(response.response_detail?.total, items)
        }
    }.buildList()

    private val chatUnreadMessageSubscriptions = SparseArray<Disposable>()
    private val chatUnreadMessageCount = SparseIntArray()

    private var firstLaunch = true
    private var lastChatUnreadCount: Int? = null

    private var listMode: Int = -1
    private var currentPagination: PaginationList<*>? = null
    private val paginationDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        EventBus.getDefault().register(this)
        onShowChatListClick()

        compositeDisposable += appData.chatMessageCountSubject
                .performOnBackgroundOutOnMain()
                .subscribe({
                    val lastCount = lastChatUnreadCount
                    if (lastCount != null && lastCount < it) currentPagination?.invalidate()
                    lastChatUnreadCount = it
                }, {})
    }

    override fun attachView(view: ChatListContract.View?) {
        super.attachView(view)
        if (firstLaunch) firstLaunch = false
        else currentPagination?.invalidate()
    }

    override fun onShowChatListClick() {
        viewState.selectChats()
        changeListMode(SCREEN_MODE_CHATS)
    }

    override fun onShowInvitesClick() {
        viewState.selectInvites()
        changeListMode(SCREEN_MODE_INVITES)
    }

    override fun onShowBanesClick() = changeListMode(SCREEN_MODE_BANS)

    private fun changeListMode(mode: Int) {
        if (listMode != mode) {
            listMode = mode
            viewState.clearData()
            paginationDisposable.clear()
            listMode = mode
            subscribeToPagination()
        }
    }

    private fun subscribeToPagination() {
        val mode = listMode
        val pagination = when (mode) {
            SCREEN_MODE_CHATS -> chatsPagination
            SCREEN_MODE_INVITES -> invitesPagination
            SCREEN_MODE_BANS -> bansPagination
            else -> throw IllegalArgumentException("Wrong list mode $mode")
        }

        currentPagination = pagination

        paginationDisposable += Observable.create(pagination)
                .doOnDispose { Timber.tag("PAGINATION_T").d("DISPOSE") }
                .doOnError { Timber.tag("PAGINATION_T").d("ERR ${it.message}") }
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.apply {
                        dispatchListUpdate(it, mode)
                        showEmptyView(it.isEmpty())
                    }
                }, { it.printStackTrace() })
    }

    private fun dispatchListUpdate(data: List<ChatListDataItem>, mode: Int) {
        when (mode) {
            SCREEN_MODE_CHATS -> dispatchChatsListUpdate(data)
            SCREEN_MODE_INVITES -> dispatchInvitesListUpdate(data)
            SCREEN_MODE_BANS -> bansPagination
            else -> throw IllegalArgumentException("Wrong list mode $mode")
        }
    }

    private fun dispatchChatsListUpdate(data: List<ChatListDataItem>) {
        val chats = mutableListOf<UserChat>()
        val favorites = mutableListOf<Speaker>()

        data.forEach {
            when (it) {
                is ChatListDataItem.Chat -> chats.add(it.userChat)
                is ChatListDataItem.User -> favorites.add(it.user)
            }
        }

        viewState.setChatsData(chats, favorites)
    }

    private fun dispatchInvitesListUpdate(data: List<ChatListDataItem>) {
        val invites = mutableListOf<UserChat>()

        data.forEach { if (it is ChatListDataItem.Invite) invites.add(it.userChat) }
        viewState.setInvitesData(invites)
    }

    override fun onChatClick(userChat: UserChat) = viewState.openChat(userChat.id, userChat.user.user_id.toString(), userChat.user.fullName)

    override fun onMenuAddChatClick() = viewState.openSearchContact(SEARCH_ACTION_NONE)

    override fun onEmptyChatsButtonAddChatClick() = viewState.openSearchContact(SEARCH_ACTION_NONE)

    override fun onInputClick() = viewState.openSearchContact(SEARCH_ACTION_INPUT)

    override fun onInputFilterClick() = viewState.openSearchContact(SEARCH_ACTION_FILTER)

    override fun onItemTake(position: Int) {
        chatsPagination.onItemTake(position)
    }

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
//        chatsPagination.source?.invalidate()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    companion object {
        private const val SCREEN_MODE_CHATS = 0
        private const val SCREEN_MODE_INVITES = 1
        private const val SCREEN_MODE_BANS = 2
    }
}

package com.example.ui.chatList.contacts

import android.util.SparseArray
import android.util.SparseIntArray
import androidx.core.util.set
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.CreateChatBody
import com.example.data.models.*
import com.example.data.models.ChatModel.Companion.CHAT_BINDS
import com.example.data.models.ChatModel.Companion.CHAT_INVITED_USER_STATUS
import com.example.data.models.ChatModel.Companion.CHAT_LIMIT
import com.example.data.models.ChatModel.Companion.CHAT_OFFSET
import com.example.data.models.ChatModel.Companion.CHAT_SORT
import com.example.data.models.user.User
import com.example.data.socket.SocketIOManager
import com.example.events.OnSocketConnectEvent
import com.example.extensions.buildList
import com.example.repository.ChatRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.search.user.AbstractSearchUserPresenter
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
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class ChatListPresenter
@Inject constructor(
        private val chatRepository: ChatRepository,
        private val appData: AppData,
        private val userRepository: UserRepository,
        private val socket: SocketIOManager
) : BasePresenter<ChatListContract.View>(), ChatListContract.Presenter {

    private val chatsPagination = PaginationDataSourceFactory { limit, offset ->
        chatRepository.getChats(
                mapOf(CHAT_SORT to "desc", CHAT_LIMIT to limit, CHAT_OFFSET to offset,
                        CHAT_BINDS to "users,event,bans,last-message"/*last-unread-message,*/, CHAT_INVITED_USER_STATUS to "accepted")
        ).map { response ->
            val items = response.data.map { ChatListDataItem.Chat(UserChat(it.id, it.binds?.users?.first { us -> us.id != appData.getId() }!!,
            it.createdDate?: "", it.binds.lastMessage?.message, it.binds.lastMessage?.createdDate,
            if (it.binds.lastMessage?.file == null) Message.MessageType.TEXT else Message.MessageType.IMAGE,
                    it.binds.lastMessage?.acknowledge?.get(0)?.user, null, it.binds.lastMessage?.id.toString(),
                    false, it.isInInvites(appData.getId()), it.isWaitForAcceptInvites(), false, it.isBannedByYou(appData.getId()), it.isEventChat(),
                    it.binds.event?.id.toString(), 0)) }
                    //.plus(response.response.favorites.map { ChatListDataItem.User(it) })
            PaginationResponse(response.totalCount, items)
        }
        /*chatRepository.loadChatList(limit, offset).map { response ->
            val items = response.response.chats.map { ChatListDataItem.Chat(it) }
                    .plus(response.response.favorites.map { ChatListDataItem.User(it) })
            PaginationResponse(response.response_detail?.total, items)
        }*/
    }
            .applyErrorHandler { viewState.showRequestErrorMessage() }
            .buildList(enablePlaceholders = true)

    private val chatUnreadMessageSubscriptions = SparseArray<Disposable>()
    private val chatUnreadMessageCounters = SparseIntArray()
    private val chatUnreadMessageConsumer = Consumer<RoomUnreadMessageCount> {
        it.chatId.toIntOrNull()?.also { room -> chatUnreadMessageCounters[room] = it.count }
        viewState.setChatUnreadMessageCount(it.chatId, it.count)
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
        //TODO need to finish
        /*compositeDisposable += haChat.subscribeToUnreadMessageCount()
                .performOnBackgroundOutOnMain()
                .subscribe(chatUnreadMessageConsumer, Consumer {})*/

        compositeDisposable += Observable.create(chatsPagination)
                .subscribe({
                    viewState.apply {
                        checkScrollPosition()
                        dispatchChatsListUpdate(it)
                        if (isScrolledToTop) scrollToTopPosition()
                    }
                }, {
                    it.printStackTrace()
                })
    }

    override fun attachView(view: ChatListContract.View?) {
        super.attachView(view)
        if (firstLaunch) firstLaunch = false
        else chatsPagination.invalidate()
    }

    private fun dispatchChatsListUpdate(data: List<ChatListDataItem?>) {
        val chats = mutableListOf<UserChat?>()
        val favorites = mutableListOf</*User*/UserDetail>()

        data.forEach {
            when (it) {
                is ChatListDataItem.Chat -> chats.add(it.userChat.apply {
                    unreadMessageCount = chatUnreadMessageCounters[id, 0]
                })
                is ChatListDataItem.User -> favorites.add(it.user)
                null -> chats.add(null)
            }
        }

        compositeDisposable += userRepository.getUsersWithoutPagination(
                mutableMapOf<String, Any>().apply {
                    put(UserDetail.USER_LIMIT, 50)
                    put(UserDetail.USER_BINDS, "userFavorite,chat-room-with-me")
                }
        ).performOnBackgroundOutOnMain()
                .subscribeSimple(
                        onSuccess = {
                            it.filter { userDetail -> userDetail?.binds?.userFavorite != null }.forEach { user ->
                                if (user != null) favorites.add(user)
                            }
                            viewState.setChatsData(chats, favorites)
                        },
                        onError = {
                            viewState.setChatsData(chats, favorites)
                        }
                )
        /*compositeDisposable += userRepository.getUsersFavoritesWithoutPagination(
                mutableMapOf<String, Any>().apply {
                    put(UsersFavoriteModel.USERS_FAVORITE_LIMIT, 100)
                    put(UsersFavoriteModel.USERS_FAVORITE_TYPE, UsersFavoriteModel.USERS_TYPE)
                    put(UsersFavoriteModel.USERS_FAVORITE_LOAD_MODEL, true)
                    put(UsersFavoriteModel.USERS_FAVORITE_USER, appData.getId())
                }
        ).performOnBackgroundOutOnMain()
                .subscribeSimple(
                        onSuccess = {
                            it.forEach { user ->
                                if (user != null) favorites.add(user)
                            }
                            viewState.setChatsData(chats, favorites)
                        },
                        onError = {
                            viewState.setChatsData(chats, favorites)
                        }
                )*/


        //viewState.setChatsData(chats, favorites)
    }

    override fun onChatClick(userChat: UserChat) = viewState.openChat(userChat.id, userChat.user.fullName)

    override fun onUserClick(uid: Int, userName: String, chatRoomWithMe: ChatRoomWithMeModel?) {
        if (chatRoomWithMe == null) {
            compositeDisposable += chatRepository.createChat(CreateChatBody(uid))
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribeSimple {
                        viewState.openChat(it.id, userName)
                    }
        } else {
            viewState.openChat(chatRoomWithMe.id, userName)
        }

        //TODO finish this
        /*compositeDisposable += chatRepository.startChat(uid.toString())
                .performOnBackgroundOutOnMain()
                .subscribe({ viewState.openChat(it.chat_id, userName) }, {})*/
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

        val subscription = socket.subscribeToTotalMessagesCount(chatId.toString())
                .performOnBackgroundOutOnMain()
                .subscribe(chatUnreadMessageConsumer, Consumer {
                    chatUnreadMessageConsumer.accept(RoomUnreadMessageCount(chatId.toString(), 0))
                })

        chatUnreadMessageSubscriptions[chatId] = subscription
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

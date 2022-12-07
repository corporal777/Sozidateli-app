package com.example.ui.chatList.contacts

import android.util.Log
import android.util.SparseArray
import android.util.SparseIntArray
import androidx.core.util.set
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.CreateChatBody
import com.example.data.models.*
import com.example.data.models.ChatModel.Companion.CHAT_BINDS
import com.example.data.models.ChatModel.Companion.CHAT_LIMIT
import com.example.data.models.ChatModel.Companion.CHAT_OFFSET
import com.example.data.models.ChatModel.Companion.CHAT_SHOW_EVENTS
import com.example.data.models.ChatModel.Companion.CHAT_SORT
import com.example.data.models.ChatModel.Companion.CHAT_USER
import com.example.data.models.ChatModel.Companion.CHAT_USER_STATUS
import com.example.data.socket.SocketIOManager
import com.example.events.OnSocketConnectEvent
import com.example.extensions.buildList
import com.example.repository.ChatRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.chatList.contacts.items.UserChatData
import com.example.util.CHAT_SERVICE_MESSAGE_ACCEPT
import com.example.util.pagination.PaginationResponse
import com.example.util.pagination.observable.PaginationDataSourceFactory
import com.example.util.pagination.observable.applyErrorHandler
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.plusAssign
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class ChatListPresenter
@Inject constructor(
    private val chatRepository: ChatRepository,
    private val appData: AppData,
    private val userRepository: UserRepository,
    private val socket: SocketIOManager
) : BasePresenter<ChatListContract.View>(appData), ChatListContract.Presenter {

    private val chatsPagination = PaginationDataSourceFactory { limit, offset ->
        chatRepository.getChats(
            mapOf(
                CHAT_SORT to "desc",
                CHAT_LIMIT to limit,
                CHAT_OFFSET to offset,
                CHAT_BINDS to "users,event,bans,last-message",
                CHAT_USER_STATUS to "accepted",
                CHAT_USER to appData.getId(),
                CHAT_SHOW_EVENTS to true
            )
        ).map { response ->
            val items = prepareListOfChats(response.data)
            PaginationResponse(response.totalCount, items)
        }
    }.applyErrorHandler { viewState.showRequestErrorMessage() }.buildList(enablePlaceholders = true)


    private var firstLaunch = true
    private var lastChatUnreadCount: Int? = null
    private var isScrolledToTop = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        EventBus.getDefault().register(this)
        viewState.setChatsData(List(20) { null }, emptyList())
        subscribeChatSocketMessages()
        compositeDisposable += Observable.create(chatsPagination)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                },
                onNext = {
                    dispatchChatsListUpdate(it)
                })
    }

    override fun attachView(view: ChatListContract.View?) {
        super.attachView(view)
        if (firstLaunch) firstLaunch = false
        else chatsPagination.invalidate()
    }

    private fun dispatchChatsListUpdate(data: List<UserChat?>) {
        compositeDisposable += userRepository.getUsersWithoutPagination(
            mapOf(
                UserDetail.USER_LIMIT to 50,
                UserDetail.USER_BINDS to "userFavorite,chat-room-with-me"
            )
        ).performOnBackgroundOutOnMain()
            .subscribeSimple(
                onSuccess = {
                    val favorites = it.filter { userDetail -> userDetail.binds?.userFavorite != null }
                    viewState.setChatsData(data, favorites)
                },
                onError = {
                    viewState.setChatsData(data, emptyList())
                }
            )
    }

    private fun subscribeChatSocketMessages() {
        compositeDisposable += socket.subscribeToMessagesCount()
            .performOnBackgroundOutOnMain()
            .subscribe {
                viewState.setChatUnreadMessageCount(it.room, it.count)
            }
        compositeDisposable += appData.chatUnreadMessageSubject
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.setChatUnreadMessage(it.value?.chat.toString(), it.value?.message ?: "")
            }
    }

    override fun onChatClick(userChat: UserChat) =
        viewState.openChat(userChat.id, userChat.user.nameLastName, userChat.user.image.uri)

    override fun onUserClick(
        uid: Int,
        userName: String,
        avatar: String?,
        chatRoomWithMe: ChatRoomWithMeModel?
    ) {
        if (chatRoomWithMe == null) {
            compositeDisposable += chatRepository.createChat(CreateChatBody(uid))
                .performOnBackgroundOutOnMain()
                .withCustomProgressBarLoadingDialog(viewState)
                .subscribeSimple {
                    viewState.openChat(it.id, userName, avatar)
                }
        } else {
            viewState.openChat(chatRoomWithMe.id, userName, avatar)
        }
    }

    override fun onFabAddChatClick() = viewState.openSearch()

    override fun onEmptyChatsButtonAddChatClick() = viewState.openSearch()

    override fun onItemTake(position: Int) {
        chatsPagination.onItemTake(position)
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

    private fun getLastMessage(it: ChatModel): String {
        var message = ""
        if (it.binds?.lastMessage?.file != null) {
            message = "Изображение"
        } else {
            if (it.binds?.lastMessage?.message == CHAT_SERVICE_MESSAGE_ACCEPT) {
                if (it.binds.lastMessage.acknowledge?.firstOrNull()?.user != appData.getId()) {
                    message = "Пользователь подтвердил чат"
                } else {
                    message = "Вы подтвердили чат"
                }
            } else {
                message = it.binds?.lastMessage?.message ?: ""
            }
        }
        return message
    }

    private fun prepareListOfChats(it: List<ChatModel>): List<UserChat> {
        return it.sortedByDescending { x -> x.binds?.lastMessage?.createdDate }.map {
            val user = if (it.isEventChat()) {
                val img = it.binds?.event?.image
                UserDetail(
                    it.binds?.event?.id ?: 0,
                    it.binds?.event?.name,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    ContactInformationModel(null, null, null),
                    null,
                    ImageModel(
                        img?.mimeType,
                        img?.size,
                        it.binds?.lastMessage?.event?.url ?: img?.uri,
                        img?.name,
                        1,
                        null
                    ),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    false
                )
            } else {
                it.binds?.users?.first { us -> us.id != appData.getId() }!!
            }
            UserChat(
                it.id,
                user,
                it.createdDate ?: "",
                it.binds?.lastMessage?.message,
                it.binds?.lastMessage?.createdDate,
                if (it.binds?.lastMessage?.file == null) Message.MessageType.TEXT else Message.MessageType.IMAGE,
                if (!it.binds?.lastMessage?.acknowledge.isNullOrEmpty()) it.binds?.lastMessage?.acknowledge?.get(
                    0
                )?.user else 0,
                null,
                it.binds?.lastMessage?.id.toString(),
                false,
                it.isInInvites(appData.getId()),
                it.isWaitForAcceptInvites(appData.getId()),
                it.isBannedByRecipient(appData.getId()),
                it.isBannedByYou(appData.getId()),
                it.isEventChat(),
                it.binds?.event?.id.toString(),
                it.unreadMessagesCount ?: 0
            )
        }
    }
}

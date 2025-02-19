package com.example.ui.chatList.contacts

import android.widget.LinearLayout.generateViewId
import androidx.paging.map
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
import com.example.extensions.buildFlow
import com.example.extensions.buildObservable
import com.example.repository.ChatRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationResponse
import com.example.util.paginationNew.PagingDataSourceFactory
import com.example.util.paginationNew.applyErrorHandler
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withProgressBarDialogLoading
import javax.inject.Inject
import kotlin.random.Random

@InjectViewState
class ChatListPresenter
@Inject constructor(
    private val chatRepository: ChatRepository,
    private val appData: AppData,
    private val userRepository: UserRepository,
    private val socket: SocketIOManager
) : BasePresenter<ChatListContract.View>(appData), ChatListContract.Presenter {

    private val chatsList = arrayListOf<UserChatModel>()

    private val pagination = PagingDataSourceFactory { limit, offset ->
        getPaginationRequest(limit, offset)
    }.applyErrorHandler { onReceivePagingError(it) }.buildFlow(initialSize = 30, distance = 5)


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += socket.subscribeToMessagesCount()
            .performOnBackgroundOutOnMain()
            .subscribeSimple { viewState.setChatUnreadMessageCount(it.room, it.count) }

        compositeDisposable += socket.subscribeNewChatMessage()
            .performOnBackgroundOutOnMain()
            .subscribeSimple { prepareNewMessage(it.data.first()) }

        compositeDisposable += Flowable.create(pagination, BackpressureStrategy.LATEST)
            .map { pagingData -> pagingData.map { data -> data } }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onNext = { viewState.setData(it) }
            )
    }


    override fun onChatClick(chat: UserChatModel) {
        viewState.openChat(chat.id, chat.user.name, chat.user.image)
    }


    override fun onUserClick(user: UserDetail, chatRoomWithMe: ChatRoomWithMeModel?) {
        if (chatRoomWithMe == null) {
            compositeDisposable += chatRepository.createChat(CreateChatBody(user.id))
                .performOnBackgroundOutOnMain()
                .withProgressBarDialogLoading(viewState)
                .subscribeSimple {
                    viewState.openChat(it.id, user.nameLastName, user.loadUserImage())
                }
        } else viewState.openChat(chatRoomWithMe.id, user.nameLastName, user.loadUserImage())
    }


    override fun onAddChatClick() = viewState.openSearch()

    override fun onRefreshRequest() {
        chatsList.clear()
        pagination.invalidate()
    }


    private fun prepareNewMessage(message: MessageModel) {
        val data = chatsList.find { x -> x.id == message.chat }
        if (data != null) {
            val newData = data.copy(
                lastMessageId = message.id.toString(),
                lastMessage = message.message,
                lastMessageDate = message.createdDate,
                lastMessageType = if (message.file == null) Message.MessageType.TEXT else Message.MessageType.IMAGE,
                unreadMessageCount = data.unreadMessageCount + 1
            )

            chatsList.set(chatsList.indexOf(data), newData)
            pagination.invalidateFrom(chatsList)
        }
    }

    private fun getPaginationRequest(
        limit: Int,
        offset: Int
    ): Maybe<PaginationResponse<UserChatModel>> {
        return chatRepository.getChats(
            mapOf(
                CHAT_SORT to "desc",
                CHAT_LIMIT to limit,
                CHAT_OFFSET to offset,
                CHAT_BINDS to "users,event,bans,last-message",
                CHAT_USER_STATUS to "accepted",
                CHAT_USER to appData.getId(),
                CHAT_SHOW_EVENTS to true
            )
        ).map {
            chatsList.addAll(it.data.map { UserChatModel.createFromChatModel(it, appData.getId()) })
            PaginationResponse(it.totalCount, chatsList)
        }
    }
}

package com.example.ui.chatList.contacts

import androidx.paging.PagingData
import com.example.data.AppData
import com.example.data.bodies.CreateChatBody
import com.example.data.models.ChatModel.Companion.CHAT_BINDS
import com.example.data.models.ChatModel.Companion.CHAT_LIMIT
import com.example.data.models.ChatModel.Companion.CHAT_OFFSET
import com.example.data.models.ChatModel.Companion.CHAT_SHOW_EVENTS
import com.example.data.models.ChatModel.Companion.CHAT_SORT
import com.example.data.models.ChatModel.Companion.CHAT_USER
import com.example.data.models.ChatModel.Companion.CHAT_USER_STATUS
import com.example.data.models.ChatRoomWithMeModel
import com.example.data.models.Message
import com.example.data.models.MessageModel
import com.example.data.models.Optional
import com.example.data.models.UserChatModel
import com.example.data.models.UserDetail
import com.example.data.models.asOptional
import com.example.data.socket.SocketIOManager
import com.example.extensions.buildFlow
import com.example.repository.ChatRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationResponse
import com.example.util.paginationNew.PagingDataSourceFactory
import com.example.util.paginationNew.applyErrorHandler
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withProgressBarDialogLoading
import javax.inject.Inject

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
            .subscribeSimple {  }

        compositeDisposable += socket.subscribeNewChatMessage()
            .performOnBackgroundOutOnMain()
            .subscribeSimple { prepareNewMessage(it.data.first()) }

        compositeDisposable += Flowable.create(pagination, BackpressureStrategy.LATEST)
            //.map { pagingData -> pagingData.map { data -> data } }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onNext = { viewState.setData(it) }
            )
    }


    override fun onChatClick(chat: UserChatModel) {
        viewState.openChat(chat.id.toString(), chat.user.name, chat.user.image)
    }


    override fun onUserClick(user: UserDetail, chatRoom: ChatRoomWithMeModel?) {
        if (chatRoom == null) {
            compositeDisposable += chatRepository.createChat(CreateChatBody(user.id))
                .performOnBackgroundOutOnMain()
                .withProgressBarDialogLoading(viewState)
                .subscribeSimple {
                    viewState.openChat(it.id.toString(), user.nameLastName, user.loadUserImage())
                }
        } else viewState.openChat(chatRoom.id.toString(), user.nameLastName, user.loadUserImage())
    }


    override fun onAddChatClick() = viewState.openSearch()

    override fun onRefreshRequest() {
        chatsList.clear()
        pagination.invalidate()
    }


    private fun prepareNewMessage(message: MessageModel) {
        compositeDisposable += Maybe.fromAction<Optional<PagingData<UserChatModel>>> {
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
                PagingData.from(chatsList).asOptional()
            } else Optional(null)
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                val data = it.value
                if (data != null) viewState.setData(data)
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

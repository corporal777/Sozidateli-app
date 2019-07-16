package com.example.ui.chat

import android.net.Uri
import android.widget.ImageView
import com.arellomobile.mvp.InjectViewState
import com.example.R
import com.example.data.AppData
import com.example.data.models.ChatMessage
import com.example.events.OnSocketConnectEvent
import com.example.repository.ChatRepository
import com.example.ui.base.takePhoto.TakePhotoPresenter
import com.example.util.chat.ChatHelper
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import performOnBackgroundOutOnMain
import ru.houseofapps.chat.HAChat
import ru.houseofapps.chat.exceptions.NoConnectionException
import ru.houseofapps.chat.models.Message
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class ChatPresenter
@Inject constructor(
        private val chatHelper: ChatHelper,
        private val chatRepository: ChatRepository,
        private val haChat: HAChat,
        private val appData: AppData
) : TakePhotoPresenter<ChatContract.View>(), ChatContract.Presenter {

    lateinit var chatId: String
    lateinit var userId: String

    private var isChatHasMessages = false
    private var isChatScrolledToBottom = false
    private var isMessagesInitialLoad = false
    private var isMessageSend = false
    private var lastUnreadMessageId: String? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        EventBus.getDefault().register(this)

        compositeDisposable += getChat()
                .flatMapCompletable {
                    haChat.joinToRoom(chatId)
                            .andThen(haChat.addUsersToRoom(chatId, listOf(it.user.user_id.toString())))
                }
                .andThen(haChat.subscribeToChatMessageUpdates(chatId, CHAT_MESSAGE_LIST_PAGE_SIZE_LIMIT))
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({ messages ->
                    val userId = appData.getUser().user_id.toString()
                    val chatMessages: List<ChatMessage> = messages.map { ChatMessage.Personal(it, it.isUserMessage(userId)) }
                    val lastUnreadIndex = findLastUnreadMessageIndex(messages)
                    viewState.updateMessages(addUnreadMessagesItem(chatMessages, lastUnreadIndex))
                    scrollOnChatMessagesUpdate(lastUnreadIndex)
                    isMessagesInitialLoad = true
                }, {
                    if (it is NoConnectionException) {
                        viewState.showErrorDialog(listOf(R.string.not_connection_error), null)
                    }
                    it.printStackTrace()
                })
    }

    private fun getChat() = chatRepository.getChat(chatId)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                viewState.apply {
                    showAvatar(it.user.user_avatar)
                    when {
                        it.isBannedByYou -> showYouBanUser()
                        it.isBannedByRecipient -> showYouBanned()
                        it.isInInvites -> showChatConfirm()
                        else -> {
                            showChatInput(false)
                            focusOnInput(false)
                        }
                    }

                    isChatHasMessages = it.lastMessage != null
                }
            }
            .observeOn(Schedulers.io())

    private fun findLastUnreadMessageIndex(messages: List<Message>): Int {
        return if (!isMessageSend) {
            if (lastUnreadMessageId == null && !isMessagesInitialLoad) {
                val userId = appData.getUser().user_id.toString()
                lastUnreadMessageId = messages.findLast { message -> !message.isUserMessage(userId) && !message.wasRead }?._id
            }
            lastUnreadMessageId?.let { id -> messages.indexOfFirst { message -> message._id == id }.plus(1) }
                    ?: LAST_UNREAD_INDEX_INVALID
        } else LAST_UNREAD_INDEX_INVALID
    }

    private fun addUnreadMessagesItem(messages: List<ChatMessage>, index: Int): List<ChatMessage> {
        if (index != LAST_UNREAD_INDEX_INVALID) {
            return messages.toMutableList().apply { add(index, CHAT_MESSAGE_UNREAD_ITEM) }
        }

        return messages
    }

    private fun scrollOnChatMessagesUpdate(lastUnreadIndex: Int) {
        var forceScrollToBottom = false
        if (!isMessagesInitialLoad) {
            if (lastUnreadIndex != LAST_UNREAD_INDEX_INVALID) {
                viewState.scrollToMessagesUnreadItem(lastUnreadIndex)
                return
            } else {
                forceScrollToBottom = true
            }
        }

        viewState.checkScrollPosition()
        if (isChatScrolledToBottom || forceScrollToBottom) {
            viewState.scrollToBottomPosition(isMessagesInitialLoad)
        }
    }

    override fun attachView(view: ChatContract.View?) {
        super.attachView(view)
        viewState.cancelNotificationByChatId(chatId)
        chatHelper.currentChatId = chatId
    }

    override fun detachView(view: ChatContract.View?) {
        view?.hideKeyboard()
        super.detachView(view)
        chatHelper.currentChatId = null
    }

    override fun onSendTextMessageClick(message: String) {
        sendMessage(message, Message.Type.TEXT)
    }

    override fun onImageClick(url: String, imageView: ImageView) {
        viewState.openImageFullScreen(url, imageView)
    }

    private fun sendMessage(message: String, type: Message.Type) {
        if (!isMessageSend) {
            isMessageSend = true
            viewState.removeChatMessage(CHAT_MESSAGE_UNREAD_ITEM)
        }
        viewState.apply { clearMessageInput() }
        compositeDisposable += haChat.sendMessage(chatId, type, message)
                .flatMapCompletable {
                    if (!isChatHasMessages) getChat().withLoadingDialog(viewState).ignoreElement()
                    else Completable.complete()
                }
                .performOnBackgroundOutOnMain()
                .subscribe({}, {
                    if (it is NoConnectionException) {
                        viewState.showErrorDialog(listOf(R.string.not_connection_error), null)
                    }
                    it.printStackTrace()
                })
    }

    override fun onLoadPreviousMessagesRequest() {
        haChat.loadPreviousMessages(chatId, CHAT_MESSAGE_LIST_PAGE_SIZE_LIMIT)
    }

    override fun onLoadNextMessagesRequest() {
        haChat.loadNextMessages(chatId, CHAT_MESSAGE_LIST_PAGE_SIZE_LIMIT)
    }

    override fun onChatMessageOnScreen(message: ChatMessage.Personal) {
        haChat.readMessage(chatId, message.message)
    }

    override fun onChatScrollChange(isBottomPosition: Boolean) {
        isChatScrolledToBottom = isBottomPosition
    }

    override fun onImageTaken(path: String, uri: Uri) {
        compositeDisposable += chatRepository.uploadImage(chatId, path)
                .map {
                    it.response.firstOrNull()?.let { image ->
                        if (image.error || image.path.isNullOrEmpty()) null
                        else image.path
                    } ?: throw RuntimeException("Image uploading error")
                }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    sendMessage(it, Message.Type.IMAGE)
                }, {
                    it.printStackTrace()
                })
    }

    override fun onAcceptChatClick() {
        compositeDisposable += chatRepository.chatAccept(chatId)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({ viewState.showChatInput(true) }, {})
    }

    override fun onBlockChatClick() {
        viewState.showChatBlockConfirmation()
    }

    override fun onBlockChatConfirm() {
        compositeDisposable += chatRepository.chatBan(chatId)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({ viewState.navigateUp() }, {})
    }

    override fun onInputShowAnimationFinish() {
        viewState.focusOnInput(true)
    }

    override fun onMessageInput(message: String) {
        viewState.apply {
            if (message.isEmpty()) showAttachGroup()
            else showSendGroup()
        }
    }

    @Subscribe
    fun onSocketConnect(event: OnSocketConnectEvent) {
        haChat.loadNextMessages(chatId, CHAT_MESSAGE_LIST_PAGE_SIZE_LIMIT, true)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    companion object {
        private const val CHAT_MESSAGE_LIST_PAGE_SIZE_LIMIT = 50
        private const val LAST_UNREAD_INDEX_INVALID = -1

        private val CHAT_MESSAGE_UNREAD_ITEM = ChatMessage.Service(ChatMessage.Service.Type.NEW_MESSAGES)
    }
}

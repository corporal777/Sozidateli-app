package com.example.ui.chat

import android.net.Uri
import android.widget.ImageView
import com.arellomobile.mvp.InjectViewState
import com.example.R
import com.example.data.AppData
import com.example.data.models.ChatMessage
import com.example.data.models.UserChat
import com.example.events.OnSocketConnectEvent
import com.example.extensions.calendar
import com.example.extensions.isSameDay
import com.example.repository.ChatRepository
import com.example.ui.base.takePhoto.TakePhotoPresenter
import com.example.util.*
import com.example.util.chat.ChatHelper
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
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
import java.util.*
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
    var userAvatar: String? = null

    private var chat: UserChat? = null

    private var isChatHasMessages = false
    private var isChatScrolledToBottom = false
    private var isMessagesInitialLoad = false
    private var lastUnreadMessageId: String? = null
    private var isCanShowUnreadMessagesItem = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        EventBus.getDefault().register(this)

        userAvatar?.let { viewState.setUserAvatar(it) }

        subscribeToChatEvents()

        compositeDisposable += getChat()
                .flatMapCompletable {
                    haChat.joinToRoom(chatId)
                            .andThen(haChat.addUsersToRoom(chatId, listOf(it.user.user_id.toString())))
                }
                .andThen(haChat.subscribeToChatMessageUpdates(chatId, CHAT_MESSAGE_LIST_PAGE_SIZE_LIMIT))
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({ messages ->
                    val lastUnreadIndex = findLastUnreadMessageIndex(messages)
                    val chatMessages = createChatMessages(messages)
                            .addDates()
                            .addUnreadMessagesItem(lastUnreadIndex)
                    viewState.updateMessages(chatMessages)
                    scrollOnChatMessagesUpdate(lastUnreadIndex)
                    isMessagesInitialLoad = true
                }, {
                    if (it is NoConnectionException) {
                        viewState.showErrorDialog(listOf(R.string.not_connection_error), null)
                    }
                    it.printStackTrace()
                })
    }

    private fun subscribeToChatEvents() {
        val processEvent = { f: Flowable<String> ->
            f.flatMapSingle { if (it == chatId) getChat() else Single.never() }
                    .performOnBackgroundOutOnMain()
                    .subscribe({}, {})
        }

        compositeDisposable += processEvent(haChat.subscribeTo(ACTION_ACCEPT))
        compositeDisposable += processEvent(haChat.subscribeTo(ACTION_INVITE))
        compositeDisposable += processEvent(haChat.subscribeTo(ACTION_BAN))
        compositeDisposable += processEvent(haChat.subscribeTo(ACTION_UNBAN))
    }

    private fun getChat() = chatRepository.getChat(chatId)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                Completable.fromAction {
                    this.chat = it
                    viewState.apply {
                        when {
                            it.isBannedByYou -> disableMessaging { showYouBanUser() }
                            it.isBannedByRecipient -> disableMessaging { showYouBanned() }
                            it.isInInvites -> disableMessaging { showChatConfirm() }
                            it.isWaitForAcceptInvites -> disableMessaging { showWaitForInviteAccept() }
                            else -> {
                                showChatInput(false)
                                focusOnInput(false)
                            }
                        }

                        val avatarFromChat = it.user.user_avatar
                        if (userAvatar != avatarFromChat && avatarFromChat != null) {
                            setUserAvatar(avatarFromChat)
                        }

                        isChatHasMessages = it.lastMessage != null
                    }
                }
                        .andThen(Single.just(it))
            }
            .observeOn(Schedulers.io())

    private fun disableMessaging(action: () -> Unit) {
        action()
        isCanShowUnreadMessagesItem = false
        viewState.hideKeyboard()
    }

    private fun createChatMessages(messages: List<Message>): List<ChatMessage> {
        val userId = appData.getUser().user_id.toString()
        return messages.mapNotNull {
            when (it.type) {
                Message.Type.SERVICE -> {
                    if (it.message == CHAT_SERVICE_MESSAGE_ACCEPT) {
                        ChatMessage.Accept(it)
                    } else {
                        null
                    }
                }
                else -> ChatMessage.Personal(it, it.isUserMessage(userId))
            }
        }
    }

    private fun findLastUnreadMessageIndex(messages: List<Message>): Int {
        return if (isCanShowUnreadMessagesItem) {
            if (lastUnreadMessageId == null && !isMessagesInitialLoad) {
                val userId = appData.getUser().user_id.toString()
                lastUnreadMessageId = messages.findLast { message ->
                    message.type != Message.Type.SERVICE
                            && !message.isUserMessage(userId)
                            && !message.wasRead
                }?._id
            }
            lastUnreadMessageId?.let { id -> messages.indexOfFirst { message -> message._id == id }.plus(1) }
                    ?: LAST_UNREAD_INDEX_INVALID
        } else LAST_UNREAD_INDEX_INVALID
    }

    private fun List<ChatMessage>.addDates(): List<ChatMessage> {
        val list = toMutableList()
        val iterator = list.listIterator()

        val lastDate = Calendar.getInstance()
        iterator.forEach {
            val message = when (it) {
                is ChatMessage.Personal -> it.message
                is ChatMessage.Accept -> it.message
                else -> return@forEach
            }

            val messageDate = message.createdAt
            if (!messageDate.calendar().isSameDay(lastDate) && iterator.previousIndex() != 0) {
                iterator.previous()
                iterator.add(ChatMessage.Date(lastDate.timeInMillis))
                iterator.next()
            }

            if (!iterator.hasNext()) {
                iterator.add(ChatMessage.Date(messageDate))
            }

            lastDate.timeInMillis = messageDate
        }
        return list
    }

    private fun List<ChatMessage>.addUnreadMessagesItem(index: Int): List<ChatMessage> {
        return if (index != LAST_UNREAD_INDEX_INVALID) toMutableList().apply { add(index, CHAT_MESSAGE_UNREAD_ITEM) }
        else this
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
        if (isCanShowUnreadMessagesItem) {
            isCanShowUnreadMessagesItem = false
            viewState.removeChatMessage(CHAT_MESSAGE_UNREAD_ITEM)
        }
        viewState.apply { clearMessageInput() }

        val reloadChat = !isChatHasMessages
        if (reloadChat) viewState.showLoadingDialog()

        compositeDisposable += haChat.sendMessage(chatId, type, message)
                .flatMapCompletable {
                    if (reloadChat) getChat().ignoreElement()
                    else Completable.complete()
                }
                .performOnBackgroundOutOnMain()
                .subscribe({
                    if (reloadChat) viewState.hideLoadingDialog()
                }, {
                    if (reloadChat) viewState.hideLoadingDialog()
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

    override fun onChatMessageOnScreen(message: Message) {
        haChat.readMessage(chatId, message)
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
        val recipient = chat?.user?.user_id?.toString() ?: return
        compositeDisposable += chatRepository.chatAccept(chatId)
                .andThen(haChat.sendMessage(chatId, Message.Type.SERVICE, CHAT_SERVICE_MESSAGE_ACCEPT, recipient))
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

    override fun onUserClick() {
        chat?.user?.user_id?.let { viewState.showUser(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        haChat.leaveRoom(chatId)
        EventBus.getDefault().unregister(this)
    }

    companion object {
        private const val CHAT_MESSAGE_LIST_PAGE_SIZE_LIMIT = 50
        private const val LAST_UNREAD_INDEX_INVALID = -1

        private val CHAT_MESSAGE_UNREAD_ITEM = ChatMessage.NewMessages
    }
}

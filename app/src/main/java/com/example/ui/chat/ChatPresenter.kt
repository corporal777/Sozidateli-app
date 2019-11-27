package com.example.ui.chat

import android.widget.ImageView
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.ChatMessage
import com.example.data.models.ChatMessageAdditionalData
import com.example.data.models.UserChat
import com.example.events.OnSocketConnectEvent
import com.example.extensions.calendar
import com.example.extensions.isSameDay
import com.example.repository.ChatRepository
import com.example.ui.base.BasePresenter
import com.example.util.ACTION_INVITE
import com.example.util.CHAT_SERVICE_MESSAGE_ACCEPT
import com.example.util.ChatHelper
import com.example.util.IMAGE_MAX_SIZE_CHAT
import com.example.util.rxtakephoto.ResultRotation
import com.example.util.rxtakephoto.RxTakePhoto
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
import performOnBackgroundOutOnMain
import ru.houseofapps.chat.HAChat
import ru.houseofapps.chat.models.Message
import withCheckInternetConnectivity
import withLoadingDialog
import java.util.*
import javax.inject.Inject

@InjectViewState
class ChatPresenter
@Inject constructor(
        private val chatHelper: ChatHelper,
        private val chatRepository: ChatRepository,
        private val haChat: HAChat,
        private val appData: AppData,
        private val takePhoto: RxTakePhoto
) : BasePresenter<ChatContract.View>(), ChatContract.Presenter {

    lateinit var chatId: String
    var userAvatar: String? = null
    var userName: String? = null

    private var chat: UserChat? = null

    private var isChatHasMessages = false
    private var isChatScrolledToBottom = false
    private var isMessagesInitialLoad = false
    private var lastUnreadMessageId: String? = null
    private var isCanShowUnreadMessagesItem = true
    private var scrollPosition = 0
    private var scrollOffset = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        EventBus.getDefault().register(this)

        userAvatar?.let { viewState.setUserAvatar(it) }
        userName?.let { viewState.setTitle(it) }

        subscribeToChatEvents()

        compositeDisposable += getChat()
                .flatMapCompletable {
                    haChat.joinToRoom(chatId)
                            .andThen(
                                    if (!it.isEventChat) haChat.addUsersToRoom(chatId, listOf(it.user.user_id.toString()))
                                    else Completable.complete()
                            )
                }
                .andThen(haChat.subscribeToChatMessageUpdates(chatId, CHAT_MESSAGE_LIST_PAGE_SIZE_LIMIT))
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    val lastUnreadIndex = findLastUnreadMessageIndex(it)
                    val chatMessages = createChatMessages(it)
                            .addDates()
                            .addUnreadMessagesItem(lastUnreadIndex)
                    viewState.updateMessages(chatMessages)
                    scrollOnChatMessagesUpdate(lastUnreadIndex)
                    isMessagesInitialLoad = true
                }
    }

    private fun subscribeToChatEvents() {
        val processEvent = { f: Flowable<String> ->
            f.flatMapSingle { if (it == chatId) getChat() else Single.never() }
                    .performOnBackgroundOutOnMain()
                    .subscribe({}, {})
        }

//        compositeDisposable += processEvent(haChat.subscribeTo(ACTION_ACCEPT))
        compositeDisposable += processEvent(haChat.subscribeTo(ACTION_INVITE))
//        compositeDisposable += processEvent(haChat.subscribeTo(ACTION_BAN))
//        compositeDisposable += processEvent(haChat.subscribeTo(ACTION_UNBAN))
        compositeDisposable += processEvent(haChat.subscribeToExcludeFlagChange().map { it.roomKey })
    }

    private fun getChat() = chatRepository.getChat(chatId)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                Completable.fromAction {
                    this.chat = it
                    viewState.apply {
                        when {
                            it.isEventChat -> viewState.hideKeyboard()
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
                            userAvatar = avatarFromChat
                            setUserAvatar(avatarFromChat)
                        }

                        val name = it.user.fullName
                        if (userName != name) {
                            userName = name
                            setTitle(name)
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

//        if (scrolledView.add(view.hashCode()) && scrollPosition != 0 && scrollOffset != 0)
        viewState.scrollToPositionWithOffset(scrollPosition, scrollOffset)
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

        compositeDisposable += haChat.sendMessage(chatId, type, message, additionalData = createMessageAdditionalData())
                .flatMapCompletable {
                    if (reloadChat) getChat().ignoreElement()
                    else Completable.complete()
                }
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .subscribeSimple(
                        onError = {
                            if (reloadChat) viewState.hideLoadingDialog()
                            onReceiveError(it)
                        },
                        onComplete = {
                            if (reloadChat) viewState.hideLoadingDialog()
                        }
                )
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

    override fun onScrollChange(position: Int, offset: Int) {
        scrollPosition = position
        scrollOffset = offset
    }

    override fun onTakePhotoFromCameraRequest() = takePhoto(takePhoto.takeCameraImage())
    override fun onTakePhotoFromGalleryRequest() = takePhoto(takePhoto.takeGalleryImage())

    private fun takePhoto(takePhotoRequest: Observable<ResultRotation>) {
        compositeDisposable += takePhotoRequest
                .flatMapSingle { takePhoto.crop(resultRotation = it, outputMaxWidth = IMAGE_MAX_SIZE_CHAT, outputMaxHeight = IMAGE_MAX_SIZE_CHAT) }
                .flatMapSingle { chatRepository.uploadImage(chatId, it) }
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
                .andThen(haChat.sendMessage(chatId, Message.Type.SERVICE, CHAT_SERVICE_MESSAGE_ACCEPT, recipient, createMessageAdditionalData()))
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
        if (chat?.isEventChat != true) {
            chat?.user?.user_id?.let { viewState.showUser(it) }
        } else {
            chat?.eventId?.let { viewState.showEvent(it) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        haChat.leaveRoom(chatId)
        EventBus.getDefault().unregister(this)
    }

    private fun createMessageAdditionalData(): JSONObject {
        val currentUser = appData.getUser()
        val data = ChatMessageAdditionalData(currentUser.user_id, currentUser.user_name, currentUser.user_last_name, currentUser.user_avatar)
        return JSONObject(Gson().toJson(data))
    }

    companion object {
        private const val CHAT_MESSAGE_LIST_PAGE_SIZE_LIMIT = 50
        private const val LAST_UNREAD_INDEX_INVALID = -1

        private val CHAT_MESSAGE_UNREAD_ITEM = ChatMessage.NewMessages
    }
}

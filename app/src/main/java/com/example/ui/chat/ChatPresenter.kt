package com.example.ui.chat

import android.util.Log
import android.widget.ImageView
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.CreateChatBody
import com.example.data.models.ChatMessage
import com.example.data.models.ChatMessageAdditionalData
import com.example.data.models.ChatModel
import com.example.data.models.UserChat
import com.example.data.socket.SocketIOManager
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
import com.isseiaoki.simplecropview.CropImageView
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
        //private val haChat: HAChat,
        private val socket: SocketIOManager,
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

    private var newMessagesMessage: ChatMessage.NewMessages? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        EventBus.getDefault().register(this)

        userAvatar?.let { viewState.setUserAvatar(it) }
        userName?.let { viewState.setTitle(it) }

        subscribeToChatEvents()

        compositeDisposable += getChat()
                .flatMapCompletable {
                    socket.connectToSocket()
                    /*haChat.joinToRoom(chatId)
                            .andThen(
                                    if (!it.isEventChat) haChat.addUsersToRoom(chatId, listOf(it.user.id.toString()))
                                    else Completable.complete()
                            )*/
                }
                .andThen(/*haChat.subscribeToChatMessageUpdates(chatId, CHAT_MESSAGE_LIST_PAGE_SIZE_LIMIT)*/socket.subscribeToChatUpdate(chatId))
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    Log.ERROR
                    /*val lastUnreadIndex = findLastUnreadMessageIndex(it)
                    val chatMessages = createChatMessages(it)
                            .addDates()
                            .addUnreadMessagesItem(lastUnreadIndex)
                    viewState.updateMessages(chatMessages)
                    scrollOnChatMessagesUpdate(lastUnreadIndex)
                    isMessagesInitialLoad = true*/
                }
    }

    private fun subscribeToChatEvents() {
        val processEvent = { f: Flowable<String> ->
            f.flatMapSingle { if (it == chatId) getChat() else Single.never() }
                    .performOnBackgroundOutOnMain()
                    .subscribe({}, {})
        }

//        compositeDisposable += processEvent(haChat.subscribeTo(ACTION_ACCEPT))
        //compositeDisposable += processEvent(haChat.subscribeTo(ACTION_INVITE))
//        compositeDisposable += processEvent(haChat.subscribeTo(ACTION_BAN))
//        compositeDisposable += processEvent(haChat.subscribeTo(ACTION_UNBAN))
        //compositeDisposable += processEvent(haChat.subscribeToExcludeFlagChange().map { it.roomKey })
    }

    private fun getChat() = chatRepository.getChatById(chatId, mapOf(ChatModel.CHAT_BINDS to "users,event,bans,last-unread-message"))
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                Completable.fromAction {
                    this.chat = UserChat(it.id, it.binds?.users?.first { us -> us.id != appData.getId() }!!,
                            it.createdDate?: "", it.binds.lastUnreadMessage?.message, it.binds.lastUnreadMessage?.createdDate,
                            if (it.binds.lastUnreadMessage?.file == null) Message.Type.TEXT else Message.Type.IMAGE,
                            it.binds.lastUnreadMessage?.acknowledge?.get(0)?.user, null, it.binds.lastUnreadMessage?.id.toString(),
                            false, it.isInInvites(appData.getId()), it.isWaitForAcceptInvites(), false, it.isBannedByYou(appData.getId()), it.isEventChat(),
                            it.binds.event?.id.toString(), 0)
                    viewState.apply {
                        when {
                            it.isEventChat() -> viewState.hideKeyboard()
                            it.isBannedByYou(appData.getId()) -> disableMessaging { showYouBanUser() }
                            //it.isBannedByRecipient -> disableMessaging { showYouBanned() }
                            it.isInInvites(appData.getId()) -> disableMessaging { showChatConfirm(it.binds.users.first { us -> us.id != appData.getId() }.fullName) }
                            it.isWaitForAcceptInvites() -> disableMessaging { showWaitForInviteAccept() }
                            else -> {
                                showChatInput(false)
                                focusOnInput(false)
                            }
                        }

                        val avatarFromChat = it.binds.users[0].image.uri
                        if (userAvatar != avatarFromChat && avatarFromChat != null) {
                            userAvatar = avatarFromChat
                            setUserAvatar(avatarFromChat)
                        }

                        val name = it.binds.users[0].fullName
                        if (userName != name) {
                            userName = name
                            setTitle(name)
                        }

                        isChatHasMessages = it.binds.lastUnreadMessage != null
                    }
                }
                        .andThen(Single.just(it))
            }
            .observeOn(Schedulers.io())
    /*chatRepository.getChat(chatId)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                Completable.fromAction {
                    this.chat = it
                    viewState.apply {
                        when {
                            it.isEventChat -> viewState.hideKeyboard()
                            it.isBannedByYou -> disableMessaging { showYouBanUser() }
                            it.isBannedByRecipient -> disableMessaging { showYouBanned() }
                            it.isInInvites -> disableMessaging { showChatConfirm(it.user.fullName) }
                            it.isWaitForAcceptInvites -> disableMessaging { showWaitForInviteAccept() }
                            else -> {
                                showChatInput(false)
                                focusOnInput(false)
                            }
                        }

                        val avatarFromChat = it.user.image.uri/*user_avatar*/
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
            .observeOn(Schedulers.io())*/

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
        return if (index != LAST_UNREAD_INDEX_INVALID) toMutableList().apply {
            newMessagesMessage = ChatMessage.NewMessages(index).apply {
                add(index, this)
            }
        }
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
            newMessagesMessage?.let {
                viewState.removeChatMessage(it)
            }
        }
        viewState.apply { clearMessageInput() }

        val reloadChat = !isChatHasMessages
        if (reloadChat) viewState.showLoadingDialog()

        //TODO fix this
        /*compositeDisposable += haChat.sendMessage(chatId, type, message, additionalData = createMessageAdditionalData())
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
                )*/
    }

    override fun onLoadPreviousMessagesRequest() {
        //TODO fix this
        //haChat.loadPreviousMessages(chatId, CHAT_MESSAGE_LIST_PAGE_SIZE_LIMIT)
    }

    override fun onLoadNextMessagesRequest() {
        //TODO fix this
        //haChat.loadNextMessages(chatId, CHAT_MESSAGE_LIST_PAGE_SIZE_LIMIT)
    }

    override fun onChatMessageOnScreen(message: Message) {
        //TODO fix this
        //haChat.readMessage(chatId, message)
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
                .flatMapSingle { takePhoto.crop(resultRotation = it, outputMaxWidth = IMAGE_MAX_SIZE_CHAT, outputMaxHeight = IMAGE_MAX_SIZE_CHAT, cropMode = CropImageView.CropMode.FREE) }
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
        compositeDisposable += chatRepository.acceptChat(chatId.toInt())
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({ viewState.showChatInput(true) }, {})
    }

    override fun onBlockChatClick() {
        viewState.showChatBlockConfirmation()
    }

    override fun onBlockChatConfirm() {
        compositeDisposable += chatRepository.chatBann(CreateChatBody(chatId.toInt()))
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.navigateUp()
                }, { it.printStackTrace() })
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
        //TODO fix this
        //haChat.loadNextMessages(chatId, CHAT_MESSAGE_LIST_PAGE_SIZE_LIMIT, true)
    }

    override fun onUserClick() {
        if (chat?.isEventChat != true) {
            chat?.user?.id?.let { viewState.showUser(it) }
        } else {
            chat?.eventId?.let { viewState.showEvent(it) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //haChat.leaveRoom(chatId)
        socket.stopListenChatUpdate(chatId)
        //socket.disconnectFromSocket()
        EventBus.getDefault().unregister(this)
    }

    private fun createMessageAdditionalData(): JSONObject {
        val currentUser = appData.getUser()
        val data = ChatMessageAdditionalData(currentUser.user_id, currentUser.user_name, currentUser.user_last_name, currentUser.user_middle_name, currentUser.user_avatar)
        return JSONObject(Gson().toJson(data))
    }

    companion object {
        private const val CHAT_MESSAGE_LIST_PAGE_SIZE_LIMIT = 50
        private const val LAST_UNREAD_INDEX_INVALID = -1
    }
}

package com.example.ui.chat

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.CreateChatBody
import com.example.data.models.*
import com.example.data.socket.SocketIOManager
import com.example.events.OnSocketConnectEvent
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateTimeFormatter
import com.example.extensions.isSameDay
import com.example.extensions.parseToLong
import com.example.repository.ChatRepository
import com.example.ui.base.BasePresenter
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import java.io.*
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
        private val takePhoto: RxTakePhoto,
        private val contentResolver: ContentResolver,
        private val context: Context
) : BasePresenter<ChatContract.View>(), ChatContract.Presenter {

    lateinit var chatId: String
    var userAvatar: String? = null
    var userName: String? = null
    private var allMessages: /*MutableList*/MutableSet<Message> = /*mutableListOf()*/mutableSetOf()
    var messagesSize = 0
    var isUpdateAfterMessage = false

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
                .flatMap {
                    chatRepository.getChatMessages(mapOf(MessageModel.MESSAGES_CHAT to chatId, MessageModel.MESSAGES_ACKNOWLEDGED_BY to appData.getId(),
                            MessageModel.MESSAGES_LIMIT to 1, MessageModel.MESSAGES_ACKNOWLEDGED_STATE to 0))
                }.withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    //banListener()
                    if (it.totalCount == 0) {
                        getAllMessages()
                    } else {
                        prepareListOfMessages(it)
                        subscribeToSocket()
                    }
                }
    }

    /*private fun banListener() {
        compositeDisposable += socket.subscribeToBannedList(chatId)
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .subscribeSimple {
                    Log.ERROR
                }
    }*/

    private fun getAllMessages() {
        compositeDisposable += chatRepository.getChatMessages(mapOf(MessageModel.MESSAGES_SORT_TYPE to "desc",
                MessageModel.MESSAGES_CHAT to chatId, MessageModel.MESSAGES_LIMIT to 40))
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    if (it.data.isNullOrEmpty()) {
                        hasPrevious = false
                    }
                    prepareListOfMessages(it)
                    subscribeToSocket()
                }
    }

    private fun subscribeToSocket() {
        compositeDisposable += socket.connectToChat(chatId)
                .andThen(socket.subscribeToChatUpdate())
                .performOnBackgroundOutOnMain()
                .subscribeSimple { socketData ->
                    if (!hasPrevious) {
                        prepareListOfMessages(socketData)
                    }
                }
        /*compositeDisposable += socket.subscribeToChatUpdate(chatId)
                .performOnBackgroundOutOnMain()
                .subscribeSimple { socketData ->
                    if (!hasPrevious) {
                        prepareListOfMessages(socketData)
                    }
                }*/
    }

    private var canScroll = 0
    private fun prepareListOfMessages(it: ApiNewResponse<List<MessageModel>>) {
        messagesSize = it.totalCount?: 0
        val result = it.data.map { m -> Message(m.id.toString(), if (m.file != null) Message.MessageType.IMAGE else Message.MessageType.TEXT,
                m.chat.toString(), if (m.file != null) m.file.uri ?: "" else m.message
                ?: "", m.createdBy.toString(),
                m.createdDate?.parseToLong(defaultServerDateTimeFormatter) ?: 0, 0, null,
                m.acknowledge?.firstOrNull { a -> a.user == appData.getId() }?.state ?: false, null) }
        allMessages.addAll(result)
        if (canScroll < 2) {
            canScroll += 1
            isMessagesInitialLoad = false
        }
        filterByDate()
        val lastUnreadIndex = findLastUnreadMessageIndex(allMessages.toMutableList())
        val chatMessages = createChatMessages(allMessages.toMutableList())
                .addDates()
                .addUnreadMessagesItem(lastUnreadIndex)
        viewState.updateMessages(chatMessages)
        scrollOnChatMessagesUpdate(lastUnreadIndex)
        isMessagesInitialLoad = true
    }

    private fun subscribeToChatEvents() {
        val processEvent = { f: Flowable<String> ->
            f.flatMapSingle { if (it == chatId) getChat() else Single.never() }
                    .performOnBackgroundOutOnMain()
                    .subscribe({}, {})
        }

        compositeDisposable += processEvent(socket.subscribeToBannedList(chatId))
        compositeDisposable += processEvent(socket.subscribeToInviteChange(chatId))
//        compositeDisposable += processEvent(haChat.subscribeTo(ACTION_ACCEPT))
        //compositeDisposable += processEvent(haChat.subscribeTo(ACTION_INVITE))
        //compositeDisposable += processEvent(haChat.subscribeTo(ACTION_BAN))
        //compositeDisposable += processEvent(haChat.subscribeTo(ACTION_UNBAN))
        //compositeDisposable += processEvent(haChat.subscribeToExcludeFlagChange().map { it.roomKey })
    }

    private fun getChat() = chatRepository.getChatById(chatId, mapOf(ChatModel.CHAT_BINDS to "users,event,bans,last-unread-message,last-message"))
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                Completable.fromAction {
                    val user = if (it.isEventChat()) {
                        val img = it.binds?.event?.image
                        UserDetail(it.binds?.event?.id?: 0, it.binds?.event?.name, null, null,
                                null, null, null,null, ContactInformationModel(null, null, null),
                                null, ImageModel(img?.mimeType, img?.size, it.binds?.lastMessage?.event?.url?: img?.uri, img?.name, 1, null), null, null, null, null,null,
                                null,null,null,null,null,false)
                    } else {
                        it.binds?.users?.first { us -> us.id != appData.getId() }!!
                    }
                    this.chat = UserChat(it.id, /*it.binds?.users?.first { us -> us.id != appData.getId() }!!*/user,
                            it.createdDate
                                    ?: "", it.binds?.lastUnreadMessage?.message, it.binds?.lastUnreadMessage?.createdDate,
                            if (it.binds?.lastUnreadMessage?.file == null) Message.MessageType.TEXT else Message.MessageType.IMAGE,
                            if (!it.binds?.lastMessage?.acknowledge.isNullOrEmpty()) it.binds?.lastUnreadMessage?.acknowledge?.get(0)?.user else 0, null, it.binds?.lastUnreadMessage?.id.toString(),
                            false, it.isInInvites(appData.getId()), it.isWaitForAcceptInvites(appData.getId()), it.isBannedByRecipient(appData.getId()), it.isBannedByYou(appData.getId()), it.isEventChat(),
                            it.binds?.event?.id.toString(), 0)

                    val me = it.users?.firstOrNull { us -> us.user == appData.getId() }
                    val opponent = it.users?.firstOrNull { us -> us.user != appData.getId() }
                    isUpdateAfterMessage = me?.status == "basic" && opponent?.status == "basic"

                    viewState.apply {
                        when {
                            it.isEventChat() -> viewState.hideKeyboard()
                            it.isBannedByYou(appData.getId()) -> disableMessaging { showYouBanUser() }
                            it.isBannedByRecipient(appData.getId()) -> disableMessaging { showYouBanned() }
                            it.isInInvites(appData.getId()) -> disableMessaging { showChatConfirm(it.binds?.users?.first { us -> us.id != appData.getId() }?.fullName) }
                            it.isWaitForAcceptInvites(appData.getId()) -> disableMessaging { showWaitForInviteAccept() }
                            else -> {
                                showChatInput(false)
                                focusOnInput(false)
                            }
                        }

                        val avatarFromChat =if (it.isEventChat()) {
                            it.binds?.lastMessage?.event?.url?: it.binds?.event?.image?.uri
                        } else {
                            it.binds?.users?.first { us -> us.id != appData.getId() }?.image?.uri
                        }
                        //val avatarFromChat = it.binds?.users?.first { us -> us.id != appData.getId() }?.image?.uri
                        if (userAvatar != avatarFromChat && avatarFromChat != null) {
                            userAvatar = avatarFromChat
                            setUserAvatar(avatarFromChat)
                        }

                        val name = if (it.isEventChat()) {
                            it.binds?.event?.name?: ""
                        } else {
                            it.binds?.users?.first { us -> us.id != appData.getId() }?.fullName?: ""
                        }
                        //val name = it.binds?.users?.first { us -> us.id != appData.getId() }?.fullName?: ""
                        if (userName != name) {
                            userName = name
                            setTitle(name)
                        }

                        isChatHasMessages = it.binds?.lastUnreadMessage != null
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
        val userId = appData.getUserNew().id.toString()
        return messages.mapNotNull {
            when (it.type) {
                Message.MessageType.SERVICE -> {
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
                val userId = appData.getUserNew().id.toString()
                lastUnreadMessageId = messages.findLast { message ->
                    message.type != Message.MessageType.SERVICE
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
        sendMessage(message, Message.MessageType.TEXT)
    }

    override fun onImageClick(url: String, imageView: ImageView) {
        viewState.openImageFullScreen(url, imageView)
    }

    private fun sendMessage(message: String, type: Message.MessageType) {
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
        compositeDisposable += Single.fromCallable {
            MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .apply {
                        addFormDataPart("chat", chatId)
                        addFormDataPart("message", message)
                    }.build()
        }.flatMap { chatRepository.sendChatMessage(it) }
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .subscribeSimple(
                        onError = {
                            if (reloadChat) viewState.hideLoadingDialog()
                            onReceiveError(it)
                            updateChatAfterFirstMessage()
                        },
                        onSuccess = {
                            if (reloadChat) viewState.hideLoadingDialog()
                            updateChatAfterFirstMessage()
                        }
                )
        /*compositeDisposable += chatRepository.sendChatMessage()
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .subscribeSimple {

                }*/
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

    private var hasPrevious = true
    private var isCallingPrevious = false
    override fun onLoadPreviousMessagesRequest(messageId: Int?) {
        //TODO fix this
        //haChat.loadPreviousMessages(chatId, CHAT_MESSAGE_LIST_PAGE_SIZE_LIMIT)
        //if (/*allMessages.size < messagesSize*/messageId != 0) {
        if (hasPrevious && !isCallingPrevious) {
            isCallingPrevious = true
            compositeDisposable += chatRepository.getChatMessages(
                    mutableMapOf<String, Any>().apply {
                        put(MessageModel.MESSAGES_SORT_TYPE, "desc")
                        if (messageId != null) put(MessageModel.MESSAGES_START_FROM, messageId)
                        put(MessageModel.MESSAGES_CHAT, chatId)
                        put(MessageModel.MESSAGES_LIMIT, 40)
                    }
                    /*mapOf(MessageModel.MESSAGES_SORT_TYPE to "desc",
                    MessageModel.MESSAGES_START_FROM to messageId, MessageModel.MESSAGES_CHAT to chatId,
                    MessageModel.MESSAGES_LIMIT to 20)*/
            )
                    .performOnBackgroundOutOnMain()
                    .subscribeSimple {
                        hasPrevious = it.totalCount != 0
                        isCallingPrevious = false
                        prepareListOfMessages(it)
                    }
        }
        //}
    }

    private var hasNext = true
    private var isCallingNext = false
    override fun onLoadNextMessagesRequest(messageId: Int?) {
        //TODO fix this
        //haChat.loadNextMessages(chatId, CHAT_MESSAGE_LIST_PAGE_SIZE_LIMIT)
        //if (/*allMessages.size < messagesSize*/messageId != 0) {
        if (hasNext && !isCallingNext) {
            isCallingNext = true
            compositeDisposable += chatRepository.getChatMessages(
                    mutableMapOf<String, Any>().apply {
                        put(MessageModel.MESSAGES_SORT_TYPE, "desc")
                        if (messageId != null) put(MessageModel.MESSAGES_ENDS_BY, messageId)
                        put(MessageModel.MESSAGES_CHAT, chatId)
                        put(MessageModel.MESSAGES_LIMIT, 40)
                    }
            )
                    .performOnBackgroundOutOnMain()
                    .subscribeSimple {
                        hasNext = it.totalCount != 0
                        isCallingNext = false
                        prepareListOfMessages(it)
                    }
        }
        //}
    }

    private fun filterByDate() {
        val sorted = allMessages.sortedByDescending { it.createdAt}
        allMessages = sorted.toMutableSet()
        //allMessages.sortByDescending { it.createdAt }
    }

    override fun onChatMessageOnScreen(message: Message) {
        if (!message.wasRead) {
            compositeDisposable += chatRepository.markMessageAsRead(message._id.toInt())
                    .performOnBackgroundOutOnMain()
                    .subscribe({

                    }, {

                    })
        }
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

    private fun buildImageBodyPart(fileName: String, bitmap: Bitmap):  MultipartBody.Part {
        val leftImageFile = convertBitmapToFile(fileName, bitmap)
        val reqFile = RequestBody.create("image/*".toMediaTypeOrNull(), leftImageFile)
        return MultipartBody.Part.createFormData(fileName, leftImageFile.name, reqFile)
    }

    private fun convertBitmapToFile(fileName: String, bitmap: Bitmap): File {
        //create a file to write bitmap data
        val file = File(context.cacheDir, fileName)
        file.createNewFile()

        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos)
        val bitMapData = bos.toByteArray()

        //write the bytes in file
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        try {
            fos?.write(bitMapData)
            fos?.flush()
            fos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    private fun takePhoto(takePhotoRequest: Observable<ResultRotation>) {
        compositeDisposable += takePhotoRequest
                .flatMapSingle { takePhoto.crop(resultRotation = it, outputMaxWidth = IMAGE_MAX_SIZE_CHAT, outputMaxHeight = IMAGE_MAX_SIZE_CHAT, cropMode = CropImageView.CropMode.FREE) }
                .subscribe({
                    val reloadChat = !isChatHasMessages
                    if (reloadChat) viewState.showLoadingDialog()

                    compositeDisposable += Single.fromCallable {
                        MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .apply {
                                    addFormDataPart("chat", chatId)
                                    val leftImageFile = convertBitmapToFile("temp_file.png", it)

                                    val reqFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), leftImageFile)
                                    addFormDataPart("file", "temp_file.png", reqFile)
                                }.build()
                    }.flatMap {
                        chatRepository.sendChatMessage(it)
                    }
                            .performOnBackgroundOutOnMain()
                            .withLoadingDialog(viewState)
                            .subscribeSimple(
                                    onError = {
                                        if (reloadChat) viewState.hideLoadingDialog()
                                        onReceiveError(it)
                                        updateChatAfterFirstMessage()
                                    },
                                    onSuccess = {
                                        if (reloadChat) viewState.hideLoadingDialog()
                                        updateChatAfterFirstMessage()
                                    }
                            )
                },{

                })
        /*compositeDisposable += takePhotoRequest
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
                })*/
    }

    private fun updateChatAfterFirstMessage() {
        if (isUpdateAfterMessage) compositeDisposable += getChat().performOnBackgroundOutOnMain().subscribeSimple(onError = null, onSuccess = {})
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
        socket.disconnectFromChat(chatId)
        socket.stopListenChatUpdate()
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

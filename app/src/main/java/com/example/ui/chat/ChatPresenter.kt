package com.example.ui.chat

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.CreateChatBody
import com.example.data.models.*
import com.example.data.models.ChatModel.Companion.CHAT_BINDS
import com.example.data.socket.SocketIOManager
import com.example.extensions.*
import com.example.repository.ChatRepository
import com.example.ui.base.BasePresenter
import com.example.util.CHAT_SERVICE_MESSAGE_ACCEPT
import com.example.util.ChatHelper
import com.example.util.IMAGE_MAX_SIZE_CHAT
import com.example.util.convertBitmapToFile
import com.example.util.pagination.observable.PaginationList
import com.example.util.pagination.PaginationResponse
import com.example.util.pagination.observable.PaginationDataSourceFactory
import com.example.util.pagination.observable.applyErrorHandler
import com.example.util.rxtakephoto.ResultRotation
import com.example.util.rxtakephoto.RxTakePhoto
import com.isseiaoki.simplecropview.CropImageView
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withCustomProgressBarLoadingDialog
import withDelay
import withProgressBarLoadingDialog
import java.net.UnknownHostException
import java.util.*
import java.util.concurrent.TimeUnit
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
) : BasePresenter<ChatContract.View>(appData), ChatContract.Presenter {

    lateinit var chatId: String
    var userAvatar: String = ""
    var userName: String = ""
    private var allMessages: MutableSet<Message> = mutableSetOf()
    var messagesSize = 0
    var isUpdateAfterMessage = false
    private var isFirstLaunch = true
    private var firstMessageWasRead = false
    private var isCanShowUnreadMessagesItem = true
    private var isEventChat = false
    private var userId = ""

    private val pagination: PaginationDataSourceFactory<MessageModel> =
        PaginationDataSourceFactory(::getPaginationRequest)
    private lateinit var paginationList: PaginationList<MessageModel>


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            setChatPlaceholder()
            setUserNameAvatar(userAvatar, userName)
        }

        initChat()
        getChatMessages()
        subscribeChatSocketMessages()
        subscribeToChatEvents()
    }

    override fun attachView(view: ChatContract.View?) {
        super.attachView(view)
        viewState.cancelNotificationByChatId(chatId)
    }

    private fun getChatMessages() {
        paginationList = pagination.applyErrorHandler {
            if (it.cause is UnknownHostException) hasNoConnectionError = true
        }.buildList(enablePlaceholders = false, initialSize = 40)

        compositeDisposable += Observable.create(paginationList)
            .flatMapMaybe { prepareListOfMessages(it) }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.apply {
                    if (it.isNullOrEmpty()) showEmptyChatPlaceholder()
                    else {
                        updateMessages(isFirstLaunch, it)
                        if (isFirstLaunch) {
                            scrollListToPosition(0, false)
                            isFirstLaunch = false
                        }
                    }
                }
            }
    }

    private fun subscribeChatSocketMessages() {
        compositeDisposable += socket.connectToChat(chatId)
            .andThen(socket.subscribeToChatUpdate())
            .flatMapMaybe { prepareListOfMessages(it.data) }
            .performOnBackgroundOutOnMain()
            .subscribeSimple { socketData ->
                if (!isFirstLaunch) {
                    viewState.apply {
                        updateMessages(true, socketData)
                        scrollListToPosition(0, true)
                    }
                }
            }
    }

    private fun subscribeToChatEvents() {
        compositeDisposable += socket.subscribeToBannedList(chatId)
            .flatMap { socket.subscribeToInviteChange(chatId) }
            .subscribeSimple {
                if (it == chatId) initChat()
            }
    }

    override fun onChatMessageOnScreen(message: Message) {
        if (!message.wasRead) {
            compositeDisposable += chatRepository.markMessageAsRead(message._id.toInt())
                .performOnBackgroundOutOnMain()
                .subscribe({
                    if (!firstMessageWasRead) {
                        val timerDisposable = CompositeDisposable()
                        timerDisposable += Observable.interval(10000, TimeUnit.MILLISECONDS)
                            .performOnBackgroundOutOnMain()
                            .subscribe({
                                if (it <= 0) {
                                    viewState.removeUnreadMessageLabel()
                                    firstMessageWasRead = true
                                    timerDisposable.clear()
                                }
                            }, {
                                timerDisposable.clear()
                                it.printStackTrace()
                            })
                    }

                }, {
                    it.printStackTrace()
                })
        }
    }

    private fun prepareListOfMessages(it: List<MessageModel>): Maybe<List<ChatMessage>> {
        return Maybe.fromCallable {
            val result = it.filter { x -> x.chat.toString() == chatId }.map { m ->
                Message(
                    m.id.toString(),
                    if (m.file != null) Message.MessageType.IMAGE else Message.MessageType.TEXT,
                    m.chat.toString(),
                    if (m.file != null) m.file.uri ?: "" else m.message ?: "",
                    m.createdBy.toString(),
                    m.createdDate?.parseToLong(defaultServerDateTimeFormatter) ?: 0,
                    0,
                    null,
                    m.acknowledge?.firstOrNull { a -> a.user == appData.getId() }?.state ?: false,
                    null
                )
            }
            allMessages.apply { addAll(result) }
            createChatMessages(allMessages.toMutableList()).addDates()
        }
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

    private fun createChatMessages(messages: MutableList<Message>): List<ChatMessage> {
        val userId = appData.getUserNew().id.toString()
        val formattedMessages = arrayListOf<ChatMessage>()
        messages.sortedByDescending { x -> x.createdAt }.forEach {
            if (it.type == Message.MessageType.SERVICE && it.message == CHAT_SERVICE_MESSAGE_ACCEPT) {
                if (it.message == CHAT_SERVICE_MESSAGE_ACCEPT) {
                    formattedMessages.add(ChatMessage.Accept(it))
                }
            } else {
                formattedMessages.add(ChatMessage.Personal(it, it.isUserMessage(userId)))
            }
        }

        if (isCanShowUnreadMessagesItem) {
            if (messages.filter { x -> !x.wasRead }.isNotEmpty()) {
                val index = messages.indexOfLast { x -> !x.wasRead }
                formattedMessages.add(
                    index + 1,
                    ChatMessage.NewMessages(messages.filter { x -> !x.wasRead }.size)
                )
            }
            isCanShowUnreadMessagesItem = false
        }

        return formattedMessages
    }


    private fun getPaginationRequest(
        limit: Int,
        offset: Int
    ): Maybe<PaginationResponse<MessageModel>> {
        return chatRepository.getChatMessagesPagination(
            mapOf(
                MessageModel.MESSAGES_CHAT to chatId,
                MessageModel.MESSAGES_ACKNOWLEDGED_BY to appData.getId(),
                MessageModel.MESSAGES_LIMIT to limit,
                MessageModel.MESSAGES_OFFSET to offset,
                MessageModel.MESSAGES_SORT_TYPE to "desc"
            )
        )
    }

    override fun onAcceptChatClick() {
        compositeDisposable += chatRepository.acceptChat(chatId.toInt())
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribe({ viewState.showChatInput(true) }, {})
    }

    override fun onBlockChatClick() {
        viewState.showChatBlockConfirmation()
    }

    override fun onBlockChatConfirm() {
        compositeDisposable += chatRepository.chatBann(CreateChatBody(chatId.toInt()))
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribe({
                viewState.navigateUp()
            }, { it.printStackTrace() })
    }

    override fun onSendTextMessageClick(message: String) {
        viewState.apply { clearMessageInput() }

        compositeDisposable += Single.fromCallable {
            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .apply {
                    addFormDataPart("chat", chatId)
                    addFormDataPart("message", message)
                }.build()
        }.flatMap { chatRepository.sendChatMessage(it) }
            .withCheckInternetConnectivity()
            .flatMapMaybe { prepareListOfMessages(listOf(it)) }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                },
                onSuccess = {
                    viewState.apply {
                        updateMessages(true, it)
                        scrollListToPosition(0, true)
                    }
                }
            )
    }


    override fun onTakePhotoFromCameraRequest() =
        takePhoto(takePhoto.takeCameraImage().formatImage())

    override fun onTakePhotoFromGalleryRequest() =
        takePhoto(takePhoto.takeGalleryImage().formatImage())

    private fun Observable<ResultRotation>.formatImage(): Observable<Bitmap>? {
        return flatMapSingle {
            takePhoto.crop(
                resultRotation = it,
                outputMaxWidth = IMAGE_MAX_SIZE_CHAT,
                outputMaxHeight = IMAGE_MAX_SIZE_CHAT,
                cropMode = CropImageView.CropMode.FREE
            )
        }
    }

    private fun takePhoto(takePhotoRequest: Observable<Bitmap>?) {
        if (takePhotoRequest != null) {
            compositeDisposable += takePhotoRequest
                .flatMapSingle {
                    val body = MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .apply {
                            addFormDataPart("chat", chatId)
                            val leftImageFile = convertBitmapToFile(context, "temp_file.png", it)

                            val reqFile =
                                RequestBody.create("image/jpeg".toMediaTypeOrNull(), leftImageFile)
                            addFormDataPart("file", "temp_file.png", reqFile)
                        }.build()
                    Single.just(body)
                }
                .flatMapSingle { chatRepository.sendChatMessage(it) }
                .flatMapMaybe { prepareListOfMessages(listOf(it)) }
                .performOnBackgroundOutOnMain()
                .withCustomProgressBarLoadingDialog(viewState)
                .subscribeSimple(
                    onError = {
                        onReceiveError(it)
                    },
                    onNext = {
                        viewState.apply {
                            updateMessages(true, it)
                            scrollListToPosition(0, true)
                        }
                    }
                )
        }
    }


    private fun initChat() {
        compositeDisposable += chatRepository.getChatById(
            chatId,
            mapOf(CHAT_BINDS to "users,event,bans,last-unread-message,last-message")
        )
            .doOnSuccess {
                if (it.isEventChat()) {
                    isEventChat = true
                    userId = it.binds?.event?.id.toString()
                } else {
                    isEventChat = false
                    userId = it.users?.first { us -> us.user != appData.getId() }?.user.toString()
                }
            }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                val avatarFromChat =
                    if (it.isEventChat())
                        it.binds?.lastMessage?.event?.url ?: it.binds?.event?.image?.uri
                    else it.binds?.users?.first { us -> us.id != appData.getId() }?.image?.uri

                if (userAvatar != avatarFromChat && avatarFromChat != null) {
                    userAvatar = avatarFromChat
                    viewState.setUserNameAvatar(avatarFromChat, userName)
                }
                viewState.apply {
                    when {
                        it.isEventChat() -> {
                            hideKeyboard()
                        }
                        it.isBannedByYou(appData.getId()) -> disableMessaging { showYouBanUser() }
                        it.isBannedByRecipient(appData.getId()) -> disableMessaging { showYouBanned() }
                        it.isInInvites(appData.getId()) -> disableMessaging { showChatConfirm(it.binds?.users?.first { us -> us.id != appData.getId() }?.fullName) }
                        it.isWaitForAcceptInvites(appData.getId()) -> disableMessaging { showWaitForInviteAccept() }
                        else -> {
                            showChatInput(false)
                            focusOnInput(false)
                        }
                    }
                }
            }
    }

    override fun onUserClick() {
        if (isEventChat) viewState.showEvent(userId)
        else viewState.showUser(userId)
    }

    private fun disableMessaging(action: () -> Unit) {
        action()
        isCanShowUnreadMessagesItem = false
        viewState.hideKeyboard()
    }

    override fun onMessageInput(message: String) {
        viewState.apply {
            if (message.isEmpty()) showAttachGroup()
            else showSendGroup()
        }
    }

    override fun detachView(view: ChatContract.View?) {
        view?.hideKeyboard()
        super.detachView(view)
    }

    override fun onDestroy() {
        compositeDisposable += socket.disconnectFromChat(chatId)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                socket.stopListenChatUpdate()
                super.onDestroy()
            }
    }

    override fun onItemTake(position: Int) = paginationList.onItemTake(position)
}


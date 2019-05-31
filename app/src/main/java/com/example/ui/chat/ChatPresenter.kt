package com.example.ui.chat

import android.net.Uri
import android.widget.ImageView
import call
import com.arellomobile.mvp.InjectViewState
import com.example.R
import com.example.data.models.UserChatMessage
import com.example.data.models.user.User
import com.example.events.OnSocketConnectEvent
import com.example.repository.ChatRepository
import com.example.ui.base.takePhoto.TakePhotoPresenter
import com.example.util.chat.ChatHelper
import io.reactivex.rxkotlin.plusAssign
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
        private val haChat: HAChat
) : TakePhotoPresenter<ChatContract.View>(), ChatContract.Presenter {

    private var isChatScrolledToBottom = true

    lateinit var chatId: String
    lateinit var userId: String

    private var isFirstGetMessage = true
    private var beforeItemId: String? = null

    private var firstMessageId:String? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        EventBus.getDefault().register(this)

        chatRepository.getChat(chatId)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    val canSendMsg = if (it.inFavorite && !it.user.settings_chat_allow_msg_from_fav) false
                    else !(!it.user.settings_chat_allow_msg_from_all && !(it.inFavorite && it.user.settings_chat_allow_msg_from_fav))

                    viewState.apply {
                        showAvatar(it.user.user_avatar)
                        showCantSendHolder(!canSendMsg)
                    }

                    if (canSendMsg) joinChat(it.user)
                }, { it.printStackTrace() })
                .call(compositeDisposable)
    }

    private fun joinChat(withUser: User) {
        compositeDisposable += haChat.joinToRoom(chatId, listOf(withUser.user_id.toString()))
                .andThen(haChat.subscribeToChatMessageUpdates(chatId, CHAT_MESSAGE_LIST_PAGE_SIZE_LIMIT))
                .withLoadingDialog(viewState)
                .performOnBackgroundOutOnMain()
                .subscribe({ messages ->
                    val messagesWithNewMessage = mutableListOf<UserChatMessage>()
                    var lastMessage: Message? = null
                    var positionBeforeItem = -1
                    messages.forEach {
                        if (lastMessage != null && lastMessage?.wasRead != it.wasRead && isFirstGetMessage) {
                            beforeItemId = it._id
                        }

                        if (it._id == beforeItemId) positionBeforeItem = messages.indexOf(it)

                        messagesWithNewMessage.add(UserChatMessage(it, it.isUserMessage(appData.getUser().user_id.toString()), false))
                        lastMessage = it
                    }

                    if (positionBeforeItem != -1) {
                        messagesWithNewMessage.add(positionBeforeItem, UserChatMessage(Message("", Message.Type.TEXT, "", "", ""), false, true))
                    }

                    val isFirstChange = messages.let {
                        if(it.isNotEmpty() && firstMessageId!=null){
                           return@let firstMessageId != messages[0]._id
                        } else{
                            return@let false
                        }

                    }
                    if(messages.isNotEmpty()) firstMessageId!=messages[0]._id

                    viewState.updateMessages(messagesWithNewMessage)

                    if (isFirstGetMessage && positionBeforeItem != -1) {
                        viewState.scrollTo(positionBeforeItem)
                    } else {
                        if (isChatScrolledToBottom && isFirstChange) viewState.scrollToBottomPosition()
                    }
                    isFirstGetMessage = false
                }, {
                    if (it is NoConnectionException) {
                        viewState.showErrorDialog(listOf(R.string.not_connection_error), null)
                    }
                    it.printStackTrace()
                })
    }

    override fun attachView(view: ChatContract.View?) {
        super.attachView(view)
        viewState.cancelNotificationByChatId(chatId)
        chatHelper.currentChatId = chatId
    }

    override fun detachView(view: ChatContract.View?) {
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
        viewState.apply { clearMessageInput() }
        haChat.sendMessage(type, message) {
            chatRepository.sendChatMessage(chatId, message, it._id, type.value)
        }
                .performOnBackgroundOutOnMain()
                .subscribe({

                }, {
                    if (it is NoConnectionException) {
                        viewState.showErrorDialog(listOf(R.string.not_connection_error), null)
                    }
                    it.printStackTrace()
                })
                .call(compositeDisposable)
    }

    override fun onLoadMoreMessagesRequest() {
        haChat.loadNextMessageHistory(chatId, CHAT_MESSAGE_LIST_PAGE_SIZE_LIMIT)
    }

    override fun onChatMessageOnScreen(message: UserChatMessage) {
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

    @Subscribe
    fun onSocketConnect(event: OnSocketConnectEvent) {
        haChat.loadMessagesAfterLast(chatId)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    companion object {
        const val CHAT_MESSAGE_LIST_PAGE_SIZE_LIMIT = 40
    }
}

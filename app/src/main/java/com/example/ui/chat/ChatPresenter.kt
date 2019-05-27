package com.example.ui.chat

import android.net.Uri
import android.widget.ImageView
import call
import com.arellomobile.mvp.InjectViewState
import com.example.R
import com.example.data.models.UserChatMessage
import com.example.data.models.user.User
import com.example.repository.ChatRepository
import com.example.ui.base.takePhoto.TakePhotoPresenter
import com.example.util.chat.ChatNotificationHelper
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import ru.houseofapps.chat.HAChat
import ru.houseofapps.chat.exceptions.NoConnectionException
import ru.houseofapps.chat.models.Message
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class ChatPresenter
@Inject constructor(
        private val chatNotificationHelper: ChatNotificationHelper,
        private val chatRepository: ChatRepository,
        private val haChat: HAChat
) : TakePhotoPresenter<ChatContract.View>(), ChatContract.Presenter {

    private var isChatScrolledToBottom = true

    lateinit var chatId: String
    lateinit var userId: String

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
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
                .performOnBackgroundOutOnMain()
                .subscribe({ messages ->
                    viewState.updateMessages(messages.map { UserChatMessage(it, it.isUserMessage(appData.getUser().user_id.toString())) })
                    if (isChatScrolledToBottom) viewState.scrollToBottomPosition()
                }, {
                    if(it is NoConnectionException){
                        viewState.showErrorDialog(listOf(R.string.not_connection_error),null)
                    }
                    it.printStackTrace()
                })
    }

    override fun attachView(view: ChatContract.View?) {
        super.attachView(view)
        viewState.cancelNotificationByChatId(chatId)
        chatNotificationHelper.currentChatId = chatId
    }

    override fun detachView(view: ChatContract.View?) {
        super.detachView(view)
        chatNotificationHelper.currentChatId = null
    }

    override fun onSendTextMessageClick(message: String) {
        sendMessage(message, Message.Type.TEXT)
    }

    override fun onImageClick(url: String, imageView: ImageView) {
        viewState.openImageFullScreen(url, imageView)
    }

    private fun sendMessage(message: String, type: Message.Type) {
        viewState.apply { clearMessageInput() }
        haChat.sendMessage(type, message)
                .flatMapCompletable { chatRepository.sendChatMessage(chatId, message, it._id, type.value) }
                .performOnBackgroundOutOnMain()
                .subscribe({

                }, {
                    if(it is NoConnectionException){
                        viewState.showErrorDialog(listOf(R.string.not_connection_error),null)
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

    companion object {
        const val CHAT_MESSAGE_LIST_PAGE_SIZE_LIMIT = 40
    }
}

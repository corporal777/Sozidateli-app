package com.example.ui.chat

import android.net.Uri
import android.widget.ImageView
import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.UserChatMessage
import com.example.data.models.user.User
import com.example.repository.ChatRepository
import com.example.ui.base.takePhoto.TakePhotoPresenter
import com.example.util.chat.ChatMessagesDataProvider
import com.example.util.chat.ChatNotificationHelper
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import ru.houseofapps.chat.SocketRepository
import timber.log.Timber
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class ChatPresenter
@Inject constructor(
        private val chatNotificationHelper: ChatNotificationHelper,
        private val chatRepository: ChatRepository,
        private val socketRepository: SocketRepository,
        private val chatApiRepository: ru.houseofapps.chat.ChatRepository
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
        compositeDisposable += chatRepository.joinChat(chatId, arrayOf(withUser.user_id.toString()))
                .andThen(ChatMessagesDataProvider.provideFor(chatId, socketRepository, chatApiRepository).subscribeToChatMessageUpdates())
                .doOnSubscribe { Timber.tag("CHAT_T").d("SUBSCRIBE") }
                .performOnBackgroundOutOnMain()
                .subscribe({
                    viewState.updateMessages(it)
                    if (isChatScrolledToBottom) viewState.scrollToBottomPosition()
                }, {
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
        sendMessage(message, "text")
    }

    override fun onImageClick(url: String, imageView: ImageView) {
        viewState.openImageFullScreen(url, imageView)
    }

    private fun sendMessage(message: String, type: String) {
        viewState.apply { clearMessageInput() }
        chatRepository.sendChatMessage(chatId, message, type)
                .performOnBackgroundOutOnMain()
                .subscribe({

                }, {
                    it.printStackTrace()
                })
                .call(compositeDisposable)
    }

    override fun onLoadMoreMessagesRequest() {
        ChatMessagesDataProvider.provideFor(chatId, socketRepository, chatApiRepository).loadNextMessageHistory()
    }

    override fun onChatMessageOnScreen(message: UserChatMessage) {
//        if (!message.isMyMessage && message.message.isShowed != true)
//            message.message.id?.also { chatNotificationHelper.showedMessages.add(it) }
//        if (message.isMyMessage || message.message.isRead == true) return
//        messageToMarkReadPublisher.onNext(message.message)
    }

    override fun onChatScrollChange(isBottomPosition: Boolean) {
        isChatScrolledToBottom = isBottomPosition
    }

    override fun onImageTaken(path: String, uri: Uri) {
//        chatRepository.uploadImage(chatId, path)
//                .flatMap { response ->
//                    SingleSource<ChatMessage> {
//                        val imageResponse = response.response[0]
//                        if (imageResponse.error || imageResponse.path.isNullOrEmpty()) {
//                            it.onError(RuntimeException("Image uploading error"))
//                            return@SingleSource
//                        }
//
//                        viewState.getPhotoMessageText { text ->
//                            val message = ChatMessage(text = text, senderId = appData.getUser().user_id, image = imageResponse.path)
//                            it.onSuccess(message)
//                        }
//                    }
//                }
//                .performOnBackgroundOutOnMain()
//                .withLoadingDialog(viewState)
//                .subscribe({
//                    sendMessage(it)
//                }, {
//                    it.printStackTrace()
//                }).call(compositeDisposable)
    }

    companion object {
        const val CHAT_MESSAGE_LIST_PAGE_SIZE_LIMIT = 20
    }
}

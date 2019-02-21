package com.example.ui.chat

import android.net.Uri
import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.ChatMessage
import com.example.data.models.UserChatMessage
import com.example.repository.ChatRepository
import com.example.ui.base.BasePresenter
import com.example.ui.base.takePhoto.TakePhotoPresenter
import com.example.util.FIELD_IS_READ
import com.firebase.ui.firestore.SnapshotParser
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class ChatPresenter
@Inject constructor(
        private val appData: AppData,
        private val chatRepository: ChatRepository
) : TakePhotoPresenter<ChatContract.View>(), ChatContract.Presenter {

    private var isChatScrolledToLastPosition = true

    lateinit var chatId: String
    lateinit var userId: String

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        chatRepository.singInFirebase()
                .performOnBackgroundOutOnMain()
                .subscribe({
                    val chatMessageQuery = chatRepository.getChatMessageQuery(chatId)
                    val chatMessageParser = SnapshotParser { snapshot ->
                        snapshot.toObject(ChatMessage::class.java)!!.let {
                            it.id = snapshot.id
                            it.isRead = snapshot.getBoolean(FIELD_IS_READ)
                            UserChatMessage(it, appData.getUser().user_id == it.senderId)
                        }
                    }

                    viewState.apply { iniChatAdapter(chatMessageQuery, chatMessageParser) }
                }, {})
                .call(compositeDisposable)
    }

    override fun onSendTextMessageClick(message: String) {
        sendMessage(message)
    }

    override fun onImageClick(url: String) {
        viewState.openImageFullScreen(url)
    }

    private fun sendMessage(message: String, image: String? = null) {
        if (message.isBlank()) return
        viewState.apply { clearMessageInput() }
        chatRepository.sendChatMessage(chatId, userId, ChatMessage(text = message, senderId = appData.getUser().user_id, image = image))
                .performOnBackgroundOutOnMain()
                .subscribe({

                }, {
                    it.printStackTrace()
                })
                .call(compositeDisposable)
    }

    override fun onChatMessageOnScreen(message: UserChatMessage) {
        if (message.isMyMessage || message.message.isRead == true) return
        message.message.id?.also {
            chatRepository.setMessageRead(chatId, it)
                    .performOnBackgroundOutOnMain()
                    .subscribe({}, {
                        it.printStackTrace()
                    })
                    .call(compositeDisposable)
        }
    }

    override fun onNewMessage(message: UserChatMessage) {
        if (isChatScrolledToLastPosition) viewState.scrollToLastPosition()
    }

    override fun onChatScrollChange(isLastPosition: Boolean) {
        isChatScrolledToLastPosition = isLastPosition
    }

    override fun onImageTaken(path: String, uri: Uri) {
        chatRepository.uploadImage(chatId, path)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    if(!it.response[0].error) sendMessage("Фото",it.response[0].path)
                }, {
                    it.printStackTrace()
                }).call(compositeDisposable)
    }
}

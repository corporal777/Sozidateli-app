package com.example.ui.chat

import android.net.Uri
import android.widget.ImageView
import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.ChatMessage
import com.example.data.models.UserChatMessage
import com.example.repository.ChatRepository
import com.example.ui.base.takePhoto.TakePhotoPresenter
import com.example.util.Collector
import com.example.util.FIELD_IS_READ
import com.firebase.ui.firestore.SnapshotParser
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.subjects.PublishSubject
import performOnBackgroundOutOnMain
import withLoadingDialog
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class ChatPresenter
@Inject constructor(
        private val appData: AppData,
        private val chatRepository: ChatRepository
) : TakePhotoPresenter<ChatContract.View>(), ChatContract.Presenter {

    private var isChatScrolledToBottom = true

    lateinit var chatId: String
    lateinit var userId: String

    private val messageToMarkReadPublisher = PublishSubject.create<ChatMessage>()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        Observable.create<Collector<ChatMessage>> { it.onNext(createMessagesCollector(it)) }
                .flatMap { subscribeToMessageMarkRead(it) }
                .map { messages -> messages.mapNotNull { it.id }.distinct() }
                .flatMapCompletable { chatRepository.setMessagesRead(chatId, it) }
                .performOnBackgroundOutOnMain()
                .subscribe({}, { it.printStackTrace() })
                .call(compositeDisposable)

        chatRepository.singInFirebase()
                .andThen(chatRepository.getChat(chatId))
                .performOnBackgroundOutOnMain()
                .subscribe({

                    viewState.apply {
                        val parser = SnapshotParser { snapshot ->
                            snapshot.toObject(ChatMessage::class.java)!!.let {
                                it.id = snapshot.id
                                it.isRead = snapshot.getBoolean(FIELD_IS_READ)
                                UserChatMessage(it, appData.getUser().user_id == it.senderId)
                            }
                        }
                        viewState.setQuery(
                                chatRepository.getChatMessageQuery(chatId),
                                parser,
                                CHAT_MESSAGE_LIST_PAGE_SIZE_LIMIT
                        )
                        showCantSendHolder(false)
                        showAvatar(it.user.user_avatar)
                    }
                }, {})
                .call(compositeDisposable)
    }

    override fun attachView(view: ChatContract.View?) {
        super.attachView(view)
        viewState.cancelNotificationByChatId(chatId)
    }

    private fun createMessagesCollector(creatorEmitter: ObservableEmitter<Collector<ChatMessage>>): Collector<ChatMessage> {
        return Collector<ChatMessage>().apply {
            doOnRelease = Runnable { creatorEmitter.onNext(createMessagesCollector(creatorEmitter)) }
        }
    }

    private fun subscribeToMessageMarkRead(collector: Collector<ChatMessage>): Observable<List<ChatMessage>> {
        return messageToMarkReadPublisher
                .doOnNext { collector.add(it) }
                .debounce(200, TimeUnit.MILLISECONDS)
                .flatMapSingle { Single.just(collector.release()) }
    }

    override fun onSendTextMessageClick(message: String) {
        sendMessage(ChatMessage(text = message, senderId = appData.getUser().user_id))
    }

    override fun onImageClick(url: String, imageView: ImageView) {
        viewState.openImageFullScreen(url, imageView)
    }

    private fun sendMessage(chatMessage: ChatMessage) {
        if (chatMessage.text.isNullOrBlank()) return
        viewState.apply { clearMessageInput() }
        chatRepository.sendChatMessage(chatId, userId, chatMessage)
                .performOnBackgroundOutOnMain()
                .subscribe({
                    compositeDisposable
                }, {
                    it.printStackTrace()
                })
                .call(compositeDisposable)
    }

    override fun onChatMessageOnScreen(message: UserChatMessage) {
        if (message.isMyMessage || message.message.isRead == true) return
        messageToMarkReadPublisher.onNext(message.message)
    }

    override fun onChatScrollChange(isBottomPosition: Boolean) {
        isChatScrolledToBottom = isBottomPosition
    }

    override fun onImageTaken(path: String, uri: Uri) {
        chatRepository.uploadImage(chatId, path)
                .flatMap { response ->
                    SingleSource<ChatMessage> {
                        val imageResponse = response.response[0]
                        if (imageResponse.error || imageResponse.path.isNullOrEmpty()) {
                            it.onError(RuntimeException("Image uploading error"))
                            return@SingleSource
                        }

                        viewState.getPhotoMessageText { text ->
                            val message = ChatMessage(text = text, senderId = appData.getUser().user_id, image = imageResponse.path)
                            it.onSuccess(message)
                        }
                    }
                }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    sendMessage(it)
                }, {
                    it.printStackTrace()
                }).call(compositeDisposable)
    }

    companion object {
        const val CHAT_MESSAGE_LIST_PAGE_SIZE_LIMIT = 20
    }
}

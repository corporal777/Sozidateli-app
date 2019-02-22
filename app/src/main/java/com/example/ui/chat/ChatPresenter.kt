package com.example.ui.chat

import android.net.Uri
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

    private var isChatScrolledToLastPosition = true

    lateinit var chatId: String
    lateinit var userId: String

    private val messageToMarkReadPublisher = PublishSubject.create<ChatMessage>()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        Observable.create<Collector<ChatMessage>> { it.onNext(createMessagesCollector(it)) }
                .flatMap { subscribeToMessageMarkRead(it) }
                .map { messages -> messages.mapNotNull { it.id } }
                .flatMapCompletable { chatRepository.setMessagesRead(chatId, it) }
                .performOnBackgroundOutOnMain()
                .subscribe({}, { it.printStackTrace() })
                .call(compositeDisposable)

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
        messageToMarkReadPublisher.onNext(message.message)
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
                    if (!it.response[0].error) sendMessage("Фото", it.response[0].path)
                }, {
                    it.printStackTrace()
                }).call(compositeDisposable)
    }
}

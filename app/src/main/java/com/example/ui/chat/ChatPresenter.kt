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
import com.example.util.chat.QueryList
import com.example.util.chat.QueryPageOptions
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.ChangeEventListener
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
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
    private var queryList: QueryList<UserChatMessage>? = null

    lateinit var chatId: String
    lateinit var userId: String

    private val messageToMarkReadPublisher = PublishSubject.create<ChatMessage>()
    private val changeEventListener = object : ChangeEventListener {
        override fun onDataChanged() {

        }

        override fun onChildChanged(type: ChangeEventType, snapshot: DocumentSnapshot, newIndex: Int, oldIndex: Int) {
            viewState.apply {
                when (type) {
                    ChangeEventType.ADDED -> {
                        notifyItemInserted(newIndex)
                        if (newIndex == 0 && isChatScrolledToBottom) viewState.scrollToBottomPosition()
                    }
                    ChangeEventType.CHANGED -> notifyItemChanged(newIndex)
                    ChangeEventType.REMOVED -> notifyItemRemoved(oldIndex)
                    ChangeEventType.MOVED -> notifyItemMoved(oldIndex, newIndex)
                    else -> throw IllegalStateException("Incomplete when statement")
                }
            }
        }

        override fun onError(e: FirebaseFirestoreException) {
            viewState.showToast(e.localizedMessage)
        }
    }

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

                    queryList = QueryList(QueryPageOptions(
                            chatMessageQuery,
                            chatMessageParser,
                            20
                    )).apply {
                        addChangeEventListener(changeEventListener)
                        viewState.setQuery(this)
                    }
                }, {})
                .call(compositeDisposable)

        viewState.showCantSendHolder(false)
        chatRepository.getChat(chatId)
                .performOnBackgroundOutOnMain()
                .subscribe({
                    viewState.showCantSendHolder(false)
                    viewState.showAvatar(it.user.user_avatar)
                },{it.printStackTrace()}).call(compositeDisposable)
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

    override fun onImageClick(url: String) {
        viewState.openImageFullScreen(url)
    }

    private fun sendMessage(chatMessage: ChatMessage) {
        if (chatMessage.text.isNullOrBlank()) return
        viewState.apply { clearMessageInput() }
        chatRepository.sendChatMessage(chatId, userId, chatMessage)
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

    override fun onDestroy() {
        super.onDestroy()
        queryList?.removeChangeEventListener(changeEventListener)
    }
}

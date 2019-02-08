package com.example.ui.chat

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.ChatMessage
import com.example.data.models.UserChatMessage
import com.example.repository.ChatRepository
import com.example.ui.base.BasePresenter
import com.firebase.ui.firestore.SnapshotParser
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class ChatPresenter
@Inject constructor(
        private val appData: AppData,
        private val chatRepository: ChatRepository
) : BasePresenter<ChatContract.View>(), ChatContract.Presenter {

    lateinit var chatId: String

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        chatRepository.singInFirebase()
                .performOnBackgroundOutOnMain()
                .subscribe({
                    val chatMessageQuery = chatRepository.getChatMessageQuery(chatId)
                    val chatMessageParser = SnapshotParser { snapshot ->
                        snapshot.toObject(ChatMessage::class.java)!!.let {
                            UserChatMessage(it, appData.getUser().user_id == it.senderId)
                        }
                    }

                    viewState.apply { iniChatAdapter(chatMessageQuery, chatMessageParser) }
                }, {})
                .call(compositeDisposable)
    }

    override fun onSendTextMessageClick(message: String) {
        viewState.apply { clearMessageInput() }
        chatRepository.sendChatMessage(chatId, ChatMessage(text = message, senderId = appData.getUser().user_id))
                .performOnBackgroundOutOnMain()
                .subscribe({}, {})
                .call(compositeDisposable)
    }

    override fun onNewMessage() {
        viewState.scrollToLastPosition()
    }
}

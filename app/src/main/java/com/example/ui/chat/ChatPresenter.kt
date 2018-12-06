package com.example.ui.chat

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.ChatMessage
import com.example.data.models.UserChatMessage
import com.example.repository.ChatRepository
import com.example.repository.DummyRepository
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
    lateinit var userId: String

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        val chatMessageQuery = chatRepository.getChatMessageQuery(chatId)
        val chatMessageParser = SnapshotParser { snapshot ->
            snapshot.toObject(ChatMessage::class.java)!!.let {
                UserChatMessage(it, appData.uid == it.senderId)
            }
        }

        viewState.apply {
            iniChatAdapter(chatMessageQuery, chatMessageParser)
            setChatLabel("Тестовый чат")
        }
    }

    override fun onSendTextMessageClick(message: String) {
        viewState.apply { clearMessageInput() }

        chatRepository.sendChatMessage(chatId, userId, ChatMessage(text = message, senderId = appData.uid))
                .performOnBackgroundOutOnMain()
                .subscribe({}, {})
                .call(compositeDisposable)
    }

    override fun onNewMessage() {
        viewState.scrollToLastPosition()
    }
}

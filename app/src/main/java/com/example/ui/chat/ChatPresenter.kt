package com.example.ui.chat

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.ChatMessage
import com.example.repository.ChatRepository
import com.example.ui.base.BasePresenter
import com.firebase.ui.firestore.ClassSnapshotParser
import com.google.firebase.firestore.DocumentSnapshot
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
        val chatMessageQuery = chatRepository.getChatMessageQuery(chatId)
        val chatMessageParser = object : ClassSnapshotParser<ChatMessage>(ChatMessage::class.java) {
            override fun parseSnapshot(snapshot: DocumentSnapshot): ChatMessage {
                return super.parseSnapshot(snapshot).apply {
                    isMyMessage = appData.uid === this.senderId
                }
            }
        }
        viewState.apply {
            iniChatAdapter(chatMessageQuery, chatMessageParser)
        }
    }

    override fun onSendTextMessageClick(message: String) {

    }
}

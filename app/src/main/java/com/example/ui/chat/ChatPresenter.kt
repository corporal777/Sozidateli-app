package com.example.ui.chat

import com.arellomobile.mvp.InjectViewState
import com.example.repository.ChatRepository
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class ChatPresenter
@Inject constructor(
        private val chatRepository: ChatRepository
) : BasePresenter<ChatContract.View>(), ChatContract.Presenter {

    lateinit var chatId: String

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            iniChatAdapter(chatRepository.getChatMessageQuery(chatId))
        }
    }
}

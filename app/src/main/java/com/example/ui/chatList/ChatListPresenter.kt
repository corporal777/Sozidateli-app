package com.example.ui.chatList

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.UserChat
import com.example.repository.ChatRepository
import com.example.ui.base.BasePresenter
import performOnBackgroundOutOnMain
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class ChatListPresenter
@Inject constructor(private val chatRepository: ChatRepository
) : BasePresenter<ChatListContract.View>(), ChatListContract.Presenter{
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState?.apply {
        }
    }


    override fun onChatClick(userChat: UserChat) {
        userChat.userId?.let {userId->
            userChat.chatId?.let {
                viewState.openChat(userId,it)
            }
        }
    }

}

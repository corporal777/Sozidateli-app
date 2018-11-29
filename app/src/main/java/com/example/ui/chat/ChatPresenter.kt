package com.example.ui.chat

import android.util.Log
import call
import com.arellomobile.mvp.InjectViewState
import com.example.repository.ChatRepository
import com.example.ui.base.BasePresenter
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class ChatPresenter
@Inject constructor(
        private val chatRepository: ChatRepository
) : BasePresenter<ChatContract.View>(), ChatContract.Presenter {

    lateinit var chatId: String

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        chatRepository.subscribeChatMessages(chatId)
                .subscribe({ result ->
                    //Timber.tag("CHAT_T").d("${result.documents.map { it.data }}")
                }, {
                    Timber.tag("CHAT_T").d(Log.getStackTraceString(it))
                })
                .call(compositeDisposable)
    }
}

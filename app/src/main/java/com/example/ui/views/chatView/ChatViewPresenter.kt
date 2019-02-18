package com.example.ui.views.chatView

import call
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.data.AppData
import com.example.repository.ChatRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class ChatViewPresenter @Inject constructor(
        private val chatRepository: ChatRepository,
        private val appData: AppData
) : MvpPresenter<ChatViewContract.View>(), ChatViewContract.Presenter {

    private val compositeDisposable = CompositeDisposable()
    private var chatUnreadCountDisposable: Disposable? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showCounter(false)
        appData.onUserChange
                .performOnBackgroundOutOnMain()
                .subscribe(
                        {
                            unsubscribeChatUnreadCount()
                            if (it.value != null) subscribeChatUnreadCount()
                        },
                        {
                            unsubscribeChatUnreadCount()
                        })
                .call(compositeDisposable)
    }

    private fun subscribeChatUnreadCount() {
        chatUnreadCountDisposable = chatRepository.subscribeChatUnreadMessageCount()
                .performOnBackgroundOutOnMain()
                .subscribe({
                    viewState.apply {
                        if (it > 0) {
                            setChatCount(if (it > 99) "99+" else it.toString())
                            showCounter(true)
                        } else {
                            setChatCount("")
                            showCounter(false)
                        }
                    }
                }, {
                    viewState.showCounter(false)
                }).apply {
                    call(compositeDisposable)
                }
    }

    private fun unsubscribeChatUnreadCount() {
        chatUnreadCountDisposable?.apply {
            dispose()
            viewState.showCounter(false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}

package com.example.ui.views.chatView

import call
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.repository.ChatRepository
import io.reactivex.disposables.CompositeDisposable
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class ChatViewPresenter @Inject constructor(
        private val chatRepository: ChatRepository
) : MvpPresenter<ChatViewContract.View>(), ChatViewContract.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showCounter(false)
        chatRepository.subscribeChatUnreadMessageCount()
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
                })
                .call(compositeDisposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}

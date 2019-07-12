package com.example.ui.views.chatView

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.data.AppData
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class ChatViewPresenter @Inject constructor(
        private val appData: AppData
) : MvpPresenter<ChatViewContract.View>(), ChatViewContract.Presenter {

    private val compositeDisposable = CompositeDisposable()

    private var messagesCount = 0
    private var requestsCount = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.hideCounter()

        compositeDisposable += appData.chatMessageCountSubject
                .performOnBackgroundOutOnMain()
                .subscribe({
                    messagesCount = it
                    dispatchCounterUpdate()
                }, {
                    messagesCount = 0
                    dispatchCounterUpdate()
                })

        compositeDisposable += appData.chatRequestsCountSubject
                .performOnBackgroundOutOnMain()
                .subscribe({
                    requestsCount = it
                    dispatchCounterUpdate()
                }, {
                    requestsCount = 0
                    dispatchCounterUpdate()
                })
    }

    private fun dispatchCounterUpdate() {
        viewState.apply {
            val messages = messagesCount
            val requests = requestsCount
            val countLet = { count: Int -> if (count > COUNT_MAX) TEXT_IF_MORE_THAN_MAX else count.toString() }
            when {
                messages > 0 -> setMessagesCount(messages.let(countLet))
                requests > 0 -> setRequestsCount(requests.let(countLet))
                else -> hideCounter()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    companion object {
        private const val COUNT_MAX = 99
        private const val TEXT_IF_MORE_THAN_MAX = "99+"
    }
}

package com.example.ui.views.chatView

import com.examle.data.AppData
import com.example.common.BADGE_COUNT_MAX
import com.example.common.BADGE_TEXT_IF_MORE_THAN_MAX
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import moxy.MvpPresenter
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
            val countLet = { count: Int -> if (count > BADGE_COUNT_MAX) BADGE_TEXT_IF_MORE_THAN_MAX else count.toString() }
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
}

package com.example.ui.chatList

import com.example.data.AppData
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class ChatListTabsPresenter
@Inject constructor(private val appData: AppData) :
    BasePresenter<ChatListTabsContract.View>(appData), ChatListTabsContract.Presenter {


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += appData.chatRequestsCountSubject
            .performOnBackgroundOutOnMain()
            .subscribe({ viewState.setInvitesCount(it) }, { viewState.setInvitesCount(0) })

        compositeDisposable += appData.chatMessageCountSubject
            .performOnBackgroundOutOnMain()
            .subscribe({ viewState.setChatsCount(it) }, { viewState.setChatsCount(0) })
    }


    override fun onFabAddChatClick() = viewState.openSearch()

    companion object {
        private const val TAB_CHATS = 0
        private const val TAB_INVITES = 1
    }
}

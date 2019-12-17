package com.example.ui.chatList

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class ChatListTabsPresenter
@Inject constructor(
        private val appData: AppData
) : BasePresenter<ChatListTabsContract.View>(), ChatListTabsContract.Presenter {

    private var selectedTab = TAB_CHATS

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += appData.chatRequestsCountSubject
                .performOnBackgroundOutOnMain()
                .subscribe({ viewState.setInvitesCount(100) }, { viewState.setInvitesCount(0) })
    }

    override fun attachView(view: ChatListTabsContract.View?) {
        super.attachView(view)
        viewState.selectTab(selectedTab)
    }

    override fun onChatsSelected() {
        selectedTab = TAB_CHATS
    }

    override fun onInvitesSelected() {
        selectedTab = TAB_INVITES
    }

    companion object {
        private const val TAB_CHATS = 0
        private const val TAB_INVITES = 1
    }
}

package com.example.ui.chatList.invites

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.ChatListDataItem
import com.example.data.models.ChatModel
import com.example.data.models.Message.MessageType
import com.example.data.models.UserChat
import com.example.events.OnSocketConnectEvent
import com.example.extensions.buildList
import com.example.repository.ChatRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationDataSourceFactory
import com.example.util.pagination.PaginationResponse
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class InviteListPresenter
@Inject constructor(
        private val chatRepository: ChatRepository,
        private val appData: AppData
) : BasePresenter<InviteListContract.View>(), InviteListContract.Presenter {

    private val invitesPagination = PaginationDataSourceFactory { limit, offset ->
        chatRepository.getChats(
                mapOf(ChatModel.CHAT_SORT to "desc", ChatModel.CHAT_LIMIT to limit, ChatModel.CHAT_OFFSET to offset,
                        ChatModel.CHAT_BINDS to "users,event,bans", ChatModel.CHAT_INVITED_USER_STATUS to "pending")
        ).map { response ->
            val items = response.data.map { ChatListDataItem.Invite(UserChat(it.id, it.binds?.users?.first { us -> us.id != appData.getId() }!!,
                    it.createdDate?: "", it.binds.lastUnreadMessage?.message, it.binds.lastUnreadMessage?.createdDate,
                    if (it.binds.lastUnreadMessage?.file == null) MessageType.TEXT else MessageType.IMAGE,
                    it.binds.lastUnreadMessage?.acknowledge?.get(0)?.user, null, it.binds.lastUnreadMessage?.id.toString(),
                    false, it.isInInvites(appData.getId()), it.isWaitForAcceptInvites(), it.isBannedByRecipient(appData.getId()), it.isBannedByYou(appData.getId()), it.isEventChat(),
                    it.binds.event?.id.toString(), 0)) }
                    //.plus(response.response.favorites.map { ChatListDataItem.User(it) })
            PaginationResponse(response.totalCount, items)
        }
        /*chatRepository.loadInvitesList(limit, offset).map { response ->
            response.totalCount?.let { appData.chatRequestsCount = it }
            PaginationResponse(response.totalCount, response.data.map { ChatListDataItem.Invite(it) })
        }*/
    }.buildList()

    private var firstLaunch = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        EventBus.getDefault().register(this)

        viewState.setInvitesData(List(20) { null })
        compositeDisposable += appData.chatRequestsCountSubject
                .performOnBackgroundOutOnMain()
                .subscribe({ invitesPagination.invalidate() }, { invitesPagination.invalidate() })

        compositeDisposable += Observable.create(invitesPagination)
                .subscribe({
                    dispatchInvitesListUpdate(it)
                }, { it.printStackTrace() })
    }

    override fun attachView(view: InviteListContract.View?) {
        super.attachView(view)
        if (firstLaunch) firstLaunch = false
        else invitesPagination.invalidate()
    }

    private fun dispatchInvitesListUpdate(data: List<ChatListDataItem>) {
        val invites = mutableListOf<UserChat>()
        data.forEach { if (it is ChatListDataItem.Invite) invites.add(it.userChat.apply { unreadMessageCount = 1 }) }
        viewState.setInvitesData(invites)
    }

    override fun onChatClick(userChat: UserChat) = viewState.openChat(userChat.id, userChat.user.fullName)

    override fun onItemTake(position: Int) {
        invitesPagination.onItemTake(position)
    }

    override fun onRefreshRequest() {
        invitesPagination.invalidate()
    }

    @Subscribe
    fun onSocketConnect(event: OnSocketConnectEvent) {
        invitesPagination.invalidate()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}

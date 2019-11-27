package com.example.ui.subevent.users

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.user.User
import com.example.extensions.build
import com.example.holders.UserItem
import com.example.repository.ChatRepository
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationDataSourceFactory
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class SubeventUserListPresenter @Inject constructor(
        private val eventRepository: EventRepository,
        private val chatRepository: ChatRepository
) : BasePresenter<SubeventUserListContract.View>(), SubeventUserListContract.Presenter {

    lateinit var event: String
    lateinit var subevent: String

    private val pagination = PaginationDataSourceFactory { limit, offset ->
        val eventId = event
        val subeventId = subevent
        eventRepository.getSubeventUsers(eventId, subeventId, limit, offset)
    }.map { UserItem(it.user_id, it.fullName, it.user_avatar, { onUserClick(it) }) }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        compositeDisposable += pagination.build()
                .withLoadingDialog(viewState)
                .performOnBackgroundOutOnMain()
                .subscribeSimple {
                    viewState.apply {
                        if (it.isEmpty()) showEmptyListPlaceholder()
                        else viewState.setUsers(it)
                    }
                }
    }

    override fun onUserClick(user: User) {
        compositeDisposable += chatRepository.startChat(user.user_id.toString())
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.openChat(user.fullName, user.user_avatar, it.chat_id.toString())
                }, { it.printStackTrace() })
    }
}

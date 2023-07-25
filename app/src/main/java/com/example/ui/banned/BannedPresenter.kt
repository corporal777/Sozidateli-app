package com.example.ui.banned

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.BannedUsersModel.Companion.BANNED_BINDS
import com.example.data.models.BannedUsersModel.Companion.BANNED_LIMIT
import com.example.data.models.BannedUsersModel.Companion.BANNED_OFFSET
import com.example.data.models.BannedUsersModel.Companion.BANNED_SORT_TYPE
import com.example.data.models.UserChat
import com.example.extensions.buildList
import com.example.repository.ChatRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationListGroupAdapter
import com.example.util.pagination.observable.PaginationDataSourceFactory
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class BannedPresenter
@Inject constructor(
    private val chatRepository: ChatRepository,
    appData: AppData
) : BasePresenter<BannedContract.View>(appData), BannedContract.Presenter,
    PaginationListGroupAdapter.OnItemTakeCallback {

    private var firstLaunch = true

    private val pagination = PaginationDataSourceFactory { limit, offset ->
        chatRepository.bannedList(
            mapOf(
                BANNED_SORT_TYPE to "desc",
                BANNED_LIMIT to limit,
                BANNED_OFFSET to offset,
                BANNED_BINDS to "user"
            )
        )
    }.buildList(enablePlaceholders = false, initialSize = 30)

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setItems(List(20) { null })
        compositeDisposable += Observable.create(pagination)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onNext = { viewState.setItems(it) }
            )
    }

    override fun attachView(view: BannedContract.View?) {
        super.attachView(view)
        if (firstLaunch) firstLaunch = false
        else pagination.invalidate()
    }

    override fun onUserClick(userChat: UserChat) {
        viewState.openUserInfo(userChat.user.id.toString())
    }

    override fun onUnblockLick(userChat: UserChat) {
        compositeDisposable += chatRepository.deleteBan(userChat.id)
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribe({ pagination.invalidate() }, { it.printStackTrace() })
    }

    override fun onItemTake(position: Int) = pagination.onItemTake(position)
    override fun onRefreshRequest() = pagination.invalidate()
}

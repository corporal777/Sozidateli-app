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
import com.example.util.pagination.PaginationDataSourceFactory
import com.example.util.pagination.PaginationListGroupAdapter
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject
import kotlin.math.abs

@InjectViewState
class BannedPresenter
@Inject constructor(
        private val chatRepository: ChatRepository,
        appData: AppData
) : BasePresenter<BannedContract.View>(appData), BannedContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback {

    private var firstLaunch = true
    private var mDy = 0f

    private val pagination = PaginationDataSourceFactory { limit, offset ->
        chatRepository.bannedList(mapOf(
                BANNED_SORT_TYPE to "desc",
                BANNED_LIMIT to limit,
                BANNED_OFFSET to offset,
                BANNED_BINDS to "user"
        ))
    }.buildList()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setAppBarElevation(0f)
        viewState.setItems(List(20) { null })
        compositeDisposable += Observable.create(pagination)
                .performOnBackgroundOutOnMain()
                .subscribe({
                    viewState.setItems(it)
                }, {
                    it.printStackTrace()
                })
    }

    override fun attachView(view: BannedContract.View?) {
        super.attachView(view)
        viewState.setAppBarElevation(mDy)
//        if (firstLaunch) firstLaunch = false
//        else pagination.invalidate()
    }

    fun changeScrollingOffset(value : Int){
        mDy = abs(value / 10f)
        viewState.setAppBarElevation(abs(mDy / 10f))
    }

    override fun onUserClick(userChat: UserChat) {
        viewState.openUserInfo(userChat.user.id.toString())
    }

    override fun onUnblockLick(userChat: UserChat) {
        compositeDisposable += chatRepository.deleteBan(userChat.id)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({ pagination.invalidate() }, { it.printStackTrace() })
    }

    override fun onItemTake(position: Int) {
        pagination.onItemTake(position)
    }

    override fun onRefreshRequest() {
        pagination.invalidate()
    }
}

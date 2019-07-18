package com.example.ui.banned

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.UserChat
import com.example.extensions.buildList
import com.example.repository.ChatRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationDataSourceFactory
import com.example.util.pagination.PaginationListGroupAdapter
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class BannedPresenter
@Inject constructor(
        private val chatRepository: ChatRepository
) : BasePresenter<BannedContract.View>(), BannedContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback {

    private val pagination = PaginationDataSourceFactory { limit, offset ->
        chatRepository.loadBannedList(limit, offset)
    }.buildList()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += Observable.create(pagination)
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.setItems(it)
                }, {
                    it.printStackTrace()
                })
    }

    override fun onUserClick(userChat: UserChat) {
        viewState.openUserInfo(userChat.user.user_id.toString())
    }

    override fun onItemTake(position: Int) {
        pagination.onItemTake(position)
    }
}

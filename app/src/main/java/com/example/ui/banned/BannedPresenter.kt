package com.example.ui.banned

import com.example.data.AppData
import com.example.data.models.UserChat
import com.example.repository.ChatRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withProgressBarDialogLoading
import javax.inject.Inject

@InjectViewState
class BannedPresenter
@Inject constructor(
    private val chatRepository: ChatRepository,
    appData: AppData
) : BasePresenter<BannedContract.View>(appData), BannedContract.Presenter {

    private var firstLaunch = true

//    private val pagination = PaginationDataSourceFactory { limit, offset ->
//        chatRepository.bannedList(
//            mapOf(
//                BANNED_SORT_TYPE to "desc",
//                BANNED_LIMIT to limit,
//                BANNED_OFFSET to offset,
//                BANNED_BINDS to "user"
//            )
//        )
//    }.buildList(enablePlaceholders = false, initialSize = 30)

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
//        viewState.setItems(List(20) { null })
//        compositeDisposable += Observable.create(pagination)
//            .performOnBackgroundOutOnMain()
//            .subscribeSimple(
//                onError = { onReceiveError(it) },
//                onNext = { viewState.setItems(it) }
//            )
    }


    override fun onUserClick(userChat: UserChat) {
        viewState.openUserInfo(userChat.user.id.toString())
    }

    override fun onUnblockLick(userChat: UserChat) {
        compositeDisposable += chatRepository.deleteBan(userChat.id)
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribe({  }, { it.printStackTrace() })
    }

    override fun onRefreshRequest() {}
}

package com.example.ui.state.maxNew.base

import com.example.data.AppData
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.Utils
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain

abstract class BaseMaxStatePresenter<V : BaseMaxStateContract.View>(
    private val appData: AppData,
    private val userRepository: UserRepository,
) : BasePresenter<V>(appData), BaseMaxStateContract.Presenter {

    var screen = -1


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.buttonNextEnabled(false)
    }

    fun checkNextScreen(){
        compositeDisposable += userRepository.checkUserProfileSingle()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { viewState.goToNextScreen(Utils.maxStateScreen(getUserData())) },
                onSuccess = { viewState.goToNextScreen(Utils.maxStateScreenNew(it)) }
            )
    }

    override fun onClickClose() = viewState.setClickClose(screen)
    override fun onShowMaxStateDone() = viewState.showMaxStateDone(screen)

}
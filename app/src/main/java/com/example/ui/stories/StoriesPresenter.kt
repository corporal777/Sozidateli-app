package com.example.ui.stories

import com.example.data.AppData
import com.example.repository.AuthRepository
import com.example.ui.base.BasePresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import moxy.InjectViewState
import moxy.MvpPresenter
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class StoriesPresenter
@Inject constructor(
    private val appData: AppData,
    private val authRepository: AuthRepository
) : BasePresenter<StoriesContract.View>(appData), StoriesContract.Presenter  {


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += authRepository.getTemporaryToken()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onComplete = {}
            )
    }

    override fun onStoriesComplete() {
        appData.isStoriesShown = true
        viewState.hideStories()
    }
}

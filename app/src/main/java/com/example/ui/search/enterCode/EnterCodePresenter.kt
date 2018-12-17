package com.example.ui.search.enterCode

import call
import com.arellomobile.mvp.InjectViewState
import com.example.repository.DummyRepository
import com.example.ui.base.BasePresenter
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class EnterCodePresenter
@Inject constructor(private val dummyRepository: DummyRepository) : BasePresenter<EnterCodeContract.View>(), EnterCodeContract.Presenter{
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
    }

    override fun onSearchClick(code: String) {
        dummyRepository.loadRecommendations(1,0)
                .performOnBackgroundOutOnMain()
                .subscribe({
                    viewState.openEvent(it.data[0])
                },{}).call(compositeDisposable)
    }
}

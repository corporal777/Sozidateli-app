package com.example.ui.views.suggestFieldView

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.data.AppData
import com.example.repository.DataDataRepository
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import performOnBackgroundOutOnMain
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class SuggestFieldViewPresenter @Inject constructor(
        private val dataDataRepository: DataDataRepository
) : MvpPresenter<SuggestFieldViewContract.View>(), SuggestFieldViewContract.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
    }

    override fun onQueryChange(query: String) {
        compositeDisposable.clear()
        Single.timer(350, TimeUnit.MILLISECONDS)
                .flatMap { dataDataRepository.suggestCity(query) }
                .performOnBackgroundOutOnMain()
                .subscribe({
                    viewState.setSuggested(it.suggestions)
                }, {
                    it.printStackTrace()
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun onError(errors: List<String>) {

    }
}

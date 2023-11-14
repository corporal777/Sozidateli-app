package com.example.ui.views.suggestFieldView.town

import com.example.data.models.SearchTown
import com.example.repository.CommonRepository
import io.reactivex.Maybe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import moxy.InjectViewState
import moxy.MvpPresenter
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class SearchTownBottomSheetPresenter @Inject constructor(
    private val commonRepository: CommonRepository,
) : MvpPresenter<SearchTownBottomSheetContract.View>(),
    SearchTownBottomSheetContract.Presenter {

    var region = ""
    var type = ""

    private val listTowns = arrayListOf<SearchTown>()
    private val compositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += commonRepository.getFilterTowns(type, region)
            .performOnBackgroundOutOnMain()
            .doOnSuccess { listTowns.addAll(it) }
            .subscribeBy {
                viewState.setTowns(it)
            }
    }

    override fun onTownChange(town: String) {
        compositeDisposable += Maybe.defer {
            if (town.isNullOrBlank()) Maybe.just(listTowns)
            else {
                val list = listTowns.filter { x -> x.name.contains(town, true) }
                if (list.isNullOrEmpty()) Maybe.just(List(1) { null })
                else Maybe.just(list)
            }
        }
            .performOnBackgroundOutOnMain()
            .subscribe {
                viewState.setTowns(it)
            }
    }

    override fun onTownSelected(town: String?) {
        compositeDisposable += Maybe.defer<SearchTown?> {
            Maybe.just(listTowns.find { x -> x.name == town })
        }
            .performOnBackgroundOutOnMain()
            .subscribe {
                viewState.performOnItemSelected(it)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }


}
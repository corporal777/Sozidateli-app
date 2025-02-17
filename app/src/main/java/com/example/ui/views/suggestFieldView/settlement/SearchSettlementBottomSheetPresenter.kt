package com.example.ui.views.suggestFieldView.settlement

import android.util.Log
import com.example.data.models.SearchRegion
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
class SearchSettlementBottomSheetPresenter @Inject constructor(
    private val commonRepository: CommonRepository,
) : MvpPresenter<SearchSettlementBottomSheetContract.View>(),
    SearchSettlementBottomSheetContract.Presenter {

    private val listSettlements = arrayListOf<SearchRegion>()
    private val compositeDisposable = CompositeDisposable()
    var region = ""

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += commonRepository.getSettlements(region)
            .performOnBackgroundOutOnMain()
            .doOnSuccess { listSettlements.addAll(it) }
            .subscribeBy(
                onError = {
                    it.printStackTrace()
                    viewState.setSettlements(emptyList())
                },
                onSuccess = {
                    viewState.setSettlements(it)
                })
    }

    override fun onSettlementChange(settlement: String){
        compositeDisposable += Maybe.defer {
            if (settlement.isNullOrBlank()) Maybe.just(listSettlements)
            else {
                val list = listSettlements.filter { x -> x.name.contains(settlement, true) }
                if (list.isNullOrEmpty()) Maybe.just(List(1) { null })
                else Maybe.just(list)
            }
        }
            .performOnBackgroundOutOnMain()
            .subscribe {
                viewState.setSettlements(it)
            }
    }

    override fun onSettlementSelected(settlement: String?) {
        compositeDisposable += Maybe.defer<SearchRegion?> {
            Maybe.just(listSettlements.find { x -> x.name == settlement })
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
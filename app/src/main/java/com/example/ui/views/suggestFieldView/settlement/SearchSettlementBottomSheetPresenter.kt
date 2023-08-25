package com.example.ui.views.suggestFieldView.settlement

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.data.AppData
import com.example.data.models.SearchRegion
import com.example.repository.CommonRepository
import com.example.ui.views.suggestFieldView.region.SearchRegionBottomSheetContract
import io.reactivex.Maybe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
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
            .subscribeBy {
                viewState.setSettlements(it)
            }
    }

    override fun onSettlementChange(settlement: String){
        compositeDisposable += Maybe.fromCallable {
            if (settlement.isNullOrBlank()) listSettlements
            else listSettlements.filter { x -> x.name.contains(settlement, true) }
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
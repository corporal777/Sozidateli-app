package com.example.ui.views.suggestFieldView.settlement

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.data.AppData
import com.example.data.models.SearchRegion
import com.example.repository.CommonRepository
import com.example.ui.base.BaseContract
import com.example.ui.views.suggestFieldView.region.SearchRegionBottomSheetContract
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
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
            .subscribeBy(
                onError = {
                    it.printStackTrace()
                    viewState.setSettlements(emptyList())
                },
                onSuccess = {
                    viewState.setSettlements(it)
                    Log.e("SIZE ", it.size.toString())
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
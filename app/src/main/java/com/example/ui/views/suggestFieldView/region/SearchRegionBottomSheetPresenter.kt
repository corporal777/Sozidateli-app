package com.example.ui.views.suggestFieldView.region

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.data.models.NewEventFormat
import com.example.data.models.SearchRegion
import com.example.repository.CommonRepository
import com.example.repository.EventRepository
import com.example.ui.views.suggestFieldView.format.EventFormatBottomSheetContract
import io.reactivex.Maybe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import performOnBackgroundOutOnMain
import javax.inject.Inject
import com.example.data.AppData


@InjectViewState
class SearchRegionBottomSheetPresenter @Inject constructor(
    private val appData: AppData,
    private val commonRepository: CommonRepository,
) : MvpPresenter<SearchRegionBottomSheetContract.View>(),
    SearchRegionBottomSheetContract.Presenter {

    private val listRegions = arrayListOf<SearchRegion>()
    private val compositeDisposable = CompositeDisposable()


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += commonRepository.getFilterRegions()
            .performOnBackgroundOutOnMain()
            .doOnSuccess { listRegions.addAll(it) }
            .subscribeBy {
                viewState.setRegions(it)
            }
    }

    override fun onRegionChange(region: String) {
        compositeDisposable += Maybe.fromCallable {
            if (region.isNullOrBlank()) listRegions
            else listRegions.filter { x -> x.name.contains(region, true) }
        }
            .performOnBackgroundOutOnMain()
            .subscribe {
                viewState.setRegions(it)
            }
    }

    override fun onRegionSelected(region: String?) {
        compositeDisposable += Maybe.defer<SearchRegion?> {
            Maybe.just(listRegions.find { x -> x.name == region })
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
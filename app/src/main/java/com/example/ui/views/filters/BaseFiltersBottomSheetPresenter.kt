package com.example.ui.views.filters

import com.example.data.models.EventNew
import com.example.data.models.InterestNew
import com.example.extensions.groupByNotNull
import com.example.repository.CommonRepository
import com.example.repository.EventRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import moxy.MvpPresenter
import performOnBackgroundOutOnMain

abstract class BaseFiltersBottomSheetPresenter<V : BaseFiltersBottomSheetContract.View>(
    val commonRepository: CommonRepository,
    val eventRepository: EventRepository
) : MvpPresenter<V>(), BaseFiltersBottomSheetContract.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += eventRepository.getEventFormatsList(
            mapOf(EventNew.EVENT_LIMIT to 100, EventNew.EVENT_OFFSET to 0)
        )
            .performOnBackgroundOutOnMain()
            .subscribeBy { viewState.initFormats(it) }
        compositeDisposable += commonRepository.getInterests()
            .map { interests -> interests.groupByNotNull { child -> interests.firstOrNull { it.id == child.parent } } }
            .performOnBackgroundOutOnMain()
            .subscribeBy { viewState.initInterests(it) }
    }

    fun findInterest(id: Int?, interests: Collection<InterestNew>): InterestNew? {
        return id?.let { interests.find { it.id == id } }
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}
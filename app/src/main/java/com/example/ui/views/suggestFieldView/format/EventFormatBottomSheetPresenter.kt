package com.example.ui.views.suggestFieldView.format

import androidx.annotation.ArrayRes
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.data.models.EventNew
import com.example.data.models.NewEventFormat
import com.example.repository.EventRepository
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class EventFormatBottomSheetPresenter @Inject constructor(
    private val eventRepository: EventRepository,
) : MvpPresenter<EventFormatBottomSheetContract.View>(),
    EventFormatBottomSheetContract.Presenter {

    val listFormats = arrayListOf(getCustomFormat(null))
    private val compositeDisposable = CompositeDisposable()


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (listFormats.isNullOrEmpty()) {
            compositeDisposable += eventRepository.getEventFormatsList(mapOf(EventNew.EVENT_LIMIT to 100, EventNew.EVENT_OFFSET to 0))
                .doOnSuccess { listFormats.addAll(it) }
                .performOnBackgroundOutOnMain()
                .subscribeBy {
                    viewState.setFormats(listFormats)
                }
        } else viewState.setFormats(listFormats)
    }


    override fun onFormatChange(format: String) {
        compositeDisposable += Maybe.fromCallable {
            listFormats[0] = getCustomFormat(format)
            if (format.isNullOrBlank()) listFormats
            else listFormats.filter { x -> x.name?.contains(format, true) == true }
        }
            .performOnBackgroundOutOnMain()
            .subscribe {
                viewState.setFormats(it)
            }
    }

    override fun onFormatSelected(format: String?) {
        compositeDisposable += Maybe.defer<NewEventFormat?> {
            val item = listFormats.findLast { x -> x.name == format }
            if (item == null) Maybe.just(getCustomFormat(null))
            else Maybe.just(item)
        }
            .performOnBackgroundOutOnMain()
            .subscribe {
                viewState.performOnItemSelected(it)
            }
    }

    private fun getCustomFormat(format : String?): NewEventFormat {
        return NewEventFormat(null, format, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}
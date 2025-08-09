package com.example.ui.views.suggestFieldView.format

//@InjectViewState
//class EventFormatBottomSheetPresenter @Inject constructor(
//    private val eventRepository: EventRepository,
//) : MvpPresenter<EventFormatBottomSheetContract.View>(),
//    EventFormatBottomSheetContract.Presenter {
//
//    val listFormats = arrayListOf(getCustomFormat(null))
//    private val compositeDisposable = CompositeDisposable()
//
//
//    override fun onFirstViewAttach() {
//        super.onFirstViewAttach()
//        if (listFormats.isNullOrEmpty() || listFormats.size == 1) {
//            compositeDisposable += eventRepository.getActiveEventFormatsList()
//                .doOnSuccess { listFormats.addAll(it) }
//                .performOnBackgroundOutOnMain()
//                .subscribeBy {
//                    viewState.setFormats(listFormats)
//                }
//        } else viewState.setFormats(listFormats)
//    }
//
//
//    override fun onFormatChange(format: String) {
//        compositeDisposable += Maybe.fromCallable {
//            listFormats[0] = getCustomFormat(format)
//            if (format.isNullOrBlank()) listFormats
//            else listFormats.filter { x -> x.name?.contains(format, true) == true }
//        }
//            .performOnBackgroundOutOnMain()
//            .subscribe {
//                viewState.setFormats(it)
//            }
//    }
//
//    override fun onFormatSelected(format: String?) {
//        compositeDisposable += Maybe.defer<NewEventFormat?> {
//            val item = listFormats.findLast { x -> x.name == format }
//            if (item == null) Maybe.just(getCustomFormat(null))
//            else Maybe.just(item)
//        }
//            .performOnBackgroundOutOnMain()
//            .subscribe {
//                viewState.performOnItemSelected(it)
//            }
//    }
//
//    private fun getCustomFormat(format : String?): NewEventFormat {
//        return NewEventFormat(null, format, 0, true)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        compositeDisposable.clear()
//    }
//
//}
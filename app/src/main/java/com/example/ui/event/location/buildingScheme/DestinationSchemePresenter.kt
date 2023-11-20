package com.example.ui.event.location.buildingScheme

import android.content.Context
import com.example.data.AppData
import com.example.data.models.Place
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.pdfToUri
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withProgressBarLoading
import javax.inject.Inject
import kotlin.math.abs

@InjectViewState
class DestinationSchemePresenter
@Inject constructor(
    private val appData: AppData,
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository,
    private val context: Context
) : BasePresenter<DestinationSchemeContract.View>(appData),
    DestinationSchemeContract.Presenter {

    var eventId = ""
    private var scrollValue = 0

    override fun attachView(view: DestinationSchemeContract.View?) {
        super.attachView(view)
        viewState.setAppBarShadow(abs(scrollValue / 10f))
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += eventRepository.getEventDetails(eventId)
            .map { it.event.binds?.destinationScheme?.map { s -> s.toPlace() } }
            .doOnSuccess { checkPlaceImages(it) }
            .performOnBackgroundOutOnMain()
            .withProgressBarLoading(viewState)
            .subscribeSimple {
                if (!it.isNullOrEmpty()) viewState.setScheme(it, 0)
            }
    }

    override fun onImageClick(url: String?) {
        if (!url.isNullOrEmpty()) viewState.showImage(url)
    }

    override fun onScrollChangeOffset(scroll: Int) {
        scrollValue = scroll
        viewState.setAppBarShadow(abs(scrollValue / 10f))
    }

    private fun checkPlaceImages(list: List<Place>?){
        list?.forEachIndexed { index, place ->
            if (!place.image.isNullOrEmpty()) {
                if (place.image!!.contains("pdf")){
                    place.image = pdfToUri(place.image?:"", context, index).toString()
                }
            }
        }
    }
}
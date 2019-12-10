package com.example.ui.event.speakers

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.Speaker
import com.example.extensions.buildList
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationDataSourceFactory
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class EventSpeakersPresenter
@Inject constructor(
        private val appData: AppData,
        private val eventRepository: EventRepository,
        private val userRepository: UserRepository
) : BasePresenter<EventSpeakersContract.View>(), EventSpeakersContract.Presenter {

    lateinit var eventId: String

    val pagination = PaginationDataSourceFactory { limit, offset -> eventRepository.getEventSpeakers(eventId, limit, offset) }
            .buildList()

    private var firstLaunch = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += Observable.create(pagination)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    val uid = appData.getUser().user_id
                    it.forEach { speaker -> speaker.user.isCurrentUser = speaker.user.user_id == uid }
                    viewState.apply { setData(it) }
                }
    }

    override fun attachView(view: EventSpeakersContract.View?) {
        super.attachView(view)
        if (firstLaunch) firstLaunch = false
        else pagination.invalidate()
    }

    override fun onSpeakerClick(speaker: Speaker) = viewState.showSpeaker(speaker)

    override fun onSpeakerFavoriteChangeClick(speaker: Speaker) {
        val id = speaker.user.user_id.toString()
        val request = if (!speaker.user.is_in_favorite) userRepository.addToFavorite(id)
        else userRepository.removeFromFavorite(id)

        compositeDisposable += request.performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple({
                    viewState.showRequestErrorMessage()
                }) {
                    speaker.user.is_in_favorite = !speaker.user.is_in_favorite
                    viewState.updateSpeaker(speaker)
                }
    }

    override fun onItemTake(position: Int) {
        pagination.onItemTake(position)
    }

    override fun onRefreshRequest() {
        pagination.invalidate()
    }
}

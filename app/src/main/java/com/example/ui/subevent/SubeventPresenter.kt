package com.example.ui.subevent

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Speaker
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class SubeventPresenter @Inject constructor(
        private val eventRepository: EventRepository,
        private val userRepository: UserRepository
) : BasePresenter<SubeventContract.View>(), SubeventContract.Presenter {

    lateinit var event: String
    lateinit var subevent: String

    private var firstLoading = true

    override fun attachView(view: SubeventContract.View?) {
        super.attachView(view)
        val eventId = event
        val subeventId = subevent
        compositeDisposable += eventRepository.getSubevent(eventId, subeventId)
                .let {
                    if (firstLoading) it.withLoadingDialog(viewState)
                    else it
                }
                .performOnBackgroundOutOnMain()
                .subscribeSimple {
                    if (firstLoading) viewState.setData(it)
                    firstLoading = false
                    viewState.setSpeakers(it.speakers)
                }
    }

    override fun onSpeakerClick(speaker: Speaker) {
        viewState.showSpeakerProfile(speaker)
    }

    override fun onSpeakerChangeSubscriptionClick(speaker: Speaker) {
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
}

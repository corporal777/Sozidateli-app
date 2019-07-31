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

    var event: Int = ID_INVALID
    var subevent: Int = ID_INVALID

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        val eventId = event
        val subeventId = subevent
        if (eventId == ID_INVALID || subeventId == ID_INVALID) throw IllegalArgumentException("Invalid id: event: $eventId, subeventId: $subeventId")
        compositeDisposable += eventRepository.getSubevent(eventId, subeventId)
                .withLoadingDialog(viewState)
                .performOnBackgroundOutOnMain()
                .subscribe({
                    viewState.setData(it)
                }, {

                })
    }

    override fun onSpeakerClick(speaker: Speaker) {
        viewState.showSpeakerProfile(speaker)
    }

    override fun onSpeakerChangeSubscriptionClick(speaker: Speaker) {
        val id = speaker.id.toString()
        compositeDisposable += (if (speaker.isInFavorite) userRepository.removeFromFavorite(id)
        else userRepository.addToFavorite(id))
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.updateSpeaker(speaker.apply { isInFavorite = !isInFavorite })
                }, {
                    it.printStackTrace()
                })
    }

    override fun onOpenUserListClick() {
        viewState.openUserList(event, subevent)
    }

    companion object {
        private const val ID_INVALID = -1
    }
}

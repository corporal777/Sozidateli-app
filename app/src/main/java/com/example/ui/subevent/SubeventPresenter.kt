package com.example.ui.subevent

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.AddToFavoriteEntityModel
import com.example.data.bodies.AddToFavoriteModel
import com.example.data.models.*
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class SubeventPresenter @Inject constructor(
        private val appData: AppData,
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
        compositeDisposable += eventRepository.getEventActivityDetail(subeventId)
                .let {
                    if (firstLoading) it.withLoadingDialog(viewState)
                    else it
                }
                .performOnBackgroundOutOnMain()
                .subscribeSimple {
                    if (firstLoading) viewState.setData(it)
                    firstLoading = false

                    val uid = appData.getId()
                    it.binds?.member?.forEach { speaker -> speaker.binds?.user?.isCurrentUser = speaker.user == uid }
                    viewState.setSpeakers(it.binds?.member?: emptyList())
                }
    }

    override fun onSpeakerClick(speaker: MemberModel) {
        viewState.showSpeakerProfile(speaker)
    }

    override fun onSpeakerChangeSubscriptionClick(speaker: MemberModel) {
        val id = speaker.user.toString()
        if (speaker.binds?.user?.binds?.userFavorite == null)
            compositeDisposable += eventRepository.addToFavorites(AddToFavoriteModel(appData.getId(), AddToFavoriteEntityModel(AddToFavoriteEntityModel.FAVORITE_SPEAKER, id.toInt())))
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribeSimple({
                        viewState.showRequestErrorMessage()
                    }) {
                        speaker.binds?.user?.binds?.userFavorite = EventUserFavorite(it.id, it.user)
                        viewState.updateSpeaker(speaker)
                    }
        else
            compositeDisposable += eventRepository.deleteFromFavorite(speaker.binds.user.binds?.userFavorite?.id.toString())
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribeSimple({
                        viewState.showRequestErrorMessage()
                    }) {
                        speaker.binds.user.binds?.userFavorite = null
                        viewState.updateSpeaker(speaker)
                    }
    }

    override fun onSubeventChangeSubscriptionClick(subevent: EventActivityModel) {
        val id = subevent.id.toString()
        if (subevent.binds?.userFavorite == null)
            compositeDisposable += eventRepository.addToFavorites(AddToFavoriteModel(appData.getId(), AddToFavoriteEntityModel(AddToFavoriteEntityModel.FAVORITE_SUB_EVENT, id.toInt())))
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribeSimple({
                        viewState.showRequestErrorMessage()
                    }) {
                        subevent.binds?.userFavorite = EventUserFavorite(it.id, it.user)
                        viewState.setData(subevent)
                    }
        else
            compositeDisposable += eventRepository.deleteFromFavorite(subevent.binds.userFavorite?.id.toString())
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribeSimple({
                        viewState.showRequestErrorMessage()
                    }) {
                        subevent.binds.userFavorite = null
                        viewState.setData(subevent)
                    }
    }
}

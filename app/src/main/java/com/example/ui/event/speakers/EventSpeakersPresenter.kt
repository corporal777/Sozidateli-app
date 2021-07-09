package com.example.ui.event.speakers

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.AddToFavoriteEntityModel
import com.example.data.bodies.AddToFavoriteModel
import com.example.data.models.EventUserFavorite
import com.example.data.models.MemberModel
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

    val pagination = PaginationDataSourceFactory { limit, offset -> eventRepository.getSpeakers(
    mapOf(MemberModel.MEMBER_EVENT to eventId, MemberModel.MEMBER_ROLE to MemberModel.MEMBER_ROLE_SPEAKER,
    MemberModel.MEMBER_LIMIT to limit, MemberModel.MEMBER_OFFSET to offset, MemberModel.MEMBER_BINDS to /*"user,userFavorite"*/"user")) }
            .buildList()

    private var firstLaunch = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += Observable.create(pagination)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    val uid = appData.getId()
                    it.forEach { speaker -> speaker.binds?.user?.isCurrentUser = speaker.binds?.user?.id == uid }
                    viewState.apply { setData(it) }
                }
    }

    override fun attachView(view: EventSpeakersContract.View?) {
        super.attachView(view)
        if (firstLaunch) firstLaunch = false
        else pagination.invalidate()
    }

    override fun onSpeakerClick(speaker: MemberModel) = viewState.showSpeaker(speaker)

    override fun onSpeakerFavoriteChangeClick(speaker: MemberModel) {
        val id = speaker.user.toString()/*speaker.user.user_id.toString()*/
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

    override fun onItemTake(position: Int) {
        pagination.onItemTake(position)
    }

    override fun onRefreshRequest() {
        pagination.invalidate()
    }
}

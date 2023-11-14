package com.example.ui.event.favorite.subevent

import com.example.data.AppData
import com.example.data.bodies.AddToFavoriteEntityModel
import com.example.data.bodies.AddToFavoriteModel
import com.example.data.models.EventActivityModel
import com.example.data.models.EventUserFavorite
import com.example.extensions.defaultServerDateTimeFormatter
import com.example.extensions.parseToDate
import com.example.extensions.startOfDay
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withProgressBarDialogLoading
import javax.inject.Inject

@InjectViewState
class FavoriteSubeventPresenter @Inject constructor(
    private val eventRepository: EventRepository,
    private val appData: AppData
) : BasePresenter<FavoriteSubeventContract.View>(appData), FavoriteSubeventContract.Presenter {

    lateinit var event: String
    lateinit var actions: List<EventActivityModel>

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        setData(groupData(actions))
    }

    override fun attachView(view: FavoriteSubeventContract.View?) {
        super.attachView(view)
        loadData()
    }

    private fun loadData() {
        eventRepository.getEventActivities(event.toInt())
            .doOnSuccess { actions = it }
            .map { groupData(it) }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onSuccess = { setData(it) }
            )
    }

    private fun groupData(actions: List<EventActivityModel>): Map<Long?, List<EventActivityModel>> {
        val list = actions.filter { it.binds?.userFavorite != null }
        return if (list.isNullOrEmpty()) emptyMap()
        else {
            list.groupBy { it.holdingDate?.from?.parseToDate(defaultServerDateTimeFormatter)?.time?.startOfDay() }
        }
    }

    private fun setData(data: Map<Long?, List<EventActivityModel>>) {
        viewState.setData(data)
    }

    override fun onChangeFavoriteRequest(subevent: EventActivityModel) {
        val id = subevent.id
        if (subevent.binds?.userFavorite == null)
            compositeDisposable += eventRepository.addToFavorites(
                AddToFavoriteModel(
                    appData.getId(),
                    AddToFavoriteEntityModel(
                        AddToFavoriteEntityModel.FAVORITE_SUB_EVENT,
                        id?.toInt()
                    )
                )
            )
                .performOnBackgroundOutOnMain()
                .withProgressBarDialogLoading(viewState)
                .subscribeSimple({
                    viewState.showRequestErrorMessage()
                }) {
                    actions.firstOrNull { ac -> ac.id == subevent.id }?.binds?.userFavorite =
                        EventUserFavorite(it.id, it.user)
                    setData(groupData(actions))
                }
        else
            compositeDisposable += eventRepository.deleteFromFavorite(subevent.binds.userFavorite?.id.toString())
                .performOnBackgroundOutOnMain()
                .withProgressBarDialogLoading(viewState)
                .subscribeSimple({
                    viewState.showRequestErrorMessage()
                }) {
                    actions.firstOrNull { ac -> ac.id == subevent.id }?.binds?.userFavorite = null
                    setData(groupData(actions))
                }
    }

    override fun onSubEventClick(subEvent: EventActivityModel) {
        checkInternetAndRun {
            viewState.showSubEvent(event, subEvent.id.toString())
        }
    }

    override fun onRefreshRequest() {
        loadData()
    }
}

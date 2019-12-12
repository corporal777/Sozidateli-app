package com.example.ui.event.favorite.subevent

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.SubEvent
import com.example.extensions.defaultServerDateTimeFormatter
import com.example.extensions.parseToDate
import com.example.extensions.startOfDay
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class FavoriteSubeventPresenter @Inject constructor(
        private val eventRepository: EventRepository
) : BasePresenter<FavoriteSubeventContract.View>(), FavoriteSubeventContract.Presenter {

    lateinit var event: String
    lateinit var actions: List<SubEvent>

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        setData(groupData(actions))
    }

    override fun attachView(view: FavoriteSubeventContract.View?) {
        super.attachView(view)
        loadData()
    }

    private fun loadData() {
        eventRepository.getEventActivity(event)
                .performOnBackgroundOutOnMain()
                .subscribeSimple(
                        onError = { it.printStackTrace() },
                        onSuccess = {
                            actions = it.activities
                            setData(groupData(it.activities))
                        }
                )
    }

    private fun groupData(actions: List<SubEvent>): Map<Long?, List<SubEvent>> {
        return actions.filter { it.isInFavorites }.groupBy {
            it.start.parseToDate(defaultServerDateTimeFormatter)?.time?.startOfDay()
        }
    }

    private fun setData(data: Map<Long?, List<SubEvent>>) {
        viewState.setData(data)
    }

    override fun onChangeFavoriteRequest(subevent: SubEvent) {
        val id = subevent.id
        val request = if (!subevent.isInFavorites) eventRepository.subscribeToSubevent(event, id)
        else eventRepository.unsubscribeFromSubEvent(event, id)

        compositeDisposable += request.performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    subevent.isInFavorites = !subevent.isInFavorites
                    setData(groupData(actions))
                }
    }

    override fun onSubEventClick(subEvent: SubEvent) {
        checkInternetAndRun {
            viewState.showSubEvent(event, subEvent.id)
        }
    }

    override fun onRefreshRequest() {
        loadData()
    }
}

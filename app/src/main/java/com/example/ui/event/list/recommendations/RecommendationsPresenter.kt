package com.example.ui.event.list.recommendations

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.models.Event
import com.example.data.models.EventNew
import com.example.data.models.EventNew.Companion.EVENT_BINDS
import com.example.data.models.EventNew.Companion.EVENT_HIDDEN
import com.example.data.models.EventNew.Companion.EVENT_LIMIT
import com.example.data.models.EventNew.Companion.EVENT_OFFSET
import com.example.data.models.EventNew.Companion.EVENT_SORT_TYPE
import com.example.data.models.EventNew.Companion.EVENT_STATUS
import com.example.data.models.EventNewModel
import com.example.di.Connectivity
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.event.list.EventListPresenter
import com.example.util.pagination.PaginationResponse
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

@InjectViewState
class RecommendationsPresenter
@Inject constructor(
        appData: AppData,
        eventData: UserEventData,
        private val eventRepository: EventRepository,
        userRepository: UserRepository,
        @Connectivity connectivity: Observable<Boolean>
) : EventListPresenter<RecommendationsContract.View>(appData, eventData, eventRepository, userRepository, connectivity), RecommendationsContract.Presenter {

    override fun getPaginationRequest(limit: Int, offset: Int): Maybe<PaginationResponse<EventNew?>> {
        Log.e("EventsList", "limit: $limit ,offset: $offset")
        return eventRepository.getEventsList(mapOf(EVENT_LIMIT to limit, EVENT_OFFSET to offset, EVENT_SORT_TYPE to "desc",
                EVENT_BINDS to "rights,organization,tag,page,activity,user-registration,user-form-result,current-user-registration,destination-scheme"/*,
                EVENT_STATUS to "approved,registration,running"*/, EVENT_HIDDEN to false))
    }

    override fun onSearchClick() = viewState.showSearch()

    override fun onOrganizationsClick() = viewState.showOrganizations()

    override fun onMyEventsClick() = viewState.showMyEvents()

    override fun onMenuChatClick() = viewState.showChat()

    override fun onMenuAccountClick() = viewState.showAccount()

    override fun onMenuNotificationsClick() = viewState.showNotifications()
}
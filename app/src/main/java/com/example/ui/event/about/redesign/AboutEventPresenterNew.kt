package com.example.ui.event.about.redesign

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.bodies.*
import com.example.data.models.*
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import retrofit2.HttpException
import withCheckInternetConnectivity
import withCustomProgressBarLoadingDialog
import withLoadingDialog
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@InjectViewState
class AboutEventPresenterNew
@Inject constructor(
    private val appData: AppData,
    private val eventRepository: EventRepository,
    private val userEventData: UserEventData,
    private val userRepository: UserRepository
) : BasePresenter<AboutEventContractNew.View>(appData), AboutEventContractNew.Presenter {

    lateinit var eventId: String
    private var event: EventInfo? = null
    private var firstLaunch = true
    private var mTags = arrayListOf<Tag>()
    private lateinit var mUserEvent: UserEvent
    private var mDy = 0

    override fun changeAppBarBackgroundColorValue(canScrollVertically: Boolean, value: Int) {
        if (!canScrollVertically) {
            mDy = 0
            viewState.updateAppBarBackgroundColorValue(mDy)
        } else {
            mDy += value
            viewState.updateAppBarBackgroundColorValue(mDy)
        }
    }

    override fun attachView(view: AboutEventContractNew.View?) {
        super.attachView(view)
        viewState.updateAppBarBackgroundColorValue(mDy)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.updateAppBarBackgroundColorValue(mDy)
        val userEventInfo = userEventData.userEvent?.eventInfo
        val eventInfoMaybe =
            if (userEventInfo?.event?.id.toString() == eventId) Maybe.just(userEventInfo)
            else eventRepository.getEventDetails(eventId)
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)

        compositeDisposable += eventInfoMaybe
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    catchEventError(it)
                },
                onSuccess = { eventInfo ->
                    setEventInfoData(eventInfo)
                })
    }


    private fun setEventInfoData(eventInfo: EventInfo?) {
        this.event = eventInfo
        viewState.apply {
            setEventData(eventInfo?.event)
            setOrganizationAndInformation(
                eventInfo?.event?.binds?.organization,
                eventInfo?.event?.binds?.page,
                eventInfo?.event?.address?.fullValue,
            )
        }
        var pair = Pair<Boolean, Map<String, List<EventActivityModel>>>(false, emptyMap())
        compositeDisposable += Maybe.fromCallable {
            mTags.clear()
            eventInfo?.event?.binds?.tag?.forEach {
                mTags.add(Tag.EventTag(it.id.toString(), it.name ?: ""))
            }
            pair = getSubEvents(eventInfo?.event)
            getSortedSpeakers(eventInfo)
        }.performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.apply {
                    setEventSpeakers(it.first, it.second)
                    setEventActivitiesAndTags(pair.first, pair.second, mTags)
                    setEventPartners(eventInfo?.event?.binds?.partner)

                }
            }
    }

    private fun getSubEvents(eventNew: EventNew?): Pair<Boolean, Map<String, List<EventActivityModel>>> {
        val isApproved =
            event?.event?.binds?.currentUserRegistration?.status?.value == Event.Status.APPROVED
        var filteredSubEvents = mapOf<String, List<EventActivityModel>>()
        eventNew.let {
            if (!it?.binds?.activity.isNullOrEmpty()) {
                val list = arrayListOf<EventActivityModel>()
                if (it?.binds?.activity?.size!! > 4) {
                    for (i in 0 until 4) {
                        list.add(it.binds?.activity!!.get(i))
                    }
                } else {
                    list.addAll(it.binds?.activity ?: emptyList())
                }
                filteredSubEvents = list.groupBy { event ->
                    event.holdingDate?.from?.split(" ")?.get(0) ?: ""
                }
            }
        }
        return Pair(isApproved, filteredSubEvents.toSortedMap())
    }

    override fun onTagSelected() {
        val selectedTags = mTags.filter { x -> x.isSelected }.map {
            NewTags(it.id, it.name, it.isSelected)
        }
        viewState.showEventActivities(eventId, selectedTags)
        mTags.map {
            if (it.isSelected) {
                it.isSelected = false
            }
        }
    }

    override fun onShowEventActivitiesClick() {
        viewState.showEventActivities(eventId, emptyList())
    }

    override fun onAddEventToFavoriteClick() {
        if (event?.event?.binds?.userFavorite != null) {
            compositeDisposable += eventRepository.deleteFromFavorite(event?.event?.binds?.userFavorite?.id.toString())
                .performOnBackgroundOutOnMain()
                .withCustomProgressBarLoadingDialog(viewState)
                .subscribeSimple {
                    this.event?.event?.binds?.userFavorite = null
                    viewState.changeEventSubscription(false)
                }
        } else {
            compositeDisposable += eventRepository.addToFavorites(
                AddToFavoriteModel(
                    appData.getId(),
                    AddToFavoriteEntityModel(
                        AddToFavoriteEntityModel.FAVORITE_EVENT,
                        eventId.toInt()
                    )
                )
            )
                .performOnBackgroundOutOnMain()
                .withCustomProgressBarLoadingDialog(viewState)
                .subscribeSimple {
                    this.event?.event?.binds?.userFavorite = EventUserFavorite(it.id, it.user)
                    viewState.apply {
                        changeEventSubscription(true)
                        showEventAddedToFavoriteMessage()
                    }
                }
        }
    }

    override fun onCreateEventSubscriptionClick() {
        compositeDisposable += eventRepository.createEventSubscription(eventId.toInt())
            .andThen(eventRepository.getEventDetails(eventId))
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            //.withProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                this.event = it
                viewState.setActionButton(it.event)
            }
    }

    override fun onDeleteEventSubscriptionClick() {
        val mSubscriptionId = event?.event?.binds?.eventSubscribe?.id ?: 0
        compositeDisposable += eventRepository.deleteEventSubscription(eventId.toInt())
            .andThen(eventRepository.getEventDetails(eventId))
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                this.event = it
                viewState.setActionButton(it.event)
            }
    }

    override fun onRefreshRequest() {
        compositeDisposable += eventRepository.getEventDetails(eventId)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                }, onSuccess = {
                    setEventInfoData(it)
                })

    }

    override fun onPageClick(page: Int) {
        checkInternetAndRun { viewState.showPage(eventId, page.toString()) }
    }

    override fun onPartnerClick(partner: Int) {
        checkInternetAndRun { viewState.showPartner(eventId, partner.toString()) }
    }

    override fun onGoToEventClick() {
        if (event?.event?.binds?.currentUserRegistration == null || event?.event?.binds?.currentUserRegistration?.status?.value == Event.Status.CANCELED) {
            viewState.showEventRequest(eventId)
        }
    }

    override fun onAddToScheduleClick(subEvent: EventActivityModel) {
        processChangeEventInCalendarStatusRequest(
            subEvent,
            eventRepository.addEventToCalendarWithResult(
                EventCalendarBody(
                    appData.getId(),
                    EventCalendarBodyEntity(
                        EventCalendarBody.CALENDAR_EVENT_ACTIVITY, subEvent.id
                            ?: 0
                    )
                )
            )
                .flatMapCompletable { subEv ->
                    Completable.fromAction {
                        subEvent.binds?.userCalendar = subEv
                    }
                }
        )
    }

    override fun onRemoveFromScheduleClick(subEvent: EventActivityModel) {
        processChangeEventInCalendarStatusRequest(
            subEvent,
            eventRepository.deleteCalendarEvent(subEvent.binds?.userCalendar?.id.toString())
                .andThen(Completable.fromAction { subEvent.binds?.apply { userCalendar = null } })
        )
    }

    override fun onSubEventClick(subEvent: EventActivityModel) {
        checkInternetAndRun {
            viewState.showSubEvent(eventId, subEvent.id.toString())
        }
    }

    override fun onSpeakerClick(memberId: Int) = viewState.showSpeakerProfile(memberId)
    override fun onShowAllSpeakersClick() = viewState.showSpeakers(eventId)

    override fun onMapPageSelected() {
        viewState.apply {
            val eventInfo = event
            if (eventInfo?.event?.address?.lat != null && eventInfo.event.address.lon != null) {
                showMap(
                    eventInfo.event.createMapInfo(),
                )
            }
        }
    }


    private fun processChangeEventInCalendarStatusRequest(
        subEvent: EventActivityModel,
        request: Completable
    ) {
        compositeDisposable += request
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                viewState.updateSubEvent(subEvent)
            }
    }


    override fun onAddOrganizationToFavoriteClick() {
        if (event?.event?.binds?.organization?.binds?.userFavorite != null) {
            val id = event?.event?.binds?.organization?.binds?.userFavorite?.id.toString()
            compositeDisposable += eventRepository.deleteFromFavorite(id)
                .performOnBackgroundOutOnMain()
                .withCustomProgressBarLoadingDialog(viewState)
                .subscribeSimple(
                    onComplete = {
                        event?.event?.binds?.organization?.binds?.userFavorite = null
                        viewState.changeOrganizationSubscription(false)
                    }, onError = {
                        it.printStackTrace()
                    })
        } else {
            compositeDisposable += eventRepository.addToFavorites(
                AddToFavoriteModel(
                    appData.getId(),
                    AddToFavoriteEntityModel(
                        AddToFavoriteEntityModel.FAVORITE_ORGANIZATION,
                        event?.event?.binds?.organization?.id?.toInt()
                    )
                )
            )
                .performOnBackgroundOutOnMain()
                .withCustomProgressBarLoadingDialog(viewState)
                .subscribeSimple(
                    onSuccess = {
                        event?.event?.binds?.organization?.binds?.userFavorite =
                            EventUserFavorite(it.id, it.user)
                        viewState.changeOrganizationSubscription(true)
                    }, onError = {
                        it.printStackTrace()
                    })
        }
    }

    override fun onActionCancel() {
        compositeDisposable += eventRepository.cancelRegisterToEvent(
            event?.event?.binds?.currentUserRegistration?.id ?: 0
        )
            .andThen(eventRepository.getEventDetails(eventId))
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                this.event = it
                viewState.setActionButton(it.event)
            }
    }

    override fun onOrganizationClick(organization: String) =
        viewState.showOrganization(organization)

    override fun onShareClick() = viewState.showShare(eventId)

    private fun getSortedSpeakers(eventInfo: EventInfo?): Pair<Boolean, List<MemberModel>> {
        var canShowMore = false
        var list = eventInfo?.event?.binds?.member?.filter { it.role == "speaker" }
        if (!list.isNullOrEmpty()) {
            if (list.size > 5) {
                list = list.subList(0, 5)
                canShowMore = true
            } else {
                canShowMore = false
            }
        } else {
            canShowMore = false
        }
        val members = arrayListOf<MemberModel>()
        members.addAll(list?.filter { x -> x.isLead == true }
            ?.sortedBy { x -> x.binds?.user?.fullName } ?: emptyList())
        members.addAll(list?.filter { x -> x.isLead == false }
            ?.sortedBy { x -> x.binds?.user?.fullName } ?: emptyList())
        return Pair(canShowMore, members)
    }


    private fun catchEventError(t: Throwable) {
        if (t is HttpException) {
            when (t.code()) {
                403 -> {
                    try {
                        val error = Gson().fromJson(
                            t.response()?.errorBody()?.string(),
                            NewErrors::class.java
                        )
                        when (error.errors[0].message) {
                            "you have no access for such operation" -> {
                                val message =
                                    "В данный момент страница мероприятия доступна только владельцу или администратору"
                                viewState.showErrorMessageWithResult(false, "", message)
                            }
                            "The event has been banned" -> {
                                val eventName = error.errors[0].additionalData?.name
                                val eventId = error.errors[0].additionalData?.id.toString()
                                val message =
                                    "Мероприятие «$eventName» заблокировано."
                                viewState.showErrorMessageWithResult(true, eventId, message)
                            }
                            "The event has been cancelled" -> {
                                val eventName = error.errors[0].additionalData?.name
                                val eventId = error.errors[0].additionalData?.id.toString()
                                val message =
                                    "Мероприятие «$eventName» было отменено организатором."
                                viewState.showErrorMessageWithResult(true, eventId, message)
                            }
                            else -> {
                                onReceiveError(t)
                            }
                        }
                    } catch (e: Exception) {

                    }
                }
            }
        }
    }

}

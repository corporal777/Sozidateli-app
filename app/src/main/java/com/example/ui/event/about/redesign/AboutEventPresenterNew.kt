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
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import withProgressBarLoadingDialog
import javax.inject.Inject

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


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showLoadingDialog()
        val userEventInfo = userEventData.userEvent?.eventInfo

        val eventInfoMaybe =
            if (userEventInfo?.event?.id.toString() == eventId) Maybe.just(userEventInfo)
            //else eventRepository.getEventDetails(eventId)
            else userEventData.loadEventData(eventId)
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)

        compositeDisposable += eventInfoMaybe
            .subscribeSimple(
                onSuccess = { userEvent ->
                    mUserEvent = userEvent as UserEvent
                    setEventInfoData(userEvent.eventInfo)
                },
                onError = {
                    it.printStackTrace()
                })

        Log.e("TOKEN", appData.token!!)
    }

    private fun setEventInfoData(eventInfo: EventInfo?) {
        this.event = eventInfo
        Log.e("EVENT ID", eventId)

        mTags.clear()
        eventInfo?.event?.binds?.tag?.map {
            mTags.add(Tag.EventTag(it.id.toString(), it.name ?: ""))
        }
        viewState.apply {
            setEventData(
                eventInfo?.event,
                eventInfo?.event?.binds?.currentUserRegistration?.status?.value,
                eventInfo?.event?.binds?.page,
                eventInfo?.event?.binds?.partner,
                mTags,
                eventInfo?.event?.userAgreement?.name ?: eventInfo?.event?.userAgreement?.uri
            )
        }
        setSubEvents()
    }

    private fun getSubEvents() {
        compositeDisposable += userEventData.loadEventData(eventId)
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                mUserEvent = it
            }
    }

    private fun setSubEvents() {
        mUserEvent.let {
            if (!it.activity.activities.isNullOrEmpty()) {
                val listSubEvents = it.activity.activities.groupBy { event ->
                    event.holdingDate?.from?.split(" ")?.get(0)
                }
                val filteredSubEvents = mutableMapOf<String, ArrayList<EventActivityModel>>()
                var mSize = 4

                listSubEvents.map { map ->
                    if (mSize != 0) {
                        filteredSubEvents[map.key ?: ""] = arrayListOf()
                    }
                    map.value.forEach { event ->
                        if (mSize != 0) {
                            filteredSubEvents[map.key]?.add(event)
                        }
                        mSize -= 1
                    }
                }

                viewState.setSubEvents(filteredSubEvents)
            }
        }

    }

    override fun onTagSelected(){
        //setSubEvents()
        val selectedTags = mTags.filter { x -> x.isSelected }.map {
            NewTags(it.id, it.name, it.isSelected)
        }
        viewState.showEventActivities(eventId, selectedTags)
        mTags.map {
            if (it.isSelected){
                it.isSelected = false
            }
        }
    }

    override fun onShowEventActivitiesClick() {
        viewState.showEventActivities(eventId, emptyList())
    }

    override fun onRefreshRequest() {
        compositeDisposable += userEventData.loadEventData(eventId)
        //compositeDisposable += eventRepository.getEventDetails(eventId)
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(onError = {
                it.printStackTrace()
            }, onSuccess = {
                mUserEvent = it
                setEventInfoData(it.eventInfo)
            })

    }

    override fun onPageClick(page: Int) { checkInternetAndRun { viewState.showPage(eventId, page.toString()) } }
    override fun onPartnerClick(partner: Int) { checkInternetAndRun { viewState.showPartner(eventId, partner.toString()) } }

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

        //viewState.showSubEvent(userEvent.eventId, subEvent.id.toString())
        checkInternetAndRun {
            viewState.showSubEvent(eventId, subEvent.id.toString())
        }
    }

    override fun onSpeakerClick(speakerId: Int) = viewState.showSpeakerProfile(speakerId)

    override fun onMapPageSelected() {
        viewState.apply {
            val eventInfo = event
            showMap(
                eventInfo?.event?.createMapInfo(),
            )
        }
    }


    private fun processChangeEventInCalendarStatusRequest(
        subEvent: EventActivityModel,
        request: Completable
    ) {
        compositeDisposable += request
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withProgressBarLoadingDialog(viewState)
            //.withLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                },
                onComplete = {
                    viewState.updateSubEvent(subEvent)
                })


    }


    override fun onChangeFavoriteClick() {
        if (event?.event?.binds?.userFavorite != null) {
            compositeDisposable += eventRepository.deleteFromFavorite(event?.event?.binds?.userFavorite?.id.toString())
                .performOnBackgroundOutOnMain()
                .withProgressBarLoadingDialog(viewState)
                //.withLoadingDialog(viewState)
                .subscribeSimple(
                    onComplete = {
                        event?.event?.binds?.userFavorite = null
                        viewState.changeEventSubscription(false)
                    }, onError = {
                        it.printStackTrace()
                    })
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
                .withProgressBarLoadingDialog(viewState)
                //.withLoadingDialog(viewState)
                .subscribeSimple(
                    onSuccess = {
                        event?.event?.binds?.userFavorite = EventUserFavorite(it.id, it.user)
                        viewState.changeEventSubscription(true)
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
            .withProgressBarLoadingDialog(viewState)
            //.withLoadingDialog(viewState)
            .subscribeSimple {
                this.event = it
                viewState.setActionButton(
                    it.event,
                    it?.event?.binds?.currentUserRegistration?.status?.value
                )
            }
    }

    override fun onOrganizationClick(organization: String) = viewState.showOrganization(organization)
    override fun onShareClick() = viewState.showShare(eventId)

}

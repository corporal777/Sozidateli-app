package com.example.ui.event.about

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.bodies.AddToFavoriteEntityModel
import com.example.data.bodies.AddToFavoriteModel
import com.example.data.bodies.EventCalendarBody
import com.example.data.bodies.EventCalendarBodyEntity
import com.example.data.models.*
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.event.about.items.AboutEventData
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import retrofit2.HttpException
import withCheckInternetConnectivity
import withCustomProgressBarLoadingDialog
import withDelay
import withProgressBarLoadingDialog
import javax.inject.Inject

@InjectViewState
class AboutEventPresenterNew
@Inject constructor(
    private val appData: AppData,
    private val eventRepository: EventRepository,
    private val userEventData: UserEventData,
) : BasePresenter<AboutEventContractNew.View>(appData), AboutEventContractNew.Presenter {

    lateinit var eventId: String
    private var event: EventInfo? = null
    private var mDy = 0
    private lateinit var aboutEventData: AboutEventData

    override fun changeAppBarBackgroundColorValue(value: Int) {
        mDy = value
        viewState.updateAppBarBackgroundColorValue(mDy)
    }

    override fun attachView(view: AboutEventContractNew.View?) {
        super.attachView(view)
        //viewState.updateAppBarBackgroundColorValue(mDy)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            updateAppBarBackgroundColorValue(mDy)
            setEventDataPlaceholder()
        }

        val userEventInfo = userEventData.userEvent?.eventInfo
        val eventInfoMaybe =
            if (userEventInfo?.event?.id.toString() == eventId) Maybe.just(userEventInfo)
            else eventRepository.getEventDetails(eventId)

        compositeDisposable += eventInfoMaybe
            .flatMap {
                this.event = it
                prepareAboutEventData(it.event)
            }
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    catchEventError(it)
                }, onSuccess = { eventInfo ->
                    viewState.setEventData(eventInfo)
                })
    }

    private fun prepareAboutEventData(event: EventNew?): Maybe<AboutEventData> {
        return if (event != null) {
            aboutEventData = AboutEventData(event).apply {
                setTags()
                setSortedSpeakers()
                setSubEvents()
                setPartners()
            }
            Maybe.just(aboutEventData)
        } else Maybe.empty()
    }

    override fun onRefreshRequest() {
        compositeDisposable += eventRepository.getEventDetails(eventId)
            .flatMap {
                this.event = it
                prepareAboutEventData(it.event)
            }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                }, onSuccess = {
                    viewState.setEventData(it)
                })

    }


    override fun onTagSelected() {
        val selectedTags = aboutEventData.tags.filter { x -> x.isSelected }.map {
            NewTags(it.id, it.name, it.isSelected)
        }
        viewState.showEventActivities(eventId, selectedTags)
        compositeDisposable += Maybe.fromCallable {
            aboutEventData.tags.find { it.isSelected }?.apply {
                this.isSelected = false
            }
        }
            .withDelay(100)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                if (it != null) viewState.updateTags(it)
            }
    }

    override fun onAddEventToFavoriteClick() {
        if (event?.event?.binds?.userFavorite != null) {
            compositeDisposable += eventRepository.deleteFromFavorite(event?.event?.binds?.userFavorite?.id.toString())
                .performOnBackgroundOutOnMain()
                .subscribeSimple {
                    this.event?.event?.binds?.userFavorite = null
                    viewState.apply {
                        changeEventSubscription(false)
                        showEventRemovedFromFavoriteDialog()
                    }
                }
        } else {
            compositeDisposable += eventRepository.addToFavorites(
                AddToFavoriteModel(
                    appData.getId(), AddToFavoriteEntityModel(
                        AddToFavoriteEntityModel.FAVORITE_EVENT, eventId.toInt()
                    )
                )
            ).performOnBackgroundOutOnMain()
                .subscribeSimple {
                    this.event?.event?.binds?.userFavorite = EventUserFavorite(it.id, it.user)
                    viewState.apply {
                        changeEventSubscription(true)
                        showEventAddedToFavoriteDialog()
                    }
                }
        }
    }

    override fun onCreateEventSubscriptionClick() {
        compositeDisposable += eventRepository.createEventSubscription(eventId.toInt())
            .andThen(eventRepository.getEventDetails(eventId)).performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                this.event = it
                viewState.setActionButton(it.event)
            }
    }

    override fun onDeleteEventSubscriptionClick() {
        val mSubscriptionId = event?.event?.binds?.eventSubscribe?.id ?: 0
        compositeDisposable += eventRepository.deleteEventSubscription(eventId.toInt())
            .andThen(eventRepository.getEventDetails(eventId)).performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState).subscribeSimple {
                this.event = it
                viewState.setActionButton(it.event)
            }
    }


    override fun onShowEventActivitiesClick() = viewState.showEventActivities(eventId, emptyList())
    override fun onPageClick(page: Int) = viewState.showPage(eventId, page.toString())
    override fun onPartnerClick(partner: Int) = viewState.showPartner(eventId, partner.toString())
    override fun onSubEventClick(subEvent: EventActivityModel) =
        viewState.showSubEvent(eventId, subEvent.id.toString())

    override fun onSpeakerClick(memberId: Int) = viewState.showSpeakerProfile(memberId, eventId)
    override fun onShowAllSpeakersClick() = viewState.showSpeakers(eventId)


    override fun onAddToScheduleClick(subEvent: EventActivityModel) {
        processChangeEventInCalendarStatusRequest(subEvent,
            eventRepository.addEventToCalendarWithResult(
                EventCalendarBody(
                    appData.getId(), EventCalendarBodyEntity(
                        EventCalendarBody.CALENDAR_EVENT_ACTIVITY, subEvent.id ?: 0
                    )
                )
            ).flatMapCompletable { subEv ->
                Completable.fromAction {
                    subEvent.binds?.userCalendar = subEv
                }
            })
    }

    override fun onRemoveFromScheduleClick(subEvent: EventActivityModel) {
        processChangeEventInCalendarStatusRequest(
            subEvent,
            eventRepository.deleteCalendarEvent(subEvent.binds?.userCalendar?.id.toString())
                .andThen(Completable.fromAction { subEvent.binds?.apply { userCalendar = null } })
        )
    }

    override fun onMapPageSelected() {
        viewState.apply {
            val eventInfo = event
            if (eventInfo?.event?.address?.lat != null && eventInfo.event.address.lon != null) {
                showMap(eventInfo.event.createMapInfo())
            }
        }
    }


    private fun processChangeEventInCalendarStatusRequest(
        subEvent: EventActivityModel, request: Completable
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
        val organizationFavorite = event?.event?.binds?.organization?.binds?.userFavorite
        if (organizationFavorite != null) {
            compositeDisposable += eventRepository.deleteFromFavorite(organizationFavorite.id.toString())
                .doOnComplete { event?.event?.binds?.organization?.binds?.userFavorite = null }
                .performOnBackgroundOutOnMain()
                .subscribeSimple(
                    onComplete = {
                        viewState.apply {
                            changeOrganizationSubscription(false)
                            showEventRemovedFromFavoriteDialog()
                        }
                    }, onError = { it.printStackTrace() }
                )
        } else {
            compositeDisposable += eventRepository.addToFavorites(
                AddToFavoriteModel(
                    appData.getId(), AddToFavoriteEntityModel(
                        AddToFavoriteEntityModel.FAVORITE_ORGANIZATION,
                        event?.event?.binds?.organization?.id?.toInt()
                    )
                )
            )
                .performOnBackgroundOutOnMain()
                .doOnSuccess {
                    event?.event?.binds?.organization?.binds?.userFavorite =
                        EventUserFavorite(it.id, it.user)
                }
                .subscribeSimple(
                    onSuccess = {
                        viewState.apply {
                            changeOrganizationSubscription(true)
                            showEventAddedToFavoriteDialog()
                        }
                    }, onError = { it.printStackTrace() }
                )
        }
    }

    override fun onActionRegister(url: String?) {
//        if (event?.event?.binds?.currentUserRegistration == null || event?.event?.binds?.currentUserRegistration?.status?.value == Event.Status.CANCELED) {
//            viewState.showEventRequest(eventId)
//        }
        if (url.isNullOrEmpty()) viewState.showEventRequest(eventId)
        else {
            compositeDisposable += eventRepository.checkRegistrationAgreement(eventId)
                .performOnBackgroundOutOnMain()
                .withCustomProgressBarLoadingDialog(viewState)
                .subscribeSimple(
                    onError = { onReceiveError(it) },
                    onSuccess = {
                        if (it.isAccepted()) viewState.showEventRequest(eventId)
                        else viewState.showAgreementRegisterDialog(eventId, url)
                    }
                )
        }
    }

    override fun onActionCancel() {
        val registrationId = event?.event?.binds?.currentUserRegistration?.id ?: 0
        compositeDisposable += eventRepository.cancelRegisterToEvent(registrationId)
            .andThen(eventRepository.getEventDetails(eventId))
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                this.event = it
                viewState.setActionButton(it.event)
            }
    }

    override fun onAcceptRegistrationAgreement(event: String) {
        compositeDisposable += eventRepository.acceptRegistrationAgreement(event)
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { if (it.isAccepted()) viewState.showEventRequest(event) }
            )
    }

    override fun onOrganizationClick(organization: String) =
        viewState.showOrganization(organization)

    override fun onShareClick() = viewState.showShare(eventId)
    override fun onAddEventToCalendarClick() = viewState.addEventToCalendar(this.event?.event)


    private fun catchEventError(t: Throwable) {
        if (t is HttpException) {
            when (t.code()) {
                403 -> {
                    try {
                        val error = Gson().fromJson(
                            t.response()?.errorBody()?.string(), NewErrors::class.java
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
                                val message = "Мероприятие «$eventName» заблокировано."
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
                else -> onReceiveError(t)
            }
        } else onReceiveError(t)
    }

}

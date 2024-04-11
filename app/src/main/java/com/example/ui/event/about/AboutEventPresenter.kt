package com.example.ui.event.about

import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.bodies.AddToFavoriteModel.Companion.toEventBody
import com.example.data.bodies.AddToFavoriteModel.Companion.toOrgBody
import com.example.data.bodies.EventCalendarBody.Companion.toCalendarBody
import com.example.data.models.AboutEventData
import com.example.data.models.EventActivityModel
import com.example.data.models.EventFormModel
import com.example.data.models.EventUserFavorite
import com.example.data.models.Optional
import com.example.data.models.createMapInfo
import com.example.data.socket.SocketIOManager
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.google.gson.Gson
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import retrofit2.HttpException
import withCheckInternetConnectivity
import withCustomLoading
import withDelay
import withProgressBarDialogLoading
import javax.inject.Inject

@InjectViewState
class AboutEventPresenter
@Inject constructor(
    private val appData: AppData,
    private val eventRepository: EventRepository,
    private val userEventData: UserEventData,
    private val socket: SocketIOManager,
) : BasePresenter<AboutEventContract.View>(appData), AboutEventContract.Presenter {

    lateinit var eventId: String
    private var mDy = 0
    private lateinit var aboutEventData: AboutEventData

    override fun changeAppBarBackgroundColorValue(value: Int) {
        mDy = value
        viewState.updateAppBarBackgroundColorValue(mDy)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.updateAppBarBackgroundColorValue(mDy)

        compositeDisposable += Maybe.defer {
            val userEventInfo = userEventData.userEvent?.eventInfo
            if (userEventInfo?.event?.id.toString() == eventId) Maybe.just(userEventInfo)
            else eventRepository.getEventDetails(eventId)
        }
            .map { AboutEventData(it.event).apply { aboutEventData = this } }
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { catchEventError(it) },
                onSuccess = { viewState.setEventData(it) }
            )
    }

    override fun onRefreshRequest() {
        compositeDisposable += eventRepository.getEventDetails(eventId)
            .map { AboutEventData(it.event).apply { aboutEventData = this } }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onSuccess = { viewState.setEventData(it) }
            )
    }


    override fun onTagSelected() {
        val selectedTags = aboutEventData.getSelectedTags()
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

    override fun onCreateEventSubscriptionClick() {
        compositeDisposable += eventRepository.createEventSubscription(eventId.toInt())
            .andThen(eventRepository.getEventDetails(eventId))
            .doOnSuccess { aboutEventData.event = it.event }
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple {
                viewState.setActionButton(it.event)
            }
    }

    override fun onDeleteEventSubscriptionClick() {
        compositeDisposable += eventRepository.deleteEventSubscription(eventId.toInt())
            .andThen(eventRepository.getEventDetails(eventId))
            .doOnSuccess { aboutEventData.event = it.event }
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple {
                viewState.setActionButton(it.event)
            }
    }

    override fun onAddEventToFavoriteClick() {
        val userFavorite = aboutEventData.event.binds?.userFavorite
        compositeDisposable += Single.defer {
            if (userFavorite != null) eventRepository.deleteFromFavorite(userFavorite.id.toString())
                .andThen(Single.just(Optional(null)))
            else eventRepository.addToFavorites(toEventBody(appData.getId(), eventId))
                .map { Optional(EventUserFavorite(it.id, it.user)) }
        }.doOnSuccess { aboutEventData.event.binds?.userFavorite = it.value }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.apply {
                    setEventFavoriteButton(aboutEventData.event.binds?.userFavorite != null)
                    if (aboutEventData.event.binds?.userFavorite == null) showEventRemovedFromFavoriteDialog()
                    else showEventAddedToFavoriteDialog()
                }
            }
    }

    override fun onAddOrganizationToFavoriteClick() {
        val organizationFavorite = aboutEventData.event.binds?.organization?.binds?.userFavorite
        compositeDisposable += Single.defer {
            if (organizationFavorite != null)
                eventRepository.deleteFromFavorite(organizationFavorite.id.toString())
                    .andThen(Single.just(Optional(null)))
            else eventRepository.addToFavorites(
                toOrgBody(appData.getId(), aboutEventData.event.binds?.organization?.id)
            ).map { Optional(EventUserFavorite(it.id, it.user)) }
        }.doOnSuccess { aboutEventData.event.binds?.organization?.binds?.userFavorite = it.value }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.apply {
                    changeOrganizationSubscription(it.value != null)
                    if (it.value == null) showEventRemovedFromFavoriteDialog()
                    else showEventAddedToFavoriteDialog()
                }
            }
    }

    override fun onAddToScheduleClick(subEvent: EventActivityModel) {
        val userCalendar = subEvent.binds?.userCalendar
        compositeDisposable += Single.defer {
            if (userCalendar == null)
                eventRepository.addEventToCalendarWithResult(
                    toCalendarBody(appData.getId(), subEvent.id)
                ).map { Optional(it) }
            else eventRepository.deleteCalendarEvent(userCalendar.id.toString())
                .andThen(Single.just(Optional(null)))
        }.doOnSuccess { subEvent.binds?.userCalendar = it.value }
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple {
                viewState.updateSubEvent(subEvent)
            }
    }


    override fun onActionRegister(url: String?) {
        if (url.isNullOrEmpty()) registerToEvent(eventId)
        else compositeDisposable += eventRepository.checkRegistrationAgreement(eventId)
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    viewState.showAgreementRegisterDialog(eventId, url)
                    //if (it.isAccepted()) registerToEvent(eventId)
                    //else viewState.showAgreementRegisterDialog(eventId, url)
                }
            )
    }

    override fun onAcceptRegistrationAgreement(event: String) {
        compositeDisposable += eventRepository.acceptRegistrationAgreement(event)
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { if (it.isAccepted()) registerToEvent(eventId) }
            )
    }

    private fun registerToEvent(event: String) {
        if (aboutEventData.event.isFormEnabled()) viewState.showEventRequest(event)
        else compositeDisposable += eventRepository.registerToEvent(eventId.toInt())
            .andThen(socket.connectToUpdates())
            .andThen(eventRepository.getEventDetails(eventId))
            .doOnSuccess { aboutEventData.event = it.event }
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { viewState.setActionButton(it.event) }
            )
    }

    override fun onActionCancel() {
        val registrationId = aboutEventData.event.binds?.currentUserRegistration?.id ?: 0
        compositeDisposable += eventRepository.cancelRegisterToEvent(registrationId)
            .andThen(eventRepository.getEventDetails(eventId))
            .doOnSuccess { aboutEventData.event = it.event }
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
            .subscribeSimple {
                viewState.setActionButton(it.event)
            }
    }

    override fun onShowEventActivitiesClick() = viewState.showEventActivities(eventId, emptyList())
    override fun onPageClick(page: Int) = viewState.showPage(eventId, page.toString())
    override fun onPartnerClick(partner: Int) = viewState.showPartner(eventId, partner.toString())
    override fun onSubEventClick(subEvent: EventActivityModel) =
        viewState.showSubEvent(eventId, subEvent.id)

    override fun onOrganizationClick(orgId: String) = viewState.showOrganization(orgId)
    override fun onSpeakerClick(memberId: Int) = viewState.showSpeakerProfile(memberId, eventId)
    override fun onShowAllSpeakersClick() = viewState.showSpeakers(eventId)

    override fun onMapPageSelected() {
        if (aboutEventData.event.address?.lat != null && aboutEventData.event.address?.lon != null)
            viewState.showMap(aboutEventData.event.createMapInfo())
    }

    override fun onShareClick() = viewState.showShare(eventId)
    override fun onAddEventToCalendarClick() = viewState.addEventToCalendar(aboutEventData.event)

    override fun onShowFormResult() {
        val formResult = aboutEventData.event.binds?.userFormResult
            ?.firstOrNull { e -> e.formType?.type == EventFormModel.Type.PARTICIPATION } ?: return
        viewState.showEventFormResult(formResult)
    }

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
                            else -> onReceiveError(t)
                        }
                    } catch (e: Exception) { }
                }

                else -> onReceiveError(t)
            }
        } else onReceiveError(t)
    }

}

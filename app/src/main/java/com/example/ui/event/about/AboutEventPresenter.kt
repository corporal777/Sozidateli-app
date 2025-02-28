package com.example.ui.event.about

import android.util.Log
import com.example.data.AppData
import com.example.data.models.AboutEventData
import com.example.data.models.ApiError
import com.example.data.models.EventActivityModel
import com.example.data.models.EventNew
import com.example.data.socket.SocketIOManager
import com.example.exceptions.EventAgreementException
import com.example.repository.EventRepository
import com.example.repository.OrganizationRepository
import com.example.ui.base.BasePresenter
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import retrofit2.HttpException
import withCheckInternetConnectivity
import withCustomLoading
import withDelay
import javax.inject.Inject

@InjectViewState
class AboutEventPresenter
@Inject constructor(
    private val appData: AppData,
    private val eventRepository: EventRepository,
    private val organizationRepository: OrganizationRepository,
    private val socket: SocketIOManager,
) : BasePresenter<AboutEventContract.View>(appData), AboutEventContract.Presenter {

    lateinit var eventId: String
    private lateinit var eventData: AboutEventData
    private var onRequest: () -> Unit = {}

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        onRequest = {
            compositeDisposable += eventRepository.getEventDetails(eventId)
                .map { AboutEventData(it).apply { eventData = this } }
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .subscribeSimple(
                    onError = { catchEventError(it) },
                    onSuccess = { viewState.setEventData(it) }
                )
        }
        onRequest.invoke()
    }


    override fun onTagSelected() {
        val selectedTags = eventData.getSelectedTags()
        viewState.showEventActivities(eventId, selectedTags)
        compositeDisposable += Maybe.fromCallable {
            eventData.tags.find { it.isSelected }?.apply {
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
        compositeDisposable += eventRepository.addOrRemoveEventFavorite(eventData.event)
            .doOnSuccess { eventData.event.binds?.userFavorite = it.value }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    viewState.apply {
                        setEventFavoriteButton(it.value != null)
                        if (it.value == null) showRemovedFromFavoriteDialog()
                        else showAddedToFavoriteDialog()
                    }
                })
    }


    override fun onAddOrganizationToFavoriteClick() {
        val organization = eventData.event.binds?.organization
        compositeDisposable += organizationRepository.addOrRemoveOrgFavorite(organization)
            .doOnSuccess { eventData.event.binds?.organization?.binds?.userFavorite = it.value }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    viewState.apply {
                        updateOrganization(it.value != null)
                        if (it.value == null) showRemovedFromFavoriteDialog()
                        else showAddedToFavoriteDialog()
                    }
                })

    }

    override fun onAddToScheduleClick(subEvent: EventActivityModel) {
        compositeDisposable += eventRepository.addOrRemoveSubEventCalendar(subEvent)
            .doOnSuccess { subEvent.binds?.userCalendar = it.value }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { viewState.updateSubEvent(subEvent) }
            )
    }

    override fun onSubscribeEvent(isSubscribed: Boolean) {
        compositeDisposable += Completable.defer {
            if (isSubscribed) eventRepository.deleteEventSubscription(eventId.toInt())
            else eventRepository.createEventSubscription(eventId.toInt())
        }
            .andThen(eventRepository.getEventDetails(eventId))
            .doOnSuccess { eventData.event = it }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { onReceiveActionError(it) },
                onSuccess = {
                    viewState.setActionButton(it)
                    viewState.showEventSubscribedDialog(it.binds?.isUserSubscribed)
                }
            )
    }

    override fun onActionRegister(withAccept: Boolean) {
        val url = eventData.event.userAgreement?.uri
        compositeDisposable += Completable.defer {
            if (withAccept) eventRepository.acceptRegistrationAgreement(eventId)
                .doOnSuccess { if (it.isAccepted()) eventData.event.state?.agreement?.setAccepted() }
                .ignoreElement()
            else {
                if (url.isNullOrEmpty()) Completable.complete()
                else if (eventData.event.state?.isAgreementAccepted() == true) Completable.complete()
                else Completable.error(EventAgreementException()).withDelay(500)
            }
        }
            .andThen(registerToEvent())
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    if (it is EventAgreementException) viewState.showAgreementDialog(eventId, url)
                    else onReceiveActionError(it)
                },
                onSuccess = {
                    viewState.apply {
                        setActionButton(it)
                        if (it.isFormEnabled()) viewState.showEventRequest(eventId)
                        else showEventRegistrationSuccessDialog()
                    }
                }
            )
    }

    private fun registerToEvent(): Maybe<EventNew> {
        return if (eventData.event.isFormEnabled()) Maybe.just(eventData.event).withDelay(500)
        else eventRepository.registerToEvent(eventId.toInt())
            .andThen(socket.connectToUpdates())
            .andThen(eventRepository.getEventDetails(eventId))
            .doOnSuccess {
                eventData.event = it
                appData.sendUpdateEvent(it)
            }
    }

    override fun onActionCancel() {
        val registrationId = eventData.event.binds?.currentUserRegistration?.id ?: 0
        compositeDisposable += eventRepository.cancelRegisterToEvent(registrationId)
            .andThen(eventRepository.getEventDetails(eventId))
            .doOnSuccess {
                eventData.event = it
                appData.sendUpdateEvent(it)
            }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { onReceiveActionError(it) },
                onSuccess = { viewState.setActionButton(it) }
            )
    }


    override fun onShowSubEventsClick() = viewState.showEventActivities(eventId, emptyList())

    override fun onPartnerClick(partner: Int) = viewState.showPartner(eventId, partner.toString())
    override fun onSubEventClick(subEvent: Int) = viewState.showSubEvent(eventId, subEvent)

    override fun onOrganizationClick(orgId: String) = viewState.showOrganization(orgId)
    override fun onSpeakerClick(memberId: Int) = viewState.showSpeakerProfile(memberId, eventId)
    override fun onShowAllSpeakersClick() = viewState.showSpeakers(eventId)

    override fun onShareClick() = viewState.showShare(eventId)
    override fun onAddEventToCalendarClick() = viewState.addEventToCalendar(eventData.event)


    override fun onShowAuthorization() {
        appData.savedEventId = eventId
        viewState.showAuthorization()
    }

    override fun onChangeAppBarBackgroundColor(value: Int) {
        viewState.updateAppBarBackgroundColor(value)
    }

    override fun onRefreshRequest() = onRequest.invoke()

    private fun onReceiveActionError(it : Throwable){
        viewState.setActionButton(eventData.event)
        onReceiveError(it)
    }

    private fun catchEventError(t: Throwable) {
        if (t is HttpException && t.code() == 403){
            try {
                val error = NewErrors.fromJson(t)
                val errorMessage = error?.errors?.get(0)?.message ?: ""

                if (errorMessage == "you have no access for such operation"){
                    val message = "В данный момент страница мероприятия доступна только владельцу или администратору"
                    viewState.showErrorMessageWithResult(false, "", message)
                }
                else if (errorMessage == "The event has been banned"){
                    val eventName = error?.errors?.get(0)?.additionalData?.name
                    val eventId = error?.errors?.get(0)?.additionalData?.id.toString()
                    val message = "Мероприятие «$eventName» заблокировано."
                    viewState.showErrorMessageWithResult(true, eventId, message)
                }
                else if (errorMessage == "The event has been cancelled"){
                    val eventName = error?.errors?.get(0)?.additionalData?.name
                    val eventId = error?.errors?.get(0)?.additionalData?.id.toString()
                    val message = "Мероприятие «$eventName» было отменено организатором."
                    viewState.showErrorMessageWithResult(true, eventId, message)
                }
                else onReceiveError(t)
            } catch (e: Exception) { onReceiveError(t) }
        } else onReceiveError(t)
    }

}

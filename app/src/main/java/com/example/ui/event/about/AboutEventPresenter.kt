package com.example.ui.event.about

import com.example.data.AppData
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
import io.reactivex.Completable
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
    private val socket: SocketIOManager,
) : BasePresenter<AboutEventContract.View>(appData), AboutEventContract.Presenter {

    lateinit var eventId: String
    private var scrollOffset = 0
    private var isFirstLaunch = true
    private lateinit var aboutEventData: AboutEventData

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadData()
    }


    private fun loadData() {
        compositeDisposable += eventRepository.getEventDetails(eventId)
            .map { AboutEventData(it).apply { aboutEventData = this } }
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { catchEventError(it) },
                onSuccess = { viewState.setEventData(it) }
            )
    }

    override fun onRefreshRequest() = loadData()


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

    override fun onSubscribeEvent(isSubscribed: Boolean) {
        compositeDisposable += Completable.defer {
            if (isSubscribed) eventRepository.deleteEventSubscription(eventId.toInt())
            else eventRepository.createEventSubscription(eventId.toInt())
        }
            .andThen(eventRepository.getEventDetails(eventId))
            .doOnSuccess { aboutEventData.event = it }
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple {
                viewState.setActionButton(it)
                viewState.showEventSubscribedDialog(it.binds?.isUserSubscribed)
            }

    }

    override fun onAddEventToFavoriteClick() {
        val data = aboutEventData.event.binds?.userFavorite
        compositeDisposable += Single.defer {
            if (data != null) eventRepository.deleteFromFavorites(data.id.toString())
                .andThen(Single.just(Optional(null)))
            else eventRepository.addEventToFavorites(eventId)
                .map { Optional(EventUserFavorite(it.id, it.user)) }
        }.doOnSuccess { aboutEventData.event.binds?.userFavorite = it.value }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.apply {
                    setEventFavoriteButton(aboutEventData.event.binds?.userFavorite != null)
                    if (aboutEventData.event.binds?.userFavorite == null) showRemovedFromFavoriteDialog()
                    else showAddedToFavoriteDialog()
                }
            }
    }


    override fun onAddOrganizationToFavoriteClick() {
        val organizationFavorite = aboutEventData.event.binds?.organization?.binds?.userFavorite
        compositeDisposable += Single.defer {
            if (organizationFavorite != null)
                eventRepository.deleteFromFavorites(organizationFavorite.id.toString())
                    .andThen(Single.just(Optional(null)))
            else eventRepository.addOrgToFavorites(aboutEventData.event.binds?.organization?.id.toString())
            .map { Optional(EventUserFavorite(it.id, it.user)) }
        }.doOnSuccess { aboutEventData.event.binds?.organization?.binds?.userFavorite = it.value }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.apply {
                    updateOrganization(it.value != null)
                    if (it.value == null) showRemovedFromFavoriteDialog()
                    else showAddedToFavoriteDialog()
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
                    if (it.isAccepted()) registerToEvent(eventId)
                    else viewState.showAgreementRegisterDialog(eventId, url)
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
            .doOnSuccess { aboutEventData.event = it }
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    viewState.apply {
                        setActionButton(it)
                        showEventRegistrationSuccessDialog()
                    }
                }
            )
    }

    override fun onActionCancel() {
        val registrationId = aboutEventData.event.binds?.currentUserRegistration?.id ?: 0
        compositeDisposable += eventRepository.cancelRegisterToEvent(registrationId)
            .andThen(eventRepository.getEventDetails(eventId))
            .doOnSuccess { aboutEventData.event = it }
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
            .subscribeSimple {
                viewState.setActionButton(it)
            }
    }

    override fun onShowEventActivitiesClick() = viewState.showEventActivities(eventId, emptyList())

    override fun onPartnerClick(partner: Int) = viewState.showPartner(eventId, partner.toString())
    override fun onSubEventClick(subEvent: EventActivityModel) =
        viewState.showSubEvent(eventId, subEvent.id)

    override fun onOrganizationClick(orgId: String) = viewState.showOrganization(orgId)
    override fun onSpeakerClick(memberId: Int) = viewState.showSpeakerProfile(memberId, eventId)
    override fun onShowAllSpeakersClick() = viewState.showSpeakers(eventId)

    override fun onShareClick() = viewState.showShare(eventId)
    override fun onAddEventToCalendarClick() = viewState.addEventToCalendar(aboutEventData.event)


    override fun onShowAuthorization(id: String) {
        appData.savedEventId = id
        viewState.showAuthorization()
    }

    override fun onChangeAppBarBackgroundColor(value: Int) {
        scrollOffset = value
        viewState.updateAppBarBackgroundColor(scrollOffset)
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
                    } catch (e: Exception) {
                    }
                }

                else -> onReceiveError(t)
            }
        } else onReceiveError(t)
    }

}

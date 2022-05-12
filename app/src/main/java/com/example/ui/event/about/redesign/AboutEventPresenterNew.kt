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
import com.example.ui.eventTabs.EventTabsPresenter
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withDelay
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


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showLoadingDialog()
        val userEventInfo = userEventData.userEvent?.eventInfo
        val eventInfoMaybe =
            if (userEventInfo?.event?.id.toString() == eventId) Maybe.just(userEventInfo)
            else eventRepository.getEventDetails(eventId)
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)

        compositeDisposable += eventInfoMaybe
            .subscribeSimple(
                onSuccess = {
                    setEventInfoData(it)
                    viewState.setSubEvents(emptyList())
                },
                onError = {
                    it.printStackTrace()
                })

        Log.e("TOKEN", appData.token!!)

    }

    private fun setEventInfoData(eventInfo: EventInfo?) {
        this.event = eventInfo
        Log.e("EVENT ID", eventId)
        viewState.apply {
            setEventData(
                eventInfo?.event,
                /*if (eventInfo?.event?.binds?.userRegister?.isNotEmpty() == true) eventInfo.event.binds?.userRegister?.get(0)?.status?.value else null*/
                eventInfo?.event?.binds?.currentUserRegistration?.status?.value,
                eventInfo?.event?.binds?.page,
                eventInfo?.event?.binds?.partner,
                hasContacts(),
                eventInfo?.event?.userAgreement?.name ?: eventInfo?.event?.userAgreement?.uri
            )
            getSubEvents()
        }

    }


    private fun getSubEvents(){
        compositeDisposable += userEventData.loadEventData(eventId)
            .withCheckInternetConnectivity()
            .withDelay(1000)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                if (!it.activity.activities.isNullOrEmpty()){
                    viewState.setSubEvents(it.activity.activities)
                }

            }
    }

    override fun onSpeakersClick() {
        checkInternetAndRun { viewState.showSpeakers(eventId) }
    }

    override fun onRateClick() {
        viewState.showRating(eventId)
    }

    override fun onAgreementClick() {
        val agreement = event?.event?.userAgreement?.name ?: event?.event?.userAgreement?.uri
        agreement?.takeIf { it.isNotEmpty() }?.let { viewState.showAgreement(it) }
        //event.event.userAgreement?.takeIf { it.isNotEmpty() }?.let { viewState.showAgreement(it) }
    }

    override fun onPageClick(page: Int/*EventPage*/) {
        checkInternetAndRun { viewState.showPage(eventId, page.toString()) }
    }

    override fun onPartnerClick(partner: Int/*EventParther*/) {
        checkInternetAndRun { viewState.showPartner(eventId, partner.toString()) }
    }

    override fun onContactsClick() {
        val eventData = event?.event
        viewState.showContacts(
            /*eventData.name,
            eventData.phone,
            eventData.email,
            eventData.web,
            eventData.social,
            eventData.address,
            eventData.place,
            eventData.createMapInfo(),
            event.places*/
            eventData?.name ?: "",
            eventData?.phone ?: arrayListOf(),
            eventData?.email ?: arrayListOf(),
            eventData?.site?.map { s -> s.value ?: "" },
            eventData?.socialLink?.map { l -> l.value ?: "" },
            eventData?.address?.fullValue,
            eventData?.address?.description?.place,
            MapInfo(
                eventData?.address?.lat,
                eventData?.address?.lon,
                eventData?.address?.description?.title,
                eventData?.address?.description?.description
            ),
            /*event.places*/arrayListOf()
        )
    }

    private fun hasContacts(): Boolean {
        val eventData = event?.event
        /*return eventData.name.isNotEmpty()
                || eventData.phone.isNotEmpty()
                || eventData.email.isNotEmpty()
                || eventData.web.isNotEmpty()
                || eventData.social.isNotEmpty()
                || eventData.address?.isNotEmpty() ?: false
                || eventData.place?.isNotEmpty() ?: false
                || eventData.createMapInfo() != null
                || event.places.isNotEmpty()*/
        return eventData?.name?.isNotEmpty() == true
                || eventData?.phone?.isNotEmpty() == true
                || eventData?.email?.isNotEmpty() == true
                || eventData?.site?.isNotEmpty() == true
                || eventData?.socialLink?.isNotEmpty() == true
                || eventData?.address?.getShortAddress()?.isNotEmpty() ?: false
                || eventData?.address?.description?.place?.isNotEmpty() ?: false
                || /*eventData.createMapInfo()*/eventData?.address?.description != null
        /*|| event.places.isNotEmpty()*/
    }

    override fun onGoToEventClick() {
        if (event?.event?.binds?.currentUserRegistration == null || event?.event?.binds?.currentUserRegistration?.status?.value == Event.Status.CANCELED) {
            viewState.showEventRequest(eventId)
        }


        /*compositeDisposable += eventRepository.checkUserProfile()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple(
                        onError = {
                            checkRegistrationFields(emptyList())
                        },
                        onSuccess = {
                            checkRegistrationFields(it.fields?: emptyList())
                        }
                )*/
    }

    private fun checkRegistrationFields(fields: List<UserProfileFields/*EventRegisterCheckField*/>) {
        val filtered = fields.filter { it.filled == false }.mapNotNull { it.name }
        //val filtered = fields.mapNotNull { it.title }
        if (filtered.isEmpty()) {
            if (event?.event?.binds?.currentUserRegistration == null)
                viewState.showEventRequest(eventId)
        } else {
            viewState.showRegistrationFieldsRequest(filtered)
        }
    }

    override fun onShowEditProfileClick() {
        viewState.showEditProfile(appData.getId().toString())
    }

    override fun onShareClick() = viewState.showShare()

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
        viewState.showSubEvent(eventId, subEvent.id.toString())
        //viewState.showSubEvent(userEvent.eventId, subEvent.id.toString())
        checkInternetAndRun {

        }
    }

    override fun onSpeakerClick(speakerId: Int) {
        viewState.showSpeakerProfile(speakerId)
    }

    override fun onMapPageSelected() {
        viewState.apply {
            val eventInfo = event
            showMap(
                eventInfo?.event?.createMapInfo(),
            )
        }
    }


    protected open fun processChangeEventInCalendarStatusRequest(
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
                    viewState.updateSubevent(subEvent)
                })


    }


    override fun onSelectEventClick() {
        compositeDisposable += eventRepository.addEventToCalendar(
            EventCalendarBody(
                appData.getId(),
                EventCalendarBodyEntity(EventCalendarBody.CALENDAR_EVENT, eventId.toInt())
            )
        )
            .andThen(userRepository.getUserShortNew().ignoreElement().onErrorComplete())
            .andThen(userEventData.load(eventId))
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    Log.ERROR
                },
                onComplete = {
                    viewState.selectEvent()
                },
                onNoInternetConnectionException = {
                })

        /*compositeDisposable += eventRepository.setDefaultEvent(eventId)
                .andThen(userRepository.getUserShort().ignoreElement().onErrorComplete())
                .andThen(userEventData.load(eventId))
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple { viewState.selectEvent() }*/
    }

    override fun onLogoClick() {
        val url = /*event.event.backgroundImage*/event?.event?.binds?.organization?.logo?.uri
        if (url != null) viewState.showLogoImage(url)
    }

    override fun onRefreshRequest() {
        compositeDisposable += eventRepository.getEventDetails(eventId)
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(onSuccess = ::setEventInfoData)
    }

    override fun onShowFilterClick(format: Int) {

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

    override fun onWriteToOrganizationClick() {
        viewState.showWriteToOrganizationForm()
    }

    override fun onWriteToOrganizationMessage(message: String) {
        compositeDisposable += eventRepository.mailToEvent(
            MessageToEventBody(
                message,
                appData.getId(),
                eventId.toInt()
            )
        )
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
            .subscribeSimple(
                onComplete = {
                    viewState.apply {
                        hideWriteToOrganizationForm()
                        showWriteToOrganizationComplete()
                    }
                },
                onError = {
                    viewState.showWriteToOrganizationError()
                })
    }

    override fun onOrganizationClick(organization: String) {
        viewState.showOrganization(organization)
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

    override fun onActionWriteToOrganization() {
        val emails = event?.event?.email
        if (!emails.isNullOrEmpty()) viewState.showWriteToOrganizationEmails(emails)
    }

    override fun onWriteToOrganizationEmailChosen(email: EventPhoneModel/*EmailAffiliation*/) {
        viewState.showWriteToOrganization(email)
    }
}

package com.example.ui.event.about

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
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class AboutEventPresenter
@Inject constructor(
        private val appData: AppData,
        private val eventRepository: EventRepository,
        private val userEventData: UserEventData,
        private val userRepository: UserRepository
) : BasePresenter<AboutEventContract.View>(), AboutEventContract.Presenter {

    lateinit var eventId: String
    private var event: EventInfo? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showLoadingDialog()

        val userEventInfo = userEventData.userEvent?.eventInfo

        val eventInfoMaybe = if (userEventInfo?.event?.id.toString() == eventId) Maybe.just(userEventInfo)
        else eventRepository.getEventDetails(eventId)
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
        compositeDisposable += eventInfoMaybe
                .subscribeSimple(onSuccess = ::setEventInfoData)
    }

    private fun setEventInfoData(eventInfo: EventInfo?) {
        this.event = eventInfo
        //val event = eventInfo
        val event = eventInfo?.event
        viewState.apply {
            /*setEventData(
                    event,
                    eventInfo.userRegistration?.status,
                    eventInfo.pages,
                    eventInfo.partners,
                    hasContacts(),
                    event.userAgreement
            )

            setActionButton(
                    event,
                    eventInfo.userRegistration?.status
            )*/
            setEventData(
                    eventInfo?.event,
                    /*if (eventInfo?.event?.binds?.userRegister?.isNotEmpty() == true) eventInfo.event.binds?.userRegister?.get(0)?.status?.value else null*/eventInfo?.event?.binds?.currentUserRegistration?.status?.value,
                    eventInfo?.event?.binds?.page,
                    eventInfo?.event?.binds?.partner,
                    hasContacts(),
                    eventInfo?.event?.userAgreement?.name?: eventInfo?.event?.userAgreement?.uri
            )

            setActionButton(
                    eventInfo?.event,
                    eventInfo?.event?.binds?.currentUserRegistration?.status?.value
                    /*if (eventInfo?.event?.binds?.userRegister?.isNotEmpty() == true) eventInfo.event.binds?.userRegister?.get(0)?.status?.value else null*/
            )
        }
    }

    override fun onSpeakersClick() {
        checkInternetAndRun { viewState.showSpeakers(eventId) }
    }

    override fun onRateClick() {
        viewState.showRating(eventId)
    }

    override fun onAgreementClick() {
        val agreement = event?.event?.userAgreement?.name?: event?.event?.userAgreement?.uri
        agreement?.takeIf { it.isNotEmpty() }?.let { viewState.showAgreement(it) }
        //event.event.userAgreement?.takeIf { it.isNotEmpty() }?.let { viewState.showAgreement(it) }
    }

    override fun onPageClick(page: PageModel/*EventPage*/) {
        checkInternetAndRun { viewState.showPage(eventId, page.id.toString()) }
    }

    override fun onPartnerClick(partner: PartnerModel/*EventParther*/) {
        checkInternetAndRun { viewState.showPartner(eventId, partner.id.toString()) }
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
                eventData?.name?: "",
                eventData?.phone?: arrayListOf(),
                eventData?.email?: arrayListOf(),
                eventData?.site?.map { s -> s.value?: "" },
                eventData?.socialLink?.map { l -> l.value?: "" },
                eventData?.address?.getShortAddress(),
                eventData?.address?.description?.place,
                MapInfo(eventData?.address?.lat, eventData?.address?.lon, eventData?.address?.description?.title, eventData?.address?.description?.description),
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
        compositeDisposable += eventRepository.checkUserProfile()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple(
                        onError = {
                            checkRegistrationFields(emptyList())
                        },
                        onSuccess = {
                            checkRegistrationFields(it.fields?: emptyList())
                        }
                )
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

    override fun onSelectEventClick() {
        compositeDisposable += eventRepository.addEventToCalendar(EventCalendarBody(appData.getId(), EventCalendarBodyEntity(EventCalendarBody.CALENDAR_EVENT, eventId.toInt())))
                .andThen(userRepository.getUserShortNew().ignoreElement().onErrorComplete())
                .andThen(userEventData.load(eventId))
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple { viewState.selectEvent() }

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
                    .withLoadingDialog(viewState)
                    .subscribeSimple {
                        event?.event?.binds?.userFavorite = null
                        viewState.changeEventSubscription(false)
                    }
        } else {
            compositeDisposable += eventRepository.addToFavorites(AddToFavoriteModel(appData.getId(), AddToFavoriteEntityModel(AddToFavoriteEntityModel.FAVORITE_EVENT, eventId.toInt())))
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribeSimple {
                        event?.event?.binds?.userFavorite = EventUserFavorite(it.id, it.user)
                        viewState.changeEventSubscription(true)
                    }
        }
    }

    override fun onWriteToOrganizationClick() {
        viewState.showWriteToOrganizationForm()
    }

    override fun onWriteToOrganizationMessage(message: String) {
        compositeDisposable += eventRepository.mailToEvent(message, eventId, false, false)
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
        compositeDisposable += eventRepository.cancelRegisterToEvent(event?.event?.binds?.currentUserRegistration?.id?: 0)
                .andThen(eventRepository.getEventDetails(eventId))
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    viewState.setActionButton(it.event, it?.event?.binds?.currentUserRegistration?.status?.value)
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

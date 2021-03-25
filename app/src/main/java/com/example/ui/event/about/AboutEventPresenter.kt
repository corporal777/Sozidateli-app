package com.example.ui.event.about

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.bodies.AddToFavoriteEntityModel
import com.example.data.bodies.AddToFavoriteModel
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
    private lateinit var event: EventInfo

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

    private fun setEventInfoData(eventInfo: EventInfo) {
        this.event = eventInfo
        val event = eventInfo
        viewState.apply {
            setEventData(
                    eventInfo.event,
                    eventInfo.event.binds?.userRegister?.status?.value,
                    eventInfo.event.binds?.page,
                    eventInfo.event.binds?.partner,
                    hasContacts(),
                    eventInfo.event.userAgreement?.name
            )

            setActionButton(
                    eventInfo.event,
                    eventInfo.event.binds?.userRegister?.status?.value
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
        event.event.userAgreement?.name?.takeIf { it.isNotEmpty() }?.let { viewState.showAgreement(it) }
    }

    override fun onPageClick(page: PageModel) {
        checkInternetAndRun { viewState.showPage(eventId, page.id.toString()) }
    }

    override fun onPartnerClick(partner: PartnerModel) {
        checkInternetAndRun { viewState.showPartner(eventId, partner.id.toString()) }
    }

    override fun onContactsClick() {
        val eventData = event.event
        viewState.showContacts(
                eventData.name?: "",
                eventData.phone?: arrayListOf(),
                eventData.email?: arrayListOf(),
                eventData.site?.map { s -> s.value?: "" },
                eventData.socialLink?.map { l -> l.value?: "" },
                eventData.address?.getShortAddress(),
                eventData.address?.description?.place,
                MapInfo(eventData.address?.lat, eventData.address?.lon, eventData.address?.description?.title, eventData.address?.description?.description),
                /*event.places*/arrayListOf()
        )
    }

    private fun hasContacts(): Boolean {
        val eventData = event.event
        return eventData.name?.isNotEmpty() == true
                || eventData.phone?.isNotEmpty() == true
                || eventData.email?.isNotEmpty() == true
                || eventData.site?.isNotEmpty() == true
                || eventData.socialLink?.isNotEmpty() == true
                || eventData.address?.getShortAddress()?.isNotEmpty() ?: false
                || eventData.address?.description?.place?.isNotEmpty() ?: false
                || /*eventData.createMapInfo()*/eventData.address?.description != null
                /*|| event.places.isNotEmpty()*/
    }

    override fun onGoToEventClick() {
        compositeDisposable += eventRepository.eventRegisterCheck(eventId)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple(
                        onError = {
                            checkRegistrationFields(emptyList())
                        },
                        onSuccess = {
                            checkRegistrationFields(it)
                        }
                )
    }

    private fun checkRegistrationFields(fields: List<EventRegisterCheckField>) {
        val filtered = fields.mapNotNull { it.title }
        if (fields.isEmpty()) {
            viewState.showEventRequest(eventId)
        } else {
            viewState.showRegistrationFieldsRequest(filtered)
        }
    }

    override fun onShowEditProfileClick() {
        viewState.showEditProfile(appData.getUser().user_id.toString())
    }

    override fun onSelectEventClick() {
        compositeDisposable += userRepository.getUserShort().ignoreElement().onErrorComplete()
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
        val url = event.event.binds?.organization?.logo?.uri
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
        if (event.event.binds?.userFavorite != null) {
            compositeDisposable += eventRepository.deleteFromFavorite(event.event.binds?.userFavorite?.id.toString())
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribeSimple {
                        event.event.binds?.userFavorite = null
                        viewState.changeEventSubscription(false)
                    }
        } else {
            compositeDisposable += eventRepository.addToFavorites(AddToFavoriteModel(appData.getId(), AddToFavoriteEntityModel(AddToFavoriteEntityModel.FAVORITE_EVENT, eventId.toInt())))
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribeSimple {
                        event.event.binds?.userFavorite = EventUserFavorite(it.id, it.user)
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
        /*compositeDisposable += eventRepository.eventRegisterCancel(eventId)
                .andThen(eventRepository.getEventInfo(eventId))
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    viewState.setActionButton(it.event, it.userRegistration?.status)
                }*/
    }

    override fun onActionWriteToOrganization() {
        val emails = event.event.email
        if (!emails.isNullOrEmpty()) viewState.showWriteToOrganizationEmails(emails)
    }

    override fun onWriteToOrganizationEmailChosen(email: EventPhoneModel) {
        viewState.showWriteToOrganization(email)
    }
}

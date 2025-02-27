package com.example.ui.event.about

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.app.BuildConfig
import com.example.app.R
import com.example.app.databinding.FragmentAboutEventBinding
import com.example.data.models.AboutEventData
import com.example.data.models.EventActivityModel
import com.example.data.models.EventFormModel
import com.example.data.models.EventNew
import com.example.data.models.NewTags
import com.example.data.models.Tag
import com.example.extensions.dp
import com.example.extensions.findItemBy
import com.example.extensions.onScrolled
import com.example.extensions.px
import com.example.extensions.setArgument
import com.example.extensions.setOnClickListener
import com.example.extensions.statusBarColorValue
import com.example.extensions.topMargin
import com.example.extensions.updateItems
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventActivityItem
import com.example.holders.redesign.EventPartnerItem
import com.example.ui.base.BaseActivity
import com.example.ui.base.BaseVBFragment
import com.example.ui.event.about.items.AboutEventLabelItem
import com.example.ui.event.about.items.EventDetailActivitiesItem
import com.example.ui.event.about.items.EventDetailImageItem
import com.example.ui.event.about.items.EventDetailOrganizationItem
import com.example.ui.event.about.items.EventDetailShowActivitiesItem
import com.example.ui.event.about.items.SpeakersHorizontalListItem
import com.example.ui.event.about.items.TagsItem
import com.example.ui.event.activities.ActivitiesFragmentArgs
import com.example.ui.event.formResult.EventFormResultFragment
import com.example.ui.event.formResult.EventFormResultFragment.Companion.EVENT_FORM_FRAGMENT_TAG
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.event.speakers.list.EventSpeakersFragmentArgs
import com.example.ui.event.speakers.member.UserSpeakerFragmentArgs
import com.example.ui.main.MainActivity
import com.example.ui.organizations.detail.OrganizationFragmentArgs
import com.example.ui.partner.PartnerFragmentArgs
import com.example.ui.subevent.SubEventFragmentArgs
import com.example.ui.views.LinearLayoutManagerAccurateOffset
import com.example.ui.views.dialogs.DefaultAlertDialog
import com.example.ui.views.dialogs.EventAgreementBottomSheet
import com.example.ui.views.dialogs.EventDetailInformationBottomSheetDialog
import com.example.ui.views.dialogs.StateType
import com.example.util.SYSTEM_UI_LIGHT_STATUS_BAR
import com.example.util.openDeviceCalendarApp
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.Section
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.abs


class AboutEventFragment : BaseVBFragment<FragmentAboutEventBinding>(), AboutEventContract.View {

    @InjectPresenter
    lateinit var presenter: AboutEventPresenter

    @Inject
    lateinit var presenterProvider: Provider<AboutEventPresenter>

    @ProvidePresenter
    fun providePresenter(): AboutEventPresenter = presenterProvider.get().apply {
        eventId = AboutEventFragmentArgs.fromBundle(requireArguments()).eventId
    }


    private val eventMainSection = Section().apply {
        add(PlaceholderItem(PlaceholderItem.Type.EVENT_MAIN))
    }
    private val eventSpeakersSection = Section().apply {
        setHeader(AboutEventLabelItem(R.string.speakers))
        setHideWhenEmpty(true)
    }
    private val eventProgramSection = Section().apply {
        setHeader(AboutEventLabelItem(R.string.event_program))
        setFooter(EventDetailShowActivitiesItem { presenter.onShowSubEventsClick() })
        setHideWhenEmpty(true)
    }
    private val eventPartnersSection = Section().apply {
        setHeader(AboutEventLabelItem(R.string.partners_label))
        setHideWhenEmpty(true)
    }


    private val offsetMap = mutableMapOf<Int, Int>()
    private val groupAdapter = GroupieAdapter().apply {
        add(eventMainSection)
        add(eventSpeakersSection)
        add(eventProgramSection)
        add(eventPartnersSection)
    }


    private val onActionClickListener = object : EventDetailImageItem.OnActionClickListener {
        override fun onActionRegister() = presenter.onActionRegister(false)
        override fun onActionCancel() = presenter.onActionCancel()
        override fun onShowUpdateState() = showStateErrorMessage(StateType.BASE, false, null)
        override fun onSubscribeEvent(subscribe: Boolean) = presenter.onSubscribeEvent(subscribe)
        override fun onShowNeedAuth() = presenter.onShowAuthorization()
    }

    private val onSubEventClickListener = object : EventActivityItem.OnEventActivityClickListener {
        override fun onSubEventClick(eventId: String, subEvent: EventActivityModel) = presenter.onSubEventClick(subEvent.id ?: 0)
        override fun onAddToScheduleClick(subEvent: EventActivityModel) = presenter.onAddToScheduleClick(subEvent)
        override fun onRemoveFromScheduleClick(subEvent: EventActivityModel) = presenter.onAddToScheduleClick(subEvent)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            eventContentList.apply {
                layoutManager = LinearLayoutManagerAccurateOffset(requireContext(), offsetMap)
                adapter = groupAdapter
                onScrolled { dx, _ ->
                    presenter.onChangeAppBarBackgroundColor(computeVerticalScrollOffset())
                }
            }
            toolbar.apply {
                ivShare.setOnClickListener(presenter::onShareClick)
                ivBack.setOnClickListener(findNavController()::navigateUp)
                btnAddToCalendar.setOnClickListener(presenter::onAddEventToCalendarClick)
            }
            swipeToRefreshLayout.setOnRefreshListener { presenter.onRefreshRequest() }
        }
    }

    override fun setEventData(eventData: AboutEventData) {
        setEventFavoriteButton(eventData.event.binds?.userFavorite != null)
        mBinding.toolbar.tbContent.isVisible = true
        mBinding.swipeToRefreshLayout.isRefreshing = false

        eventMainSection.updateItems(
            EventDetailImageItem(eventData.event,
                requireContext(),
                presenter.isTemporaryUser(),
                onActionClickListener,
                { showEventInformationDialog(eventData.event) },
                { showEventFormResult(eventData.event) }
            ),
            EventDetailOrganizationItem(eventData.event.binds?.organization,
                { presenter.onAddOrganizationToFavoriteClick() },
                { presenter.onOrganizationClick(it) }
            )
        )
        if (!eventData.speakers.isNullOrEmpty()) {
            eventSpeakersSection.updateItems(
                SpeakersHorizontalListItem(
                    eventData.speakers,
                    eventData.showMoreSpeakers,
                    { presenter.onSpeakerClick(it) },
                    { presenter.onShowAllSpeakersClick() })
            )
        }

        if (!eventData.event.isHasOneActivity()) {
            eventProgramSection.updateItems(
                if (eventData.tags.isNotEmpty()) TagsItem(eventData.tags) { presenter.onTagSelected() }
                else null,
                eventData.subEvents.map {
                    EventDetailActivitiesItem(
                        eventData.event.id.toString(),
                        eventData.getUserRegistrationState(),
                        it.key,
                        it.value,
                        onSubEventClickListener
                    )
                })
        }

        eventPartnersSection.update(
            eventData.partners.map {
                EventPartnerItem(
                    requireContext(),
                    it.id,
                    it.name,
                    it.description,
                    it.logo?.uri ?: it.image?.uri
                ) { id -> presenter.onPartnerClick(id) }
            }
        )
    }


    override fun updateSubEvent(subEvent: EventActivityModel) {
        val idLong = subEvent.id?.toLong()
        eventProgramSection.findItemBy<EventActivityItem> { it.id == idLong }
            ?.notifyChanged(subEvent)
    }

    override fun updateTags(tag: Tag) {
        eventProgramSection.findItemBy<TagsItem> { true }?.updateTag(tag)
    }

    override fun updateOrganization(isSubscribed: Boolean) {
        val item = eventMainSection.findItemBy<EventDetailOrganizationItem> { true }
        item?.notifyChanged(isSubscribed)
    }

    override fun setActionButton(event: EventNew) {
        val item = eventMainSection.findItemBy<EventDetailImageItem> { true }
        item?.notifyChanged(event)
    }

    override fun showEventSubscribedDialog(isSubscribed: Boolean?) {
        if (isSubscribed == true)
            DefaultAlertDialog(requireContext(), "Спасибо", "Мы пришлем Вам уведомление")
        else DefaultAlertDialog(
            requireContext(),
            null,
            "Вы отписались от уведомления о старте приема заявок на мероприятие",
            "Отмена",
            "Ок",
            false
        ).setSelectCallback { presenter.onSubscribeEvent(isSubscribed ?: false) }
    }

    override fun showAgreementDialog(event: String, url: String?) {
        EventAgreementBottomSheet(requireContext(), url?:"")
            .setSelectCallback { if (it) presenter.onActionRegister(true) }
            .show()
    }


    private fun showEventFormResult(event: EventNew) {
        if (event.binds?.userFormResult.isNullOrEmpty()) return
        EventFormResultFragment()
            .setArgument<EventFormResultFragment>(EVENT_FORM_FRAGMENT_TAG, event)
            .show(requireActivity().supportFragmentManager)
    }

    private fun showEventInformationDialog(event: EventNew) {
        EventDetailInformationBottomSheetDialog(requireActivity(), event).show()
    }

    override fun showSubEvent(eventId: String, subEventId: Int?) {
        val args = SubEventFragmentArgs.Builder(eventId, subEventId.toString()).build().toBundle()
        findNavController().navigate(R.id.subEvent_fragment, args)
    }

    override fun showEventActivities(eventId: String, listTags: List<NewTags>) {
        val args = ActivitiesFragmentArgs.Builder(eventId.toInt())
            .setTags(listTags.toTypedArray())
            .build().toBundle()
        findNavController().navigate(R.id.activitiesFragment, args)
    }

    override fun showSpeakerProfile(speakerId: Int, eventId: String) {
        val args = UserSpeakerFragmentArgs.Builder(speakerId.toString(), eventId).build().toBundle()
        findNavController().navigate(R.id.user_speaker_fragment, args)
    }


    override fun showPartner(eventId: String, partnerId: String) {
        val args = PartnerFragmentArgs.Builder(eventId, partnerId).build().toBundle()
        findNavController().navigate(R.id.partner_fragment, args)
    }

    override fun showSpeakers(eventId: String) {
        val args = EventSpeakersFragmentArgs.Builder(eventId).build().toBundle()
        findNavController().navigate(R.id.speakers_list_fragment, args)
    }


    override fun showEventRequest(event: String) {
        val args = EventRegistrationFragmentArgs.Builder(event).build().toBundle()
        findNavController().navigate(R.id.request_fragment, args)
    }

    override fun showOrganization(organization: String) {
        val args = OrganizationFragmentArgs.Builder(organization).build().toBundle()
        findNavController().navigate(R.id.organization_fragment_new, args)
    }

    override fun showAuthorization() {
        findNavController().navigate(R.id.authorization_fragment)
    }

    override fun showErrorMessageWithResult(with: Boolean, eventId: String, message: String) {
        DefaultAlertDialog(requireContext(), null, message).setSelectCallback {
            if (with) setFragmentResult("eventKey", bundleOf("eventId" to eventId))
            findNavController().navigateUp()
        }
    }

    override fun setEventFavoriteButton(isSubscribed: Boolean) {
        mBinding.toolbar.ivAddToFavorite.apply {
            setImageResource(if (!isSubscribed) R.drawable.ic_star else R.drawable.ic_star_filled)
            setOnClickListener { presenter.onAddEventToFavoriteClick() }
        }
    }

    override fun showShare(eventId: String) {
        try {
            val link = BuildConfig.SHARE_URL + "portal/event/$eventId"
            val shareApp = Intent(Intent.ACTION_SEND)
            shareApp.type = "text/plain"

            shareApp.putExtra(Intent.EXTRA_TEXT, link)
            startActivity(Intent.createChooser(shareApp, "Choose one of the:"))
        } catch (e: Exception) {
            showRequestErrorMessage()
        }
    }

    override fun addEventToCalendar(eventData: EventNew?) {
        if (eventData == null) return
        openDeviceCalendarApp(
            requireContext(),
            eventData.holdingDate?.from,
            eventData.holdingDate?.to,
            eventData.name,
            eventData.description,
            eventData.address?.city
        )
    }

    fun setupToolbarTopMargin(margin : Int){
        try {
            mBinding.toolbar.viewSize.topMargin =
                if (margin.px in 20..30) 26.dp
                else if (margin.px in 30..40) 35.dp
                else if (margin.px in 40 .. 50) 45.dp
                else if (margin.px in 50..70) 50.dp
                else 55.dp
        } catch (e : Exception){
            e.printStackTrace()
            mBinding.toolbar.viewSize.topMargin = 26.dp
        }
    }

    override fun updateAppBarBackgroundColor(value: Int) {
        mBinding.toolbar.apply {
            if (value <= 0) {
                requireActivity().statusBarColorValue = 0
                tbBackground.alpha = 0f
                mBinding.cardViewToolbar.cardElevation = 0f
            } else {
                tbBackground.apply { alpha = abs(value / (1450).toFloat()) }
                mBinding.cardViewToolbar.apply {
                    if (value >= 1450) {
                        requireActivity().statusBarColorValue = SYSTEM_UI_LIGHT_STATUS_BAR
                        val elevationValue = abs(value / 100f)
                        cardElevation = if (elevationValue <= 10f) abs(value / 100f) else 10f
                    } else {
                        requireActivity().statusBarColorValue = 0
                        cardElevation = 0f
                    }
                }
            }
        }
    }

    override fun binding() = FragmentAboutEventBinding::class.java
    override fun layout() = R.layout.fragment_about_event
}

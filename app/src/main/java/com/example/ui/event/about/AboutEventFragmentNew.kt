package com.example.ui.event.about

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.BuildConfig
import com.example.R
import com.example.data.models.*
import com.example.databinding.FragmentAboutEventNewBinding
import com.example.extensions.*
import com.example.holders.redesign.EventActivityItem
import com.example.holders.redesign.EventPartnerItem
import com.example.ui.base.BaseFragmentNew
import com.example.ui.event.about.items.*
import com.example.ui.event.activities.ActivitiesFragmentArgs
import com.example.ui.event.location.buildingScheme.redesign.DestinationSchemeFragmentArgs
import com.example.ui.event.location.map.redesign.MapFragmentNewArgs
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.event.speakers.list.EventSpeakersFragmentArgs
import com.example.ui.event.speakers.member.UserSpeakerFragmentArgs
import com.example.ui.organizations.detail.OrganizationFragmentArgs
import com.example.ui.page.PageFragmentArgs
import com.example.ui.partner.PartnerFragmentArgs
import com.example.ui.subevent.SubEventFragmentArgs
import com.example.ui.views.GridLayoutManagerAccurateOffset
import com.example.ui.views.LinearLayoutManagerAccurateOffset
import com.example.ui.views.StateType
import com.example.ui.views.dialogs_new.EventAddedToFavoriteDialog
import com.example.ui.views.dialogs_new.MessageDialogWithBrownButton
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import onScrolled
import setOnClickListener
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.abs


class AboutEventFragmentNew() : BaseFragmentNew<FragmentAboutEventNewBinding>(),
    AboutEventContractNew.View {

    override fun layout() = R.layout.fragment_about_event_new

    private val eventMainSection by lazy { Section() }
    private val eventOrganizationSection by lazy { Section() }
    private val eventSpeakersSection by lazy {
        Section().apply {
            setHeader(EventDetailBlocksLabelItem(getString(R.string.speakers)))
            setHideWhenEmpty(true)
        }
    }
    private val eventProgramSection by lazy { Section() }
    private val eventPartnersSection by lazy {
        Section().apply {
            setHeader(EventDetailBlocksLabelItem(getString(R.string.partners_label)))
            setHideWhenEmpty(true)
        }
    }

    @InjectPresenter
    lateinit var mPresenter: AboutEventPresenterNew

    @Inject
    lateinit var presenterNewProvider: Provider<AboutEventPresenterNew>

    @ProvidePresenter
    fun providePresenter(): AboutEventPresenterNew = presenterNewProvider.get().apply {
        eventId = AboutEventFragmentNewArgs.fromBundle(requireArguments()).eventId
    }

    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(eventMainSection)
            add(eventOrganizationSection)
            add(eventSpeakersSection)
            add(eventProgramSection)
            add(eventPartnersSection)
        }
    }


    private val onActionClickListener = object : EventDetailActionItem.OnActionClickListener {
        override fun onActionRegister() = mPresenter.onActionRegister()
        override fun onActionCancel() = mPresenter.onActionCancel()
        override fun onShowUpdateState() = showStateErrorMessage(StateType.BASE, false, null)
        override fun onSubscribeEvent() = mPresenter.onCreateEventSubscriptionClick()
        override fun onDeleteSubscribeEvent() = mPresenter.onDeleteEventSubscriptionClick()


    }

    private val onSubEventClickListener = object : EventActivityItem.OnEventActivityClickListener {
        override fun onSubEventClick(eventId: String, subEvent: EventActivityModel) {
            mPresenter.onSubEventClick(subEvent)
        }

        override fun onAddToScheduleClick(subEvent: EventActivityModel) =
            mPresenter.onAddToScheduleClick(subEvent)

        override fun onRemoveFromScheduleClick(subEvent: EventActivityModel) =
            mPresenter.onRemoveFromScheduleClick(subEvent)
    }

    private val customLayoutManager by lazy {
        GridLayoutManagerAccurateOffset(requireContext(), groupAdapter.spanCount).apply {
            spanSizeLookup = groupAdapter.spanSizeLookup
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            eventContentList.apply {
                setItemViewCacheSize(50)
                adapter = groupAdapter
                layoutManager = customLayoutManager
                onScrolled { _, _ ->
                    mPresenter.changeAppBarBackgroundColorValue(this.computeVerticalScrollOffset())
                }
            }
            toolbar.apply {
                ivShare.setOnClickListener(mPresenter::onShareClick)
                ivBack.setOnClickListener(findNavController()::navigateUp)
                btnAddToCalendar.setOnClickListener {
                    mPresenter.onAddEventToCalendarClick()
                }
            }

            swipeToRefresh.apply {
                setProgressViewOffset(
                    true,
                    resources.getDimensionPixelSize(R.dimen.swipe_distance_start_margin),
                    resources.getDimensionPixelSize(R.dimen.swipe_distance_end_margin)
                )
                setOnRefreshListener {
                    mPresenter.onRefreshRequest()
                }
            }
        }

    }


    override fun setEventData(eventData: AboutEventData) {
        decorEventFavoriteButton(eventData.event.binds?.userFavorite != null)

        eventMainSection.update(
            listOf(
                EventDetailImageItem(
                    eventData.event.name,
                    eventData.event.address?.getShortAddress(),
                    eventData.event.holdingDate?.from,
                    eventData.event.holdingDate?.to,
                    eventData.event.image?.uri,
                    eventData.event.backgroundColor?.value,
                    eventData.event.requestsApply
                ),
                EventDetailActionItem(requireContext(), eventData.event, onActionClickListener)
            )
        )
        eventOrganizationSection.updateGroup(
            EventDetailInfoBlock(
                eventData.event.binds?.organization,
                { mPresenter.onAddOrganizationToFavoriteClick() },
                { mPresenter.onOrganizationClick(it) },
                getString(R.string.information),
                eventData.event.address?.fullValue,
                eventData.event.binds?.page,
                { mPresenter.onMapPageSelected() },
                { mPresenter.onPageClick(it) }
            )
        )

        if (!eventData.speakers.isNullOrEmpty()) {
            eventSpeakersSection.updateItem(
                SpeakersHorizontalListItem(
                    eventData.speakers,
                    eventData.showMoreSpeakers,
                    { mPresenter.onSpeakerClick(it) },
                    { mPresenter.onShowAllSpeakersClick() })
            )
        }

        if (!eventData.subEvents.isNullOrEmpty() || !eventData.tags.isNullOrEmpty()) {
            eventProgramSection.apply {
                setHeader(EventDetailBlocksLabelItem(getString(R.string.event_program)))
                update(
                    if (!eventData.tags.isNullOrEmpty()) {
                        listOf(TagsItem(eventData.tags) {
                            mPresenter.onTagSelected()
                        })
                    } else {
                        emptyList()
                    }
                        .plus(eventData.subEvents.map {
                            EventDetailActivitiesItem(
                                eventData.event.id.toString(),
                                eventData.getUserRegistrationState(),
                                it.key,
                                it.value,
                                onSubEventClickListener
                            )
                        })
                )
                if (eventData.showMoreSubEvents) {
                    setFooter(EventDetailShowActivitiesItem {
                        mPresenter.onShowEventActivitiesClick()
                    })
                }
            }
        }

        eventPartnersSection.update(
            eventData.partners.map {
                EventPartnerItem(
                    it.id,
                    it.name,
                    it.description,
                    it.logo?.uri ?: it.image?.uri
                ) { id -> mPresenter.onPartnerClick(id) }
            }
        )
        groupAdapter.notifyDataSetChanged()
        mBinding.swipeToRefresh.isRefreshing = false
    }


    override fun updateSubEvent(subEvent: EventActivityModel) {
        val idLong = subEvent.id?.toLong()
        eventProgramSection.findItemBy<EventActivityItem> { it.id == idLong }
            ?.notifyChanged(subEvent)
    }

    override fun updateTags(tag: Tag) {
        eventProgramSection.findItemBy<TagsItem> { true }?.updateTag(tag)
    }

    override fun changeEventSubscription(isSubscribed: Boolean) {
        decorEventFavoriteButton(isSubscribed)
    }

    override fun changeOrganizationSubscription(isSubscribed: Boolean) {
        val item = eventOrganizationSection.findItemBy<EventDetailOrganizationItem> { true }
        item?.notifyChanged(isSubscribed)
    }

    override fun setActionButton(event: EventNew?) {
        val item = eventMainSection.findItemBy<EventDetailActionItem> { true }
        item?.notifyChanged(event)
    }

    override fun showSubEvent(eventId: String, subEventId: String) {
        val args = SubEventFragmentArgs.Builder(eventId, subEventId).build().toBundle()
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

    override fun showMap(mapInfo: MapInfo?) {
        val args = MapFragmentNewArgs.Builder(mapInfo).build().toBundle()
        findNavController().navigate(R.id.fragment_map_new, args)
    }

    override fun showShare(eventId: String) {
        val mLink = BuildConfig.SHARE_URL + "portal/event/"
        val mShareLink = StringBuilder(mLink).append(eventId).toString()

        try {
            val shareApp = Intent(Intent.ACTION_SEND)
            shareApp.type = "text/plain"

            shareApp.putExtra(Intent.EXTRA_TEXT, mShareLink)
            startActivity(Intent.createChooser(shareApp, "Choose one of the:"))
        } catch (e: Exception) {
            showRequestErrorMessage()
        }
    }


    override fun showPage(eventId: String, pageId: String) {
        findNavController().navigate(
            R.id.page_fragment,
            PageFragmentArgs.Builder(eventId, pageId).build().toBundle()
        )
    }

    override fun showPartner(eventId: String, partnerId: String) {
        findNavController().navigate(
            R.id.partner_fragment,
            PartnerFragmentArgs.Builder(eventId, partnerId).build().toBundle()
        )
    }

    override fun showSpeakers(eventId: String) {
        findNavController().navigate(
            R.id.speakers_list_fragment,
            EventSpeakersFragmentArgs.Builder(eventId).build().toBundle()
        )
    }


    override fun showEventRequest(event: String) {
        findNavController().navigate(
            R.id.request_fragment,
            EventRegistrationFragmentArgs.Builder(event).build().toBundle()
        )
    }

    override fun showErrorMessageWithResult(withResult: Boolean, eventId: String, message: String) {
        MessageDialogWithBrownButton(requireContext(), message).setSelectCallback {
            if (withResult) {
                setFragmentResult("eventKey", bundleOf("eventId" to eventId))
            }
            findNavController().navigateUp()
        }
    }


    override fun showOrganization(organization: String) {
        findNavController().navigate(
            R.id.organization_fragment_new,
            OrganizationFragmentArgs.Builder(organization).build().toBundle()
        )
    }

    private fun decorEventFavoriteButton(isSubscribed: Boolean) {
        mBinding.toolbar.ivAddToFavorite.apply {
            if (!isSubscribed) setImageResource(R.drawable.ic_star)
            else setImageResource(R.drawable.ic_star_filled)
            setOnClickListener {
                mPresenter.onAddEventToFavoriteClick()
            }
        }
    }

    override fun showEventAddedToFavoriteMessage() {
        EventAddedToFavoriteDialog(requireContext())
    }

    override fun addEventToCalendar(eventData: EventNew?) {
        if (eventData != null) {
            try {
                val startCal =
                    defaultServerDateFormatter.parse(eventData.holdingDate?.from ?: "").calendar()
                val endCal =
                    defaultServerDateFormatter.parse(eventData.holdingDate?.to ?: "").calendar()

                val intent: Intent = Intent(Intent.ACTION_INSERT).apply {
                    data = CalendarContract.Events.CONTENT_URI
                    putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startCal.timeInMillis)
                    putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endCal.timeInMillis)
                    putExtra(CalendarContract.Events.TITLE, eventData.name)
                    putExtra(CalendarContract.Events.DESCRIPTION, eventData.description)
                    putExtra(CalendarContract.Events.EVENT_LOCATION, eventData.address?.city)
                    putExtra(
                        CalendarContract.Events.AVAILABILITY,
                        CalendarContract.Events.AVAILABILITY_BUSY
                    )
                }
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun updateAppBarBackgroundColorValue(value: Int) {
        Log.e("OFFSET", value.toString())

        mBinding.toolbar.apply {
            if (value <= 0) tbBackground.alpha = 0f
            else {
                tbBackground.apply { alpha = abs(value / (1450).toFloat()) }
                if (value >= 1450) mBinding.appBar.changeAppBarElevation(abs(value / 100f))
                else mBinding.appBar.changeAppBarElevation(0f)

                requireActivity().window.apply {
                    if (value >= 740) decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    else decorView.systemUiVisibility = 0
                }
            }
        }
    }


    companion object {
        const val ABOUT_FROM_EVENT = 1
        const val ABOUT_FROM_OTHER = 2
    }

}

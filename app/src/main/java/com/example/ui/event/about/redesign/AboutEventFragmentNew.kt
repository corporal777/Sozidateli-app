package com.example.ui.event.about.redesign

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.BuildConfig
import com.example.R
import com.example.data.models.*
import com.example.databinding.FragmentAboutEventNewBinding
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.findItemBy
import com.example.holders.redesign.EventActivityItem
import com.example.holders.redesign.EventPartnerItem
import com.example.ui.event.about.redesign.items.SpeakersHorizontalListItem
import com.example.ui.base.BaseFragment
import com.example.ui.event.about.redesign.items.*
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.event.speakers.list.EventSpeakersFragmentArgs
import com.example.ui.event.speakers.member.UserSpeakerFragmentArgs
import com.example.ui.organizations.redesign.OrganizationFragmentNewArgs
import com.example.ui.page.PageFragmentArgs
import com.example.ui.partner.PartnerFragmentArgs
import com.example.ui.subevent.SubEventFragmentArgs
import com.example.ui.views.StateType
import com.example.ui.views.dialogs_new.EventAddedToFavoriteDialog
import com.example.ui.views.dialogs_new.MessageDialogWithBrownButton
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import onScrolled
import setOnClickListener
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.abs


class AboutEventFragmentNew() : BaseFragment(),
    AboutEventContractNew.View {

    private var mEventId = ""

    override fun layout() = R.layout.fragment_about_event_new

    private val mainBlock = Section()
    private val organizationAndInformationBlock = Section()
    private val speakersBlock by lazy {
        Section().apply {
            setHeader(EventDetailBlocksLabelItem(getString(R.string.speakers)))
            setHideWhenEmpty(true)
        }
    }
    private val tagsBlock by lazy {
        Section().apply {
        }
    }
    private val subEventsBlock = Section()
    private val showActivitiesBlock = Section()

    private val partnersBlock by lazy {
        Section().apply {
            setHeader(EventDetailBlocksLabelItem(getString(R.string.partners_label)))
            setHideWhenEmpty(true)
        }
    }

    private var _binding: FragmentAboutEventNewBinding? = null
    private val mBinding get() = _binding!!

    @InjectPresenter
    lateinit var mPresenter: AboutEventPresenterNew

    @Inject
    lateinit var presenterNewProvider: Provider<AboutEventPresenterNew>

    @ProvidePresenter
    fun providePresenter(): AboutEventPresenterNew = presenterNewProvider.get().apply {
        eventId = AboutEventFragmentNewArgs.fromBundle(requireArguments()).eventId
        this@AboutEventFragmentNew.mEventId = eventId
    }

    private val groupAdapter by lazy {
        GroupAdapter<com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder>().apply {
            add(mainBlock)
            add(organizationAndInformationBlock)
            add(speakersBlock)
            add(tagsBlock)
            add(subEventsBlock)
            add(showActivitiesBlock)
            add(partnersBlock)
        }
    }


    private val onActionClickListener = object : EventDetailActionItem.OnActionClickListener {
        override fun onActionRegister() = mPresenter.onGoToEventClick()
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

    private var actionItem: EventDetailActionItem? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAboutEventNewBinding.bind(view)
        mBinding.eventContentList.apply {
            adapter = groupAdapter
            setItemViewCacheSize(10)
            recycledViewPool.setMaxRecycledViews(0, 10)
            val layoutManager = this.layoutManager as LinearLayoutManager
            onScrolled { _, dy ->
                if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                    mPresenter.changeAppBarBackgroundColorValue(false, 0)
                } else {
                    mPresenter.changeAppBarBackgroundColorValue(true, dy)
                }
            }
        }

        mBinding.ivShare.setOnClickListener(mPresenter::onShareClick)
        mBinding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        mBinding.swipeToRefresh.apply {
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

    override fun setEventData(eventData: EventNew?) {
        mBinding.swipeToRefresh.isRefreshing = false
        if (eventData != null) {
            mBinding.btnAddToCalendar.setOnClickListener {
                addToCalendar(eventData)
            }
            decorEventFavoriteButton(eventData.binds?.userFavorite != null)

            groupAdapter.notifyDataSetChanged()
            mainBlock.update(
                listOf(
                    EventDetailImageItem(
                        eventData.name,
                        eventData.address?.getShortAddress(),
                        eventData.holdingDate?.from,
                        eventData.holdingDate?.to,
                        eventData.image?.uri,
                        eventData.backgroundColor?.value
                    ),
                    EventDetailActionItem(eventData, onActionClickListener).apply {
                        actionItem = this
                    }
                )
            )
        }

    }


    override fun setOrganizationAndInformation(
        organization: OrganizationNew?,
        pages: List<PageModel>?,
        address: String?
    ) {
        organizationAndInformationBlock.update(
            listOf(
                EventDetailInfoBlock(
                    organization,
                    { mPresenter.onAddOrganizationToFavoriteClick() },
                    { mPresenter.onOrganizationClick(it) },
                    getString(R.string.information),
                    address,
                    pages,
                    { mPresenter.onMapPageSelected() },
                    { mPresenter.onPageClick(it) }
                )
            )
        )
    }

    override fun setEventSpeakers(isShowMore: Boolean, members: List<MemberModel>?) {
        if (!members.isNullOrEmpty()) {
            speakersBlock.update(
                listOf(
                    SpeakersHorizontalListItem(
                        members,
                        isShowMore,
                        { mPresenter.onSpeakerClick(it) },
                        { mPresenter.onShowAllSpeakersClick() }),
                )
            )
        }
    }

    override fun setEventActivitiesAndTags(
        isApproved: Boolean,
        subEvents: Map<String, List<EventActivityModel>>,
        tags: List<Tag>
    ) {

        tagsBlock.apply {
            if (!subEvents.isNullOrEmpty() || !tags.isNullOrEmpty()) {
                setHeader(EventDetailBlocksLabelItem(getString(R.string.event_program)))
            }
            if (!tags.isNullOrEmpty()){
                update(
                    listOf(TagsItem(tags) {
                        mPresenter.onTagSelected()
                    })
                )
            }
        }
        if (!subEvents.isNullOrEmpty()) {
            subEventsBlock.update(
                subEvents.map {
                    EventDetailActivitiesItem(
                        mEventId,
                        isApproved,
                        it.key,
                        it.value,
                        onSubEventClickListener
                    )
                }
            )
            showActivitiesBlock.update(listOf(
                EventDetailShowActivitiesItem {
                    mPresenter.onShowEventActivitiesClick()
                }
            ))
        }
    }

    override fun setEventPartners(partners: List<PartnerModel>?) {
        if (!partners.isNullOrEmpty()) {
            partnersBlock.update(
                partners.map {
                    EventPartnerItem(
                        it.id ?: 0,
                        it.name,
                        it.description,
                        it.logo?.uri ?: it.image?.uri
                    ) { id ->
                        mPresenter.onPartnerClick(id)
                    }
                }
            )
        }
    }

    override fun updateSubEvent(subEvent: EventActivityModel) {
        val idLong = subEvent.id?.toLong()
        subEventsBlock.findItemBy<EventActivityItem> { it.id == idLong }
            ?.notifyChanged(subEvent)
    }

    override fun changeEventSubscription(isSubscribed: Boolean) {
        decorEventFavoriteButton(isSubscribed)
    }

    override fun changeOrganizationSubscription(isSubscribed: Boolean) {
        val item = organizationAndInformationBlock.findItemBy<EventDetailOrganizationItem> { true }
        item?.notifyChanged(isSubscribed)
    }

    override fun setActionButton(event: EventNew?) {
        val item = mainBlock.findItemBy<EventDetailActionItem> { true }
        item?.notifyChanged(event)
    }

    override fun showSubEvent(eventId: String, subEventId: String) {
        val args = SubEventFragmentArgs.Builder(eventId, subEventId).build().toBundle()
        findNavController().navigate(R.id.subEvent_fragment, args)
    }

    override fun showEventActivities(eventId: String, listTags: List<NewTags>) {
        findNavController().navigate(
            AboutEventFragmentNewDirections
                .actionAboutEventFragmentToActivitiesFragment(eventId.toInt())
                .setTags(listTags.toTypedArray())
        )
    }

    override fun showSpeakerProfile(speakerId: Int) {
        findNavController().navigate(
            R.id.user_speaker_fragment,
            UserSpeakerFragmentArgs.Builder(speakerId.toString(), mEventId).build()
                .toBundle()
        )
    }

    override fun showMap(mapInfo: MapInfo?) {
        findNavController().navigate(
            AboutEventFragmentNewDirections.actionAboutEventFragmentNewToMapFragmentNew(
                mapInfo
            )
        )
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

    override fun showErrorMessageWithResult(
        withResult: Boolean,
        eventId: String,
        message: String
    ) {
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
            OrganizationFragmentNewArgs.Builder(organization).build().toBundle()
        )
    }

    private fun setBlackIcons() {
        mBinding.apply {
            ivAddToFavorite.imageTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.vk_black)
            ivShare.imageTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.vk_black)
            ivBack.imageTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.vk_black)
            btnAddToCalendar.apply {
                setTextColor(Color.BLACK)
                background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.custom_btn_add_to_calendar_background_black
                )
            }
            requireActivity().window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

    }

    private fun setWhiteIcons() {
        mBinding.apply {
            requireActivity().window.decorView.systemUiVisibility = 0
            btnAddToCalendar.apply {
                setTextColor(Color.WHITE)
                background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.custom_btn_add_to_calendar_background
                )
            }
            ivAddToFavorite.imageTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.white)
            ivShare.imageTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.white)
            ivBack.imageTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.white)
        }

    }


    private fun decorEventFavoriteButton(isSubscribed: Boolean) {
        mBinding.apply {
            ivAddToFavorite.apply {
                setActionAlternative(!isSubscribed)
                setOnClickListener {
                    mPresenter.onAddEventToFavoriteClick()
                }
            }
        }

    }

    override fun showEventAddedToFavoriteMessage() {
        EventAddedToFavoriteDialog(requireContext())
    }

    private fun addToCalendar(eventData: EventNew) {
        try {
            val startCal = defaultServerDateFormatter.parse(eventData.holdingDate?.from ?: "").calendar()
            val endCal = defaultServerDateFormatter.parse(eventData.holdingDate?.to ?: "").calendar()

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
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    override fun updateAppBarBackgroundColorValue(value: Int) {
        Log.e("OFFSET", value.toString())

        mBinding.apply {
            if (value <= 0) {
                tbBackground.alpha = 0f
            }else {
                tbBackground.apply {
                    alpha = if (actionItem != null){
                        if (actionItem!!.viewHeight() > 1400){
                            abs(value / (actionItem!!.viewHeight()).toFloat())
                        }else {
                            abs(value / (1400).toFloat())
                        }
                    }else {
                        abs(value / (1400).toFloat())
                    }
                }
            }
            if (value >= 1450){
                appBar.changeAppBarElevation(abs(value / 100f))
            }else {
                appBar.changeAppBarElevation(0f)
            }

            if (value >= 740){
                setBlackIcons()
            }else {
                setWhiteIcons()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ABOUT_FROM_EVENT = 1
        const val ABOUT_FROM_OTHER = 2
    }

}

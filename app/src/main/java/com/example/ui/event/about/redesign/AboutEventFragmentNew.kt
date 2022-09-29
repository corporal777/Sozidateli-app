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
import com.example.ui.base.BaseFragment
import com.example.ui.event.about.redesign.items.*
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.event.speakers.list.EventSpeakersFragmentArgs
import com.example.ui.event.speakers.member.UserSpeakerFragmentArgs
import com.example.ui.organizations.OrganizationFragmentArgs
import com.example.ui.page.PageFragmentArgs
import com.example.ui.partner.PartnerFragmentArgs
import com.example.ui.subevent.SubEventFragmentArgs
import com.example.ui.views.StateType
import com.example.ui.views.dialogs_new.EventAddedToFavoriteDialog
import com.example.ui.views.dialogs_new.MessageDialogWithBrownButton
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
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

    private val subEventsBlock = Section()
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

    private val groupAdapter = GroupAdapter<GroupieViewHolder>().apply {
    }


    private val onActionClickListener = object : EventDetailActionBlock.OnActionClickListener {
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

    private var actionItem: EventDetailActionBlock? = null
    private var organizationItem: EventDetailOrganizationBlock? = null

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

    override fun setSubEvents(
        isApproved: Boolean,
        subEvents: Map<String, List<EventActivityModel>>
    ) {
        if (!subEvents.isNullOrEmpty()) {
            subEventsBlock.update(
                subEvents.map {
                    EventDetailActivitiesBlock(
                        mEventId,
                        isApproved,
                        it.key,
                        it.value,
                        onSubEventClickListener
                    )
                }
            )
        }
    }

    override fun setEventData(
        eventData: EventNew?,
        pages: List<PageModel>?,
        members: List<MemberModel>?,
        partners: List<PartnerModel>?,
        tags: List<Tag>
    ) {
        if (eventData != null) {
            mBinding.btnAddToCalendar.setOnClickListener {
                addToCalendar(eventData)
            }
            if (eventData.binds?.userFavorite != null)
                decorEventFavoriteButton(true)
            else decorEventFavoriteButton(false)
        }
        groupAdapter.clear()

        val actionItem = EventDetailActionBlock(eventData, onActionClickListener).apply {
            this@AboutEventFragmentNew.actionItem = this
        }
        val organizationItem = EventDetailOrganizationBlock(eventData,
            { mPresenter.onAddOrganizationToFavoriteClick() },
            { mPresenter.onOrganizationClick(it) }).apply {
            this@AboutEventFragmentNew.organizationItem = this
        }

        groupAdapter.update(listOf(
            EventDetailImageBlock(eventData),
            actionItem,
            Section().apply {
                add(organizationItem)
                if (!eventData?.address?.fullValue.isNullOrEmpty() || !pages.isNullOrEmpty()) {
                    add(
                        EventDetailInformationBlock(
                            getString(R.string.information),
                            eventData,
                            pages,
                            { mPresenter.onMapPageSelected() },
                            { mPresenter.onPageClick(it) })
                    )
                }
            },
            Section().apply {
                if (!members.isNullOrEmpty()) {
                    add(
                        EventDetailSpeakersBlock(
                            getString(R.string.speakers),
                            members,
                            { mPresenter.onSpeakerClick(it) },
                            { mPresenter.onShowAllSpeakersClick() })
                    )
                }
                add(EventDetailTagsBlock(getString(R.string.event_program), tags) {
                    mPresenter.onTagSelected()
                })
            },
            subEventsBlock,
            Section().apply {
                add(EventDetailShowActivitiesButtonBlock {
                    mPresenter.onShowEventActivitiesClick()
                })
                if (!partners.isNullOrEmpty()) {
                    add(
                        EventDetailPartnersBlock(
                            getString(R.string.partners_label),
                            partners
                        ) { mPresenter.onPartnerClick(it) })
                }
            }
        ))

        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun showMap(mapInfo: MapInfo?) {
        findNavController().navigate(
            AboutEventFragmentNewDirections.actionAboutEventFragmentNewToMapFragmentNew(mapInfo)
        )
    }

    override fun updateSubEvent(subEvent: EventActivityModel) {
        val idLong = subEvent.id?.toLong()
        subEventsBlock.findItemBy<EventActivityItem> { it.id == idLong }?.notifyChanged(subEvent)
    }

    override fun changeEventSubscription(isSubscribed: Boolean) {
        decorEventFavoriteButton(isSubscribed)
    }

    override fun changeOrganizationSubscription(isSubscribed: Boolean) {
        organizationItem?.notifyChanged(isSubscribed)
    }

    override fun setActionButton(event: EventNew/*EventData*/?, userRegistration: Event.Status?) {
        actionItem?.notifyChanged(event)
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
            UserSpeakerFragmentArgs.Builder(speakerId.toString(), mEventId).build().toBundle()
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
            R.id.organization_fragment,
            OrganizationFragmentArgs.Builder(organization).build().toBundle()
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
            ivShare.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
            ivBack.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
        }

    }


    private fun decorEventFavoriteButton(isSubscribed: Boolean) {
        mBinding.apply {
            ivAddToFavorite.apply {
                if (isSubscribed) {
                    setImageResource(R.drawable.ic_star_filled)
                } else {
                    setImageResource(R.drawable.ic_star)
                }
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
        val mStartDate = defaultServerDateFormatter.parse(eventData.holdingDate?.from ?: "")
        val mEndDate = defaultServerDateFormatter.parse(eventData.holdingDate?.to ?: "")
        val mStartCal = mStartDate.calendar()
        val mEndCal = mEndDate.calendar()

        val intent: Intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, mStartCal.timeInMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, mEndCal.timeInMillis)
            putExtra(CalendarContract.Events.TITLE, eventData.name)
            putExtra(CalendarContract.Events.DESCRIPTION, eventData.description)
            putExtra(CalendarContract.Events.EVENT_LOCATION, eventData.address?.city)
            putExtra(
                CalendarContract.Events.AVAILABILITY,
                CalendarContract.Events.AVAILABILITY_BUSY
            )
        }
        startActivity(intent)

    }

    override fun updateAppBarBackgroundColorValue(value: Int) {
        Log.e("OFFSET", value.toString())
        mBinding.apply {
            if (value == 0) {
                tbBackground.setBackgroundColor(Color.TRANSPARENT)
                setWhiteIcons()
            }
            if (value > 0 && value < 1750) {
                tbBackground.apply {
                    tbContent.setBackgroundColor(Color.TRANSPARENT)
                    setBackgroundColor(Color.BLACK)
                    val mAlpha = abs(value / (600).toFloat())
                    alpha = mAlpha
                    aboutEventAppBar.apply {
                        elevation = 0f
                        background = null
                        aboutEventToolbar.background = null
                    }
                    //setWhiteIcons()
                }
            }
            if (value > 1750) {
                tbBackground.apply {
                    tbContent.setBackgroundColor(Color.BLACK)
                    setBackgroundColor(Color.WHITE)
                    val value = value - 1750
                    val mAlpha = abs(value / (500).toFloat())
                    alpha = mAlpha
                }
                aboutEventAppBar.apply {
                    elevation = 10f
                    setBackgroundColor(Color.WHITE)
                    aboutEventToolbar.setBackgroundColor(Color.WHITE)
                }
                setBlackIcons()
            }
            if (value < 1750) {
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

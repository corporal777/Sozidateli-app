package com.example.ui.event.about.redesign

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.BuildConfig
import com.example.R
import com.example.data.models.*
import com.example.databinding.FragmentAboutEventNewBinding
import com.example.extensions.*
import com.example.holders.redesign.EventActivityItem
import com.example.interfaces.BackgroundImageFragment
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
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import setOnClickListener
import java.lang.StringBuilder
import javax.inject.Inject
import javax.inject.Provider


class AboutEventFragmentNew() : BaseFragment(), AboutEventContractNew.View,
    BackgroundImageFragment {

    private var mLightStatus = false
    private var _binding: FragmentAboutEventNewBinding? = null
    private val mBinding get() = _binding!!

    private var mDy: Int = 0
    private var mEventId = ""

    override fun layout() = R.layout.fragment_about_event_new

    private val subEventsBlock = Section()

    @InjectPresenter
    lateinit var presenterNew: AboutEventPresenterNew

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
        override fun onActionRegister() = presenterNew.onGoToEventClick()
        override fun onActionCancel() = presenterNew.onActionCancel()
        override fun onShowUpdateState() = showStateErrorMessage(StateType.BASE, false, null)
        override fun onSubscribeEvent() = presenterNew.onCreateEventSubscriptionClick()
        override fun onDeleteSubscribeEvent() = presenterNew.onDeleteEventSubscriptionClick()

    }

    private val onSubEventClickListener = object : EventActivityItem.OnEventActivityClickListener {
        override fun onSubEventClick(eventId: String, subEvent: EventActivityModel) {
            presenterNew.onSubEventClick(subEvent)
        }

        override fun onAddToScheduleClick(subEvent: EventActivityModel) =
            presenterNew.onAddToScheduleClick(subEvent)

        override fun onRemoveFromScheduleClick(subEvent: EventActivityModel) =
            presenterNew.onRemoveFromScheduleClick(subEvent)

        override fun onUpdateScheduleState(subEvent: EventActivityModel) {}
    }

    private var actionItem: EventDetailActionBlock? = null
    private var organizationItem: EventDetailOrganizationBlock? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAboutEventNewBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.eventContentList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = groupAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    mDy += dy
                    updateView(mDy)
                }
            })
        }


        mBinding.ivShare.setOnClickListener(presenterNew::onShareClick)
        mBinding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        mBinding.swipeToRefresh.setOnRefreshListener {
            presenterNew.onRefreshRequest()
        }
    }


    override fun setSubEvents(
        isApproved: Boolean,
        subEvents: MutableMap<String, ArrayList<EventActivityModel>>
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
            { presenterNew.onAddOrganizationToFavoriteClick() },
            { presenterNew.onOrganizationClick(it) }).apply {
            this@AboutEventFragmentNew.organizationItem = this
        }


        groupAdapter.update(listOf(
            EventDetailImageBlock(eventData),
            actionItem,
            Section().apply {
                add(organizationItem)
                add(
                    EventDetailInformationBlock(
                        getString(R.string.information),
                        eventData,
                        pages,
                        { presenterNew.onMapPageSelected() },
                        { presenterNew.onPageClick(it) })
                )
            },
            Section().apply {
                if (!members.isNullOrEmpty()) {
                    add(
                        EventDetailSpeakersBlock(
                            getString(R.string.speakers),
                            members,
                            { presenterNew.onSpeakerClick(it) },
                            { presenterNew.onShowAllSpeakersClick() })
                    )
                }
                add(EventDetailTagsBlock(getString(R.string.event_program), tags) {
                    presenterNew.onTagSelected()
                })
            },
            subEventsBlock,
            Section().apply {
                add(EventDetailShowActivitiesButtonBlock {
                    presenterNew.onShowEventActivitiesClick()
                })
                if (!partners.isNullOrEmpty()) {
                    add(
                        EventDetailPartnersBlock(
                            getString(R.string.partners_label),
                            partners
                        ) { presenterNew.onPartnerClick(it) })
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


    override fun showOrganization(organization: String) {
        findNavController().navigate(
            R.id.organization_fragment,
            OrganizationFragmentArgs.Builder(organization).build().toBundle()
        )
    }

    override fun onStart() {
        super.onStart()
        if (mBinding.eventContentList != null) {
            //mDy += eventContentList.scrollY
        }
    }


    companion object {
        const val ABOUT_FROM_EVENT = 1
        const val ABOUT_FROM_OTHER = 2
    }

    override val isLightStatus: Boolean
        get() = mLightStatus

    override fun getFragmentBackgroundDrawable(): Drawable? {
        return null
    }

    private fun setBlackIcons() {
        mBinding.apply {
            ivAddToFavorite.imageTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.vk_black)
            ivShare.imageTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.vk_black)
            ivBack.imageTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.vk_black)
            btnAddToCalendar.setTextColor(Color.BLACK)
            btnAddToCalendar.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.custom_btn_add_to_calendar_background_black
            )
            requireActivity().window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

    }

    private fun setWhiteIcons() {
        mBinding.apply {
            requireActivity().window.decorView.systemUiVisibility = 0
            btnAddToCalendar.setTextColor(Color.WHITE)
            btnAddToCalendar.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.custom_btn_add_to_calendar_background
            )
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
                    presenterNew.onAddEventToFavoriteClick()
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

    private fun updateView(offset: Int) {
        Log.e("OFFSET", offset.toString())
        mBinding.apply {
            if (offset == 0) {
                tbBackground.setBackgroundColor(Color.TRANSPARENT)
            }
            if (offset > 0 && offset < 1700) {
                tbBackground.apply {
                    tbContent.setBackgroundColor(Color.TRANSPARENT)
                    setBackgroundColor(Color.BLACK)
                    val mAlpha = Math.abs(offset / (1000).toFloat())
                    Log.e("WHITE", mAlpha.toString())
                    alpha = mAlpha
                    aboutEventAppBar.apply {
                        elevation = 0f
                        background = null
                        aboutEventToolbar.background = null
                    }
                    //setWhiteIcons()
                }
            }
            if (offset > 1900) {
                tbBackground.apply {
                    tbContent.setBackgroundColor(Color.BLACK)
                    setBackgroundColor(Color.WHITE)
                    val value = offset - 1900
                    val mAlpha = Math.abs(value / (1000).toFloat())
                    Log.e("BLACk", mAlpha.toString())
                    alpha = mAlpha
                }
                aboutEventAppBar.apply {
                    elevation = 10f
                    setBackgroundColor(Color.WHITE)
                    aboutEventToolbar.setBackgroundColor(Color.WHITE)
                }
                setBlackIcons()
            }
            if (offset < 2060) {
                setWhiteIcons()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

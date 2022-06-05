package com.example.ui.event.about.redesign

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.BuildConfig
import com.example.R
import com.example.data.models.*
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.findGroupBy
import com.example.extensions.findItemBy
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventActivityItem
import com.example.holders.redesign.EventPageItemNew
import com.example.holders.redesign.SpeakersHorizontalListItem
import com.example.holders.redesign.blocks.*
import com.example.interfaces.BackgroundImageFragment
import com.example.ui.base.BaseFragment
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.event.speakers.EventSpeakersFragmentArgs
import com.example.ui.event.speakers.UserSpeakerFragmentArgs
import com.example.ui.organizations.OrganizationFragmentArgs
import com.example.ui.page.PageFragmentArgs
import com.example.ui.partner.PartnerFragmentArgs
import com.example.ui.subevent.SubeventFragmentArgs
import com.example.ui.views.StateType
import com.example.ui.views.dialogs_new.MessageDialogWithGreenButton
import com.example.ui.views.toolbar.widget.OnTransparentListener
import com.example.util.adjustAlpha
import com.google.android.material.snackbar.Snackbar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import kotlinx.android.synthetic.main.fragment_about_event_new.*
import setOnClickListener
import java.lang.StringBuilder
import javax.inject.Inject
import javax.inject.Provider


class AboutEventFragmentNew() : BaseFragment(), AboutEventContractNew.View,
    BackgroundImageFragment {

    private var screenType = ABOUT_FROM_OTHER
    private var mLightStatus = false

    private var mDy: Int = 0
    private var mMaxOffset = 0f
    private var mEventId = ""

    override fun layout() = R.layout.fragment_about_event_new
    private lateinit var mEventData: EventNew

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
        override fun onActivityClick(subEvent: EventActivityModel) =
            presenterNew.onSubEventClick(subEvent)

        override fun onAddToScheduleClick(subEvent: EventActivityModel) =
            presenterNew.onAddToScheduleClick(subEvent)

        override fun onRemoveFromScheduleClick(subEvent: EventActivityModel) =
            presenterNew.onRemoveFromScheduleClick(subEvent)

        override fun onUpdateScheduleState(subEvent: EventActivityModel) {}
    }

    private var actionItem: EventDetailActionBlock? = null
    private var organizationItem: EventDetailOrganizationBlock? = null
    private var subEventsItem: EventDetailActivitiesBlock? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventContentList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = groupAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    mDy += dy
                    updateView(mDy)
                }
            })
        }


        iv_share.setOnClickListener(presenterNew::onShareClick)
        iv_back.setOnClickListener {
            findNavController().navigateUp()
        }
        btnAddToCalendar.setOnClickListener {
            addToCalendar(mEventData)
        }
        swipeToRefresh.setOnRefreshListener {
            presenterNew.onRefreshRequest()
        }
    }


    override fun setSubEvents(
        subEvents: MutableMap<String, ArrayList<EventActivityModel>>
    ) {
        if (!subEvents.isNullOrEmpty()){
            subEventsBlock.update(listOf(EventDetailActivitiesBlock(subEvents, onSubEventClickListener)))
        }
    }

    override fun showErrorMessage(message: String) {
        MessageDialogWithGreenButton(requireContext(), message).setSelectCallback {
            findNavController().navigateUp()
        }
    }


    override fun setEventData(
        eventData: EventNew?,
        userRegistration: Event.Status?,
        pages: List<PageModel>?,
        partners: List<PartnerModel>?,
        tags: List<Tag>,
        userAgreement: String?
    ) {
        groupAdapter.clear()

        if (eventData != null) {
            mEventData = eventData
        }

        val actionItem = EventDetailActionBlock(eventData, onActionClickListener).apply {
            this@AboutEventFragmentNew.actionItem = this
        }
        val organizationItem = EventDetailOrganizationBlock(eventData,
            { presenterNew.onChangeFavoriteClick() },
            { presenterNew.onOrganizationClick(it) }).apply {
            this@AboutEventFragmentNew.organizationItem = this
        }


        groupAdapter.update(listOf(
            Section().apply {
                add(EventDetailImageBlock(eventData))
            },
            Section().apply {
                add(actionItem)
            },
            Section().apply {
                add(organizationItem)
                add(EventDetailBlocksLabelItem(getString(R.string.information)))
                add(
                    EventPageItemNew(
                        1,
                        getString(R.string.how_to_go)
                    ) { presenterNew.onMapPageSelected() })
                if (!pages.isNullOrEmpty()) {
                    addAll(pages.map { item ->
                        EventPageItemNew(
                            item.id ?: 0,
                            item.name ?: ""
                        ) { presenterNew.onPageClick(it) }
                    })
                }
                if (!eventData?.binds?.member.isNullOrEmpty()) {
                    add(EventDetailBlocksLabelItem(getString(R.string.speakers)))
                    val speakers = eventData?.binds?.member?.filter { it.role == "speaker" }
                    add(
                        SpeakersHorizontalListItem(speakers!!,
                            { presenterNew.onSpeakerClick(it) },
                            { presenterNew.onShowAllSpeakersClick() })
                    )
                }
                add(EventDetailBlocksLabelItem(getString(R.string.event_program)))
                add(EventDetailTagsBlock(tags) {
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

        swipeToRefresh.isRefreshing = false
    }

    override fun showMap(mapInfo: MapInfo?) {
        findNavController().navigate(
            AboutEventFragmentNewDirections.actionAboutEventFragmentNewToMapFragmentNew(mapInfo)
        )
    }

    override fun updateSubEvent(subEvent: EventActivityModel) {
        //subEventsBlock.findGroupBy<EventDetailActivitiesBlock> { true }?.updateButtonState(subEvent)
        val idLong = subEvent.id?.toLong()
        subEventsBlock.findItemBy<EventActivityItem> { it.id == idLong }?.notifyChanged(subEvent)
    }

    override fun setActionButton(event: EventNew/*EventData*/?, userRegistration: Event.Status?) {
        actionItem?.apply {
            notifyChanged(event)
        }
    }

    override fun showSubEvent(eventId: String, subEventId: String) {
        val args = SubeventFragmentArgs.Builder(eventId, subEventId).build().toBundle()
        findNavController().navigate(R.id.subevent_fragment, args)
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

    override fun changeEventSubscription(isSubscribed: Boolean) {
        organizationItem?.notifyChanged(isSubscribed)
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
        if (eventContentList != null) {
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
        iv_share.setImageResource(R.drawable.ic_share_black)
        iv_back.setImageResource(R.drawable.ic_back_black)
        btnAddToCalendar.setTextColor(Color.BLACK)
        btnAddToCalendar.background = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.custom_btn_add_to_calendar_background_black
        )
        requireActivity().window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    private fun setWhiteIcons() {
        requireActivity().window.decorView.systemUiVisibility = 0
        btnAddToCalendar.setTextColor(Color.WHITE)
        btnAddToCalendar.background = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.custom_btn_add_to_calendar_background
        )

        iv_share.setImageResource(R.drawable.ic_share_white)
        iv_back.setImageResource(R.drawable.ic_back_white)
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

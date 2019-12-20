package com.example.ui.event.about

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.*
import com.example.extensions.*
import com.example.holders.*
import com.example.holders.EventGroup
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.event.contacts.EventContactsFragmentArgs
import com.example.ui.event.rating.EventRatingFragmentArgs
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.event.speakers.EventSpeakersFragmentArgs
import com.example.ui.image.ImageViewActivityArgs
import com.example.ui.organizations.OrganizationFragmentArgs
import com.example.ui.page.PageFragmentArgs
import com.example.ui.partner.PartnerFragmentArgs
import com.example.ui.views.toolbar.ToolbarContentActionBar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_about_event.*
import kotlinx.android.synthetic.main.fragment_search.recyclerView
import kotlinx.android.synthetic.main.item_action_button.view.*
import setOnClickListener
import javax.inject.Inject
import javax.inject.Provider

class AboutEventFragment : BaseFragment(), AboutEventContract.View, ToolbarFragment {

    override val title: String? = null

    @InjectPresenter
    lateinit var presenter: AboutEventPresenter

    @Inject
    lateinit var presenterProvider: Provider<AboutEventPresenter>

    @ProvidePresenter
    fun providePresenter(): AboutEventPresenter = presenterProvider.get().apply {
        eventId = AboutEventFragmentArgs.fromBundle(arguments!!).eventId
    }

    private var toolbarContentActionBar: ToolbarContentActionBar? = null

    private val groupAdapter = GroupAdapter<GroupieViewHolder>().apply {
        spanCount = 12
    }

    private val eventClickListener = object : EventStatusItem.OnEventClickListener {
        override fun onActionRegister(event: String) {
            // do nothing
        }

        override fun onActionShowEvent(event: String) {
            // do nothing
        }

        override fun onActionCancel(event: String) {
            // do nothing
        }

        override fun onActionWriteToOrganization(emails: List<EmailAffiliation>) {
            // do nothing
        }

        override fun onShowEventClick(event: String) {
            presenter.onLogoClick()
        }

        override fun onShowFilterClick(format: Int) {

        }
    }

    private var aboutItem: EventDataAboutItem? = null
    private var writeMessageDialog: AlertDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = groupAdapter

            layoutManager = GridLayoutManager(context, groupAdapter.spanCount).apply {
                spanSizeLookup = groupAdapter.spanSizeLookup
            }

            if (itemDecorationCount == 0) {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    private val padding = 16.dp

                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        if (parent.getChildViewHolder(view).itemViewType == R.layout.item_event_partner) {
                            val layoutParams = view.layoutParams as GridLayoutManager.LayoutParams
                            val gridLayoutManager = parent.layoutManager as GridLayoutManager
                            val spanSize = layoutParams.spanSize.toFloat()
                            val totalSpanSize = gridLayoutManager.spanCount.toFloat()

                            val n = totalSpanSize / spanSize // num columns
                            val c = layoutParams.spanIndex / spanSize // column index

                            val leftPadding = padding * ((n - c) / n)
                            val rightPadding = padding * ((c + 1) / n)

                            outRect.top = padding
                            outRect.left = leftPadding.toInt()
                            outRect.right = rightPadding.toInt()
                        }
                    }
                })
            }
        }

        swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
    }

    override fun setEventData(eventData: EventData, userRegistration: Event.RegistrationStatus?, userRating: EventRatingData?, pages: List<EventPage>, partners: List<EventParther>, showContacts: Boolean) {
        val aboutItem = EventDataAboutItem(
                -eventData.id.toLong(),
                eventData.organization?.name,
                eventData.name,
                eventData.conferenceFirstActivityStart?.parseAndFormat(defaultServerDateTimeFormatter, defaultTimeFormatter),
                eventData.conferenceStart.formatToEventDatesInterval(eventData.conferenceFinish),
                eventData.isFavorite ?: false,
                { presenter.onChangeFavoriteClick() },
                { eventData.organizationId?.let { presenter.onOrganizationClick(it) } }
        ).apply {
            this@AboutEventFragment.aboutItem = this
        }

        groupAdapter.update(listOf(
                EventGroup(
                        eventData.id,
                        if (eventData.status == Event.Status.CONFERENCE_ENDS) Event.Status.CONFERENCE_ENDS else null,
                        userRegistration,
                        eventData.backgroundColor,
                        eventData.backgroundImage,
                        eventData.format,
                        null,
                        eventClickListener,
                        aboutItem
                ),
                Section().apply {
                    if ((eventData.status == Event.Status.CONFERENCE_ENDS || eventData.status == Event.Status.IN_ARCHIVE) && eventData.ratingStartAt != null && userRating?.created == null && eventData.userRegistration == Event.RegistrationStatus.APPROVED) {
                        add(EventPageItem(-100, getString(R.string.about_event_rate)) { presenter.onRateClick() })
                    }
                    if (showContacts) add(EventPageItem(-90, getString(R.string.about_event_contacts)) { presenter.onContactsClick() })
                    add(EventPageItem(-80, getString(R.string.about_event_speakers)) { presenter.onSpeakersClick() })
                    add(EventPageItem(-70, getString(R.string.about_event_write_to_organization)) { presenter.onWriteToOrganizationClick() }.apply {
                        hasBottomPadding = pages.isNotEmpty()
                    })

                    addAll(pages.mapIndexed { index, item ->
                        EventPageItem(item.id, item.menu) { presenter.onPageClick(item) }.apply {
                            hasBottomPadding = index == pages.size - 1
                        }
                    })
                },

                Section().apply {
                    setHeader(PartnersTitleItem(-50))
                    setHideWhenEmpty(true)
                    addAll(partners.map { EventPartnerItem(it.id, it.logo, it.name) { presenter.onPartnerClick(it) } })
                }
        ))
        swipeToRefresh.isRefreshing = false
    }

    override fun setActionButton(eventData: EventData, userRegistration: Event.RegistrationStatus?) {
        flRegister.apply {
            val textRes: Int
            val clickAction: () -> Unit
            if (eventData.status == null || eventData.status == Event.Status.CONFERENCE_ENDS) {
                isVisible = false
                return@apply
            } else when (userRegistration) {
                Event.RegistrationStatus.PENDING -> {
                    textRes = R.string.event_action_cancel_request
                    clickAction = { presenter.onActionCancel() }
                }
                Event.RegistrationStatus.DECLINED -> {
                    if (eventData.email.isNullOrEmpty()) {
                        isVisible = false
                        return@apply
                    }

                    textRes = R.string.event_action_write_to_organisation
                    clickAction = { presenter.onActionWriteToOrganization() }
                }
                Event.RegistrationStatus.APPROVED -> {
                    textRes = R.string.event_action_show_event
                    clickAction = { presenter.onSelectEventClick() }
                }
                else -> {
                    if (Event.isCanRegister(eventData.status, userRegistration)) {
                        textRes = R.string.event_action_participate
                        clickAction = { presenter.onGoToEventClick() }
                    } else {
                        isVisible = false
                        return@apply
                    }
                }
            }

            isVisible = true
            btnAction.apply {
                text = getString(textRes)
                setOnClickListener(clickAction)
            }
        }

        recyclerView.updatePadding(bottom = if (flRegister.isVisible) resources.getDimensionPixelSize(R.dimen.about_event_bottom_gradient_height) else 20.dp)
    }

    override fun setEventName(name: String) {
        toolbarContentActionBar?.title = name
    }

    override fun changeEventSubscription(isSubscribed: Boolean) {
        aboutItem?.notifyChanged(isSubscribed)
    }

    @SuppressLint("InflateParams")
    override fun showWriteToOrganizationForm() {
        val view = layoutInflater.inflate(R.layout.dialog_send_message_to_organization, null)
        writeMessageDialog = AlertDialog.Builder(requireContext())
                .setTitle(R.string.about_event_write_to_organization_title)
                .setMessage(R.string.about_event_write_to_organization_message)
                .setView(view)
                .setPositiveButton(R.string.send, null)
                .setNegativeButton(R.string.cancel, null)
                .setOnDismissListener { writeMessageDialog = null }
                .show()
                .apply {
                    getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                        val message = view.findViewById<EditText>(R.id.etMessage).text?.toString()
                        if (!message.isNullOrEmpty()) presenter.onWriteToOrganizationMessage(message)
                        else dismiss()
                    }
                }
    }

    override fun showWriteToOrganizationEmails(emails: List<EmailAffiliation>) {
        AlertDialog.Builder(requireContext())
                .setItems(emails.map { it.getAffiliationString() }.toTypedArray()) { dialog, which ->
                    val email = emails[which]
                    presenter.onWriteToOrganizationEmailChosen(email)
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    override fun showWriteToOrganization(email: EmailAffiliation) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email.email))
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        }
    }

    override fun hideWriteToOrganizationForm() {
        writeMessageDialog?.dismiss()
        writeMessageDialog = null
    }

    override fun showPage(eventId: String, pageId: String) {
        findNavController().navigate(R.id.page_fragment, PageFragmentArgs.Builder(eventId, pageId).build().toBundle())
    }

    override fun showPartner(eventId: String, partnerId: String) {
        findNavController().navigate(R.id.partner_fragment, PartnerFragmentArgs.Builder(eventId, partnerId).build().toBundle())
    }

    override fun showSpeakers(eventId: String) {
        findNavController().navigate(R.id.speakers_list_fragment, EventSpeakersFragmentArgs.Builder(eventId).build().toBundle())
    }

    override fun showContacts(eventName: String, phones: List<PhoneAffiliation>, emails: List<EmailAffiliation>, webLinks: List<String>, socialLinks: List<String>, address: String?, place: String?, mapInfo: MapInfo?, places: List<Place>?) {
        findNavController().navigate(R.id.contacts_fragment, EventContactsFragmentArgs.Builder(
                eventName,
                phones.toTypedArray(),
                emails.toTypedArray(),
                webLinks.toTypedArray(),
                socialLinks.toTypedArray(),
                address,
                place,
                mapInfo,
                places?.toTypedArray()
        ).build().toBundle())
    }

    override fun showEventRequest(event: String) {
        findNavController().navigate(R.id.request_fragment, EventRegistrationFragmentArgs.Builder(event).build().toBundle())
    }

    override fun showLogoImage(url: String) {
        findNavController().navigate(
                R.id.image_view_activity,
                ImageViewActivityArgs.Builder(url, null, null, null).build().toBundle()
        )
    }

    override fun showRating(eventId: String) {
        findNavController().navigate(R.id.event_rating_fragment, EventRatingFragmentArgs.Builder(eventId).build().toBundle())
    }

    override fun showOrganization(organization: String) {
        findNavController().navigate(R.id.organization_fragment, OrganizationFragmentArgs.Builder(organization).build().toBundle())
    }

    override fun selectEvent() {
        findNavController().navigate(R.id.event_tabs_fragment, null, NavOptions.Builder()
                .setPopUpTo(R.id.main_navigation, true)
                .build())
    }

    override fun setupToolbarContent(toolbarContentActionBar: ToolbarContentActionBar) {
        super.setupToolbarContent(toolbarContentActionBar)
        this.toolbarContentActionBar = toolbarContentActionBar
    }

    override fun layout() = R.layout.fragment_about_event
}

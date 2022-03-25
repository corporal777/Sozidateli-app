package com.example.ui.event.about

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
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
import com.example.ui.user.UserFragmentArgs
import com.example.ui.views.EventRegistrationProfileFieldsDialog
import com.example.ui.views.StateType
import com.example.ui.views.toolbar.ToolbarContentActionBar
import com.example.util.ClickableSpan
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.dialog_event_registration_agreement_form.*
import kotlinx.android.synthetic.main.dialog_event_registration_agreement_no_form.view.*
import kotlinx.android.synthetic.main.fragment_about_event.*
import kotlinx.android.synthetic.main.fragment_search.recyclerView
import kotlinx.android.synthetic.main.item_action_button.view.*
import setOnClickListener
import javax.inject.Inject
import javax.inject.Provider

class AboutEventFragment : BaseFragment(), AboutEventContract.View, ToolbarFragment {

    private var screenType = ABOUT_FROM_OTHER

    override val title: String? = null

    override fun layout() = R.layout.fragment_about_event

    @InjectPresenter
    lateinit var presenter: AboutEventPresenter

    @Inject
    lateinit var presenterProvider: Provider<AboutEventPresenter>

    @ProvidePresenter
    fun providePresenter(): AboutEventPresenter = presenterProvider.get().apply {
        eventId = AboutEventFragmentArgs.fromBundle(requireArguments()).eventId
        screenType = AboutEventFragmentArgs.fromBundle(requireArguments()).screenType
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

        override fun onActionCancel(event: String, registrationId: String?) {
            // do nothing
        }

        override fun onActionWriteToOrganization(emails: List<EventPhoneModel/*EmailAffiliation*/>) {
            // do nothing
        }

        override fun onShowEventClick(view: View, event: String) {
            presenter.onLogoClick()
        }

        override fun onShowFilterClick(format: Int) {

        }

        override fun onShowUpdateState() = showStateErrorMessage(StateType.BASE, false, null)
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

                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
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

    override fun setEventData(
        eventData: EventNew?,
        userRegistration: Event.Status?,
        pages: List<PageModel>?,
        partners: List<PartnerModel>?,
        showContacts: Boolean,
        userAgreement: String?
        /*eventData: EventData?,
        userRegistration: Event.RegistrationStatus?,
        pages: List<EventPage>,
        partners: List<EventParther>,
        showContacts: Boolean,
        userAgreement: String?*/
    ) {

        val aboutItem = EventDataAboutItem(
            -(eventData?.id?.toLong() ?: 0),
            eventData?.binds?.organization?.legalInformation?.name?.short
                ?: eventData?.binds?.organization?.legalInformation?.name?.full,
            eventData?.name,
            null,
            eventData?.holdingDate?.from.formatToEventDatesIntervalNew(eventData?.holdingDate?.to),
            eventData?.requestsApply?.dateLimit
                ?.parseAndFormat(defaultServerDateFormatter, dateFormatterShortDayFullMothFullYear),
            eventData?.binds?.userFavorite != null,
            { presenter.onChangeFavoriteClick() },
            { eventData?.organization?.let { presenter.onOrganizationClick(it.toString()) } }
        ).apply {
            this@AboutEventFragment.aboutItem = this
        }
        /*val aboutItem = EventDataAboutItem(
                -(eventData?.id?.toLong()?:0),
                eventData?.organization?.name,
                eventData?.name,
                null,
                eventData?.conferenceStart.formatToEventDatesIntervalNew(eventData?.conferenceFinish),
                eventData?.conferenceRegistrationFinishDate
                        ?.parseAndFormat(defaultServerDateFormatter, dateFormatterShortDayFullMothFullYear),
                eventData?.isFavorite ?: false,
                { presenter.onChangeFavoriteClick() },
                { eventData?.organizationId?.let { presenter.onOrganizationClick(it) } }
        ).apply {
            this@AboutEventFragment.aboutItem = this
        }*/

        groupAdapter.update(listOf(
            EventGroup(
                /*eventData?.id?: "0",
                if (eventData?.status == Event.Status.CONFERENCE_ENDS) Event.Status.CONFERENCE_ENDS else null,
                userRegistration,
                eventData?.backgroundColor,
                eventData?.backgroundImage,
                eventData?.takeFormat(),
                null,
                !(eventData?.canRegister?: false),
                eventClickListener,
                aboutItem,
                eventData?.userAgreement,
                false*/
                eventData?.id.toString(),
                if (eventData?.status?.value == Event.Status.FINISHED) Event.Status.FINISHED else null,
                userRegistration,
                eventData?.binds?.organization?.backgroundColor?.value,
                eventData?.image?.uri,
                EventFormat(
                    name = if (eventData?.format?.name.isNullOrEmpty()) eventData?.format?.custom
                        ?: "" else eventData?.format?.name ?: ""
                ),
                null,
                /*!eventData?.binds?.rights?.registration!!*/
                (eventData?.status?.value ?: "") != Event.Status.REGISTRATION,
                eventClickListener,
                aboutItem,
                eventData?.userAgreement?.name ?: eventData?.userAgreement?.uri,
                null,
                false,
                eventData?.binds?.currentUserRegistration?.id.toString()
            ),
            Section().apply {
                //TODO add checking field hide for activities
                val isNotHide = eventData?.binds?.activity?.firstOrNull { !it.hide }
                if (!eventData?.binds?.activity.isNullOrEmpty() && isNotHide != null) add(
                    EventPageItem(-90, getString(R.string.about_event_activities)) {

                        findNavController().navigate(
                            AboutEventFragmentDirections.actionAboutEventFragmentToActivitiesFragment(
                                eventData.id ?: 0
                            )
                        )

                    })
                if (showContacts) add(
                    EventPageItem(
                        -90,
                        getString(R.string.about_event_contacts)
                    ) { presenter.onContactsClick() })
                if (!eventData?.binds?.member?.filter { it.role == "speaker" }.isNullOrEmpty()) {
                    add(
                        EventPageItem(
                            -80,
                            getString(R.string.about_event_speakers)
                        ) { presenter.onSpeakersClick() })
                }

                val rating = eventData?.binds?.userFormResult?.sumBy { it.result?.ratingMark ?: 0 }
                val hasRating = eventData?.status?.value == Event.Status.FINISHED &&
                        (eventData?.state?.rating?.formEnabled == true) &&
                        eventData?.binds?.currentUserRegistration?.status?.value == Event.Status.APPROVED &&
                        (rating ?: 0) <= 0
                /*val hasRating = (eventData?.status?.value == Event.Status.FINISHED ||
                        eventData?.status?.value == Event.Status.IN_ARCHIVE) &&
                        eventData.binds?.form?.firstOrNull { it.type == EventFormModel.Type.RATING } != null &&
                        userRegistration == Event.Status.APPROVED*/

                val hasPages = pages?.isNotEmpty()
                val hasAgreement = userAgreement.isNullOrEmpty().not()

                //if (BuildConfig.NEW_PROFILE_EDIT) {

//                add(EventPageItem(-70, getString(R.string.about_event_write_to_organization)) {
//                    presenter.onWriteToOrganizationClick()
//                }.apply {
//                    hasBottomPadding = !hasRating && !hasAgreement && hasPages == true
//                })

                //}

                if (hasRating) {
                    add(
                        EventPageItem(
                            -100,
                            getString(R.string.about_event_rate),
                            presenter::onRateClick
                        ).apply {
                            hasBottomPadding = !hasAgreement && hasPages == true
                        })
                }

                if (hasAgreement) {
                    add(EventPageItem(
                        -110,
                        getString(R.string.about_event_agreement),
                        presenter::onAgreementClick
                    ).apply {
                        hasBottomPadding = hasPages == true
                    })
                }

                addAll(pages?.mapIndexed { index, item ->
                    EventPageItem(
                        item.id ?: 0,
                        item.name ?: "" /*item.menu*/
                    ) { presenter.onPageClick(item) }.apply {
                        hasBottomPadding = index == pages.size - 1
                    }
                } ?: arrayListOf())
            },

            Section().apply {
                setHeader(PartnersTitleItem(-50))
                setHideWhenEmpty(true)
                addAll(partners?.map {
                    EventPartnerItem(
                        it.id ?: 0,
                        it.logo?.uri/*it.logo*/,
                        it.name
                    ) { presenter.onPartnerClick(it) }
                } ?: arrayListOf())
            }
        ))
        swipeToRefresh.isRefreshing = false
    }

    override fun setActionButton(event: EventNew/*EventData*/?, userRegistration: Event.Status?) {
        var textRes: Int? = null
        var clickAction: (() -> Unit)? = null
        var visibility = true

        val actions = if (event?.binds?.eventRegistrationState?.availableActions.isNullOrEmpty())
            arrayListOf("") else event?.binds?.eventRegistrationState?.availableActions
        when {
            event?.binds?.eventRegistrationState?.prohibitions?.registrationClosed == false -> {
                when (actions?.get(0)) {
                    "register" -> {
                        textRes = R.string.event_action_participate
                        clickAction = {
                            event.binds?.eventRegistrationState?.prohibitions?.profileLevelToLow?.value.checkStateLevel {
                                val agreement =
                                    event.userAgreement?.name ?: event.userAgreement?.uri
                                if (/*!BuildConfig.REGISTER_AGREEMENT_ENABLED ||*/ agreement.isNullOrEmpty()) {
                                    presenter.onGoToEventClick()
                                } else {
                                    showAgreementRegisterDialog(agreement)
                                }
                            }
                        }
                    }
                    "withdraw" -> {
                        textRes = R.string.event_action_cancel_request
                        clickAction = {
                            event.binds?.eventRegistrationState?.prohibitions?.profileLevelToLow?.value.checkStateLevel {
                                presenter.onActionCancel()
                            }
                        }
                    }
                    "view" -> {
                        textRes = R.string.event_action_show_event
                        clickAction = {
                            event.binds?.eventRegistrationState?.prohibitions?.profileLevelToLow?.value.checkStateLevel {
                                presenter.onSelectEventClick()
                            }
                        }
                    }
                }
            }
            else -> {
                if (actions?.get(0) ?: "" == "view") {
                    textRes = R.string.event_action_show_event
                    clickAction = {
                        event?.binds?.eventRegistrationState?.prohibitions?.profileLevelToLow?.value.checkStateLevel {
                            presenter.onSelectEventClick()
                        }
                    }
                } else {
                    textRes = R.string.about_event_registration_closed
                }
            }
        }

        /*when {
            event?.status?.value == Event.Status.FINISHED -> {
                visibility = false
            }
            (event?.status?.value?: "") != Event.Status.REGISTRATION -> {
                when (userRegistration) {
                    Event.Status.APPROVED -> {
                        textRes = R.string.event_action_show_event
                        clickAction = { presenter.onSelectEventClick() }
                    }
                    Event.Status.AWAITING, Event.Status.PENDING -> {
                        textRes = R.string.event_action_cancel_request
                        clickAction = { presenter.onActionCancel() }
                    }
                    else -> {
                        textRes = R.string.about_event_registration_closed
                    }
                }
            }
            else -> {
                when (userRegistration) {
                    Event.Status.APPROVED -> {
                        textRes = R.string.event_action_show_event
                        clickAction = { presenter.onSelectEventClick() }
                    }
                    Event.Status.AWAITING, Event.Status.PENDING -> {
                        textRes = R.string.event_action_cancel_request
                        clickAction = { presenter.onActionCancel() }
                    }
                    Event.Status.DECLINED -> {
                        visibility = event?.email.isNullOrEmpty().not()
                        textRes = R.string.event_action_write_to_organisation
                        clickAction = { presenter.onActionWriteToOrganization() }
                    }
                    else -> {
                        textRes = R.string.event_action_participate
                        clickAction = {
                            val agreement = event?.userAgreement?.name?: event?.userAgreement?.uri
                            if (/*!BuildConfig.REGISTER_AGREEMENT_ENABLED ||*/ agreement.isNullOrEmpty()) {
                                presenter.onGoToEventClick()
                            } else {
                                showAgreementRegisterDialog(agreement)
                            }
                        }
                    }
                }
            }
        }*/

        when (screenType) {
            ABOUT_FROM_EVENT -> flRegister.isVisible = false
            else -> flRegister.isVisible = visibility
        }

        //flRegister.isVisible = visibility

        flRegister.btnAction.apply {
            text = textRes?.let { getString(it) }
            if (clickAction != null) {
                setOnClickListener(clickAction)
            } else {
                setOnClickListener(null)
                isEnabled = false
            }
        }

        recyclerView.updatePadding(
            bottom = if (flRegister.isVisible) resources.getDimensionPixelSize(
                R.dimen.about_event_bottom_gradient_height
            ) else 20.dp
        )
    }

    private fun Boolean?.checkStateLevel(hasLevel: () -> Unit) {
        if (this == false) {
            hasLevel()
        } else {
            showStateErrorMessage(StateType.BASE, false, null)
        }
    }

    private fun showAgreementRegisterDialog(url: String) {
        val view =
            layoutInflater.inflate(R.layout.dialog_event_registration_agreement_form, null).apply {
                val agreementText =
                    SpannableString(getString(R.string.auth_agree_user_agreement)).apply {
                        val linkStart = 11
                        val linkEnd = length
                        setSpan(ClickableSpan(drawUnderline = false) {
                            showUserAgreement(url)
                        }, linkStart, linkEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                    }

                tvAgree.apply {
                    text = agreementText
                    movementMethod = LinkMovementMethod.getInstance()
                }

                cbAgree.setOnCheckedChangeListener { _, checked ->
                    btnPositive.isEnabled = checked
                }
            }

        AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
            .apply {
                setOnShowListener {
                    view.apply {
                        btnPositive.apply {
                            isEnabled = false
                            setOnClickListener {
                                presenter.onGoToEventClick()
                                dismiss()
                            }
                        }

                        btnNegative.setOnClickListener {
                            dismiss()
                        }
                    }
                }
            }
            .show()
    }

    private fun showUserAgreement(url: String) {
        try {
            val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(viewIntent)
        } catch (e: Throwable) {
            Toast.makeText(
                requireContext(),
                R.string.about_event_agreement_open_error,
                Toast.LENGTH_LONG
            ).show()
        }
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

    override fun showWriteToOrganizationEmails(emails: List<EventPhoneModel/*EmailAffiliation*/>) {
        AlertDialog.Builder(requireContext())
            .setItems(
                emails.map { it.getAffiliationString(underlinedEmail = true) }
                    .toTypedArray()
            ) { dialog, which ->
                val email = emails[which]
                presenter.onWriteToOrganizationEmailChosen(email)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun showWriteToOrganization(email: EventPhoneModel/*EmailAffiliation*/) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email.value/*email.email*/))
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        }
    }

    override fun showRegistrationFieldsRequest(fields: List<String>) {
        EventRegistrationProfileFieldsDialog(requireContext(), fields) {
            presenter.onShowEditProfileClick()
        }.show()
    }

    override fun showEditProfile(id: String) {
        findNavController().navigate(
            R.id.user_profile_fragment,
            UserFragmentArgs.Builder(id).build().toBundle()
        )
    }

    override fun hideWriteToOrganizationForm() {
        writeMessageDialog?.dismiss()
        writeMessageDialog = null
    }

    override fun showWriteToOrganizationComplete() {
        showToast(R.string.about_event_write_to_organization_complete)
    }

    override fun showWriteToOrganizationError() {
        showToast(R.string.about_event_write_to_organization_error)
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

    override fun showContacts(
        eventName: String,
        phones: List<EventPhoneModel>,
        emails: List<EventPhoneModel>,
        webLinks: List<String>?,
        socialLinks: List<String>?,
        address: String?,
        place: String?,
        mapInfo: MapInfo?,
        places: List<Place>?
        /*eventName: String,
        phones: List<PhoneAffiliation>,
        emails: List<EmailAffiliation>,
        webLinks: List<String>?,
        socialLinks: List<String>?,
        address: String?,
        place: String?,
        mapInfo: MapInfo?,
        places: List<Place>?*/
    ) {
        findNavController().navigate(
            R.id.contacts_fragment, EventContactsFragmentArgs.Builder(
                /*eventName,
                phones.toTypedArray(),
                emails.toTypedArray(),
                webLinks?.toTypedArray()?: arrayOf(),
                socialLinks?.toTypedArray()?: arrayOf(),
                address,
                place,
                mapInfo,
                places?.toTypedArray()*/
                eventName,
                phones.toTypedArray(),
                emails.toTypedArray(),
                webLinks?.toTypedArray() ?: arrayOf(),
                socialLinks?.toTypedArray() ?: arrayOf(),
                address,
                place,
                mapInfo,
                places?.toTypedArray()
            ).build().toBundle()
        )
    }

    override fun showEventRequest(event: String) {
        findNavController().navigate(
            R.id.request_fragment,
            EventRegistrationFragmentArgs.Builder(event).build().toBundle()
        )
    }

    override fun showLogoImage(url: String) {
        findNavController().navigate(
            R.id.image_view_activity,
            ImageViewActivityArgs.Builder(url, null, null, null).build().toBundle()
        )
    }

    override fun showRating(eventId: String) {
        findNavController().navigate(
            R.id.event_rating_fragment,
            EventRatingFragmentArgs.Builder(eventId).build().toBundle()
        )
    }

    override fun showAgreement(url: String) {
        try {
            val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(viewIntent)
        } catch (e: Throwable) {
            Toast.makeText(
                requireContext(),
                R.string.about_event_agreement_open_error,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun showOrganization(organization: String) {
        findNavController().navigate(
            R.id.organization_fragment,
            OrganizationFragmentArgs.Builder(organization).build().toBundle()
        )
    }

    override fun selectEvent() {
        findNavController().navigate(
            R.id.event_tabs_fragment, null, NavOptions.Builder()
                .setPopUpTo(R.id.main_navigation, true)
                .build()
        )

    }

    override fun setupToolbarContent(toolbarContentActionBar: ToolbarContentActionBar) {
        super.setupToolbarContent(toolbarContentActionBar)
        this.toolbarContentActionBar = toolbarContentActionBar
    }

    companion object {
        const val ABOUT_FROM_EVENT = 1
        const val ABOUT_FROM_OTHER = 2
    }
}

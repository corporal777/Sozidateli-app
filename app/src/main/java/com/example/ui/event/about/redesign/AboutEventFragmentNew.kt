package com.example.ui.event.about.redesign

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.*
import com.example.extensions.*
import com.example.holders.EventDataAboutItem
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventActivityItem
import com.example.holders.redesign.EventPageItemNew
import com.example.holders.redesign.SpeakersHorizontalListItem
import com.example.holders.redesign.blocks.EventDetailActivitiesBlock
import com.example.holders.redesign.blocks.EventDetailBlocksLabelItem
import com.example.holders.redesign.blocks.EventDetailPartnersBlock
import com.example.holders.redesign.blocks.EventDetailShowActivitiesButtonBlock
import com.example.interfaces.BackgroundImageFragment
import com.example.ui.base.BaseFragment
import com.example.ui.event.contacts.EventContactsFragmentArgs
import com.example.ui.event.rating.EventRatingFragmentArgs
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.event.speakers.EventSpeakersFragmentArgs
import com.example.ui.event.speakers.UserSpeakerFragmentArgs
import com.example.ui.image.ImageViewActivityArgs
import com.example.ui.organizations.OrganizationFragmentArgs
import com.example.ui.page.PageFragmentArgs
import com.example.ui.partner.PartnerFragmentArgs
import com.example.ui.subevent.SubeventFragmentArgs
import com.example.ui.user.UserFragmentArgs
import com.example.ui.views.EventRegistrationProfileFieldsDialog
import com.example.ui.views.StateType
import com.example.ui.views.TagChipNew
import com.example.ui.views.toolbar.widget.OnTransparentListener
import com.example.util.ClickableSpan
import com.example.util.setImage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import kotlinx.android.synthetic.main.dialog_event_registration_agreement_form.*
import kotlinx.android.synthetic.main.dialog_event_registration_agreement_no_form.view.*
import kotlinx.android.synthetic.main.fragment_about_event_new.*
import setOnClickListener
import javax.inject.Inject
import javax.inject.Provider


class AboutEventFragmentNew() : BaseFragment(), AboutEventContractNew.View,
    BackgroundImageFragment {

    private var screenType = ABOUT_FROM_OTHER
    private var mLightStatus = false

    private var mDy: Int = 0
    private var mMaxOffset = 0f
    override fun layout() = R.layout.fragment_about_event_new


    private val organizationBlock = Section()
    private val activitiesBlock = Section()
    private val speakersBlock = Section()
    private val partnersBlock = Section()
    private val subEventsBlock = Section()


    @InjectPresenter
    lateinit var presenterNew: AboutEventPresenterNew

    @Inject
    lateinit var presenterNewProvider: Provider<AboutEventPresenterNew>

    private var mEventId = ""

    @ProvidePresenter
    fun providePresenter(): AboutEventPresenterNew = presenterNewProvider.get().apply {
        eventId = AboutEventFragmentNewArgs.fromBundle(requireArguments()).eventId
        this@AboutEventFragmentNew.mEventId = eventId
    }

//    private val groupAdapter by lazy {
//        GroupAdapter<GroupieViewHolder>().apply {
//            spanCount = 10
////            add(imageBlock)
////            add(actionBlock)
////            add(organizationBlock)
////            add(speakersBlock)
////            add(programsBlock)
////            add(showActivitiesBlock)
////            add(partnersBlock)
//        }
//    }

    private val speakersAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {

        }
    }

    private val otherContentAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            //add(activitiesBlock)
            //add(partnersBlock)
        }
    }


    private val onSubEventClickListener = object : EventActivityItem.OnEventActivityClickListener {
        override fun onActivityClick(subEvent: EventActivityModel) {
            Log.e("State", subEvent.binds?.userCalendar.toString())
            presenterNew.onSubEventClick(subEvent)
        }

        override fun onAddToScheduleClick(subEvent: EventActivityModel) {
            showToast("Added")
            presenterNew.onAddToScheduleClick(subEvent)
        }

        override fun onRemoveFromScheduleClick(subEvent: EventActivityModel) {
            presenterNew.onRemoveFromScheduleClick(subEvent)
        }

        override fun onUpdateScheduleState(subEvent: EventActivityModel) {
            TODO("Not yet implemented")
        }

    }

    private var aboutItem: EventDataAboutItem? = null
    private var writeMessageDialog: AlertDialog? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mMaxOffset = 425f
        if (tool_bar != null){
            tool_bar.setMaxOffset(mMaxOffset)
        }

        speakersList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = speakersAdapter
        }

        otherContentList.apply {
            adapter = otherContentAdapter
        }


        nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            mDy += scrollY - oldScrollY
            tool_bar.updateTop(mDy.toFloat())
            Log.e("OFFSET", mDy.toString())
        })

        tool_bar.addOnScrollStateListener(object : OnTransparentListener {

            override fun onTransparentStart(fraction: Float) {
                Log.e("START", fraction.toString())
                toolbar_container.apply {
                    alpha = 1f
                    shadow.isVisible = false
                    background = null
                }
            }

            override fun onTransparentMiddle(fraction: Float) {
                Log.e("MIDDLE", fraction.toString())
                toolbar_container.apply {
                    aboutEventToolbar.background = null
                    requireActivity().window.decorView.systemUiVisibility = 0
                    iv_share.setImageResource(R.drawable.ic_share_white)
                    iv_back.setImageResource(R.drawable.ic_back_white)
                    background = ColorDrawable(adjustAlpha(Color.BLACK, fraction/1800))

                    btnAddToCalendar.setTextColor(Color.WHITE)
                    btnAddToCalendar.background =
                        resources.getDrawable(R.drawable.custom_btn_add_to_calendar_background)

                    shadow.isVisible = true
                }
            }

            override fun onTransparentMoreMiddle(fraction: Float) {
                Log.e("MIDDLE", fraction.toString())
                toolbar_container.apply {
                    requireActivity().window.decorView.systemUiVisibility = 0
                    iv_share.setImageResource(R.drawable.ic_share_white)
                    iv_back.setImageResource(R.drawable.ic_back_white)
                    background = ColorDrawable(Color.BLACK)
                    btnAddToCalendar.setTextColor(Color.WHITE)
                    btnAddToCalendar.background =
                        resources.getDrawable(R.drawable.custom_btn_add_to_calendar_background)

                    shadow.isVisible = true
                }
            }

            override fun onTransparentEnd(fraction: Float) {
                Log.e("END", fraction.toString())
                toolbar_container.apply {
                    aboutEventToolbar.background = ColorDrawable(Color.BLACK)
                    //background = ColorDrawable(adjustAlpha(Color.WHITE, (fraction) / 3000))
                    background = ColorDrawable(adjustAlpha(Color.WHITE, (fraction - 2000) / 1800))
                    iv_share.setImageResource(R.drawable.ic_share_black)
                    iv_back.setImageResource(R.drawable.ic_back_black)
                    btnAddToCalendar.setTextColor(Color.BLACK)
                    btnAddToCalendar.background =
                        resources.getDrawable(R.drawable.custom_btn_add_to_calendar_background_black)

                    requireActivity().window.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    shadow.isVisible = true
                }
            }

            override fun onTransparentMoreEnd(fraction: Float) {
                Log.e("END", fraction.toString())
                toolbar_container.apply {
                    background = ColorDrawable(Color.WHITE)
                    aboutEventToolbar.background = ColorDrawable(Color.WHITE)
                    iv_share.setImageResource(R.drawable.ic_share_black)
                    iv_back.setImageResource(R.drawable.ic_back_black)
                    btnAddToCalendar.setTextColor(Color.BLACK)
                    btnAddToCalendar.background =
                        resources.getDrawable(R.drawable.custom_btn_add_to_calendar_background_black)

                    requireActivity().window.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    shadow.isVisible = true
                }
            }

            override fun onTransparentUpdateFraction(p0: Float) {
            }
        })


        iv_share.setOnClickListener(presenterNew::onShareClick)
        iv_back.setOnClickListener {
            findNavController().navigateUp()
        }
//        swipeToRefresh.setOnRefreshListener {
//            presenter.onRefreshRequest()
//        }
    }

    override fun updateSubevent(subEvent: EventActivityModel) {
        subEventsBlock.findGroupBy<EventDetailActivitiesBlock> { true }?.updateButtonState(subEvent)
        //otherContentAdapter.findGroupBy<GroupieViewHolder, EventDetailActivitiesBlock> { true }?.updateButtonState(subEvent)
    }

    override fun setActionButton(event: EventNew/*EventData*/?, userRegistration: Event.Status?) {
        decorActionButton(event)
//        actionBlock.findItemBy<EventDetailActionBlock> { true }?.updateButtonActionState(event)
    }

    override fun showSubEvent(eventId: String, subEventId: String) {
        val args = SubeventFragmentArgs.Builder(eventId, subEventId).build().toBundle()
        findNavController().navigate(R.id.subevent_fragment, args)
    }

    override fun showSpeakerProfile(speakerId: Int) {
        findNavController().navigate(
            R.id.user_speaker_fragment,
            UserSpeakerFragmentArgs.Builder(speakerId.toString(), mEventId).build().toBundle()
        )

    }

    override fun showMap(mapInfo: MapInfo?) {
        findNavController().navigate(
            AboutEventFragmentNewDirections.actionAboutEventFragmentNewToMapFragmentNew(mapInfo))
    }

    override fun setSubEvents(subEvents: List<EventActivityModel>) {
        if (subEvents.isNullOrEmpty()){
            val listPlaceholders = listOf(
                PlaceholderItem(PlaceholderItem.Type.SUB_EVENT),
                PlaceholderItem(PlaceholderItem.Type.SUB_EVENT),
                PlaceholderItem(PlaceholderItem.Type.SUB_EVENT),
                PlaceholderItem(PlaceholderItem.Type.SUB_EVENT))
            subEventsBlock.update(listPlaceholders)
        } else subEventsBlock.update(listOf(EventDetailActivitiesBlock(subEvents, onSubEventClickListener)))
    }


    override fun setEventData(
        eventData: EventNew?,
        userRegistration: Event.Status?,
        pages: List<PageModel>?,
        partners: List<PartnerModel>?,
        showContacts: Boolean,
        userAgreement: String?
    ) {


//        list.clear()
//        eventData?.binds?.member?.forEach {
//            if (it.role?.equals("speaker")!!) {
//                list.add(it)
//            }
//        }
//
//        imageBlock.update(listOf(EventDetailImageBlock(eventData)))
//        actionBlock.update(
//            listOf(
//                EventDetailActionBlock(
//                    eventData,
//                    eventClickListener,
//                    { presenterNew.onGoToEventClick() },
//                    { presenterNew.onActionCancel() },
//                    { showStateErrorMessage(StateType.BASE, false, null) })
//            )
//        )
//        organizationBlock.update(
//            listOf(
//                EventDetailOrganizationBlock(
//                    eventData,
//                    { presenterNew.onChangeFavoriteClick() },
//                    { eventData?.organization?.let { presenterNew.onOrganizationClick(it.toString()) } },
//                )
//            )
//        )
//
//        programsBlock.update(
//            listOf(
//                EventDetailProgramBlock(
//                    eventData,
//                    onSubEventClickListener
//                ) { showToast(it.toString()) })
//        )
//        showActivitiesBlock.update(listOf(EventDetailShowActivitiesButtonBlock {}))
//        partnersBlock.update(
//            listOf(
//                EventDetailPartnersBlock(
//                    getString(R.string.partners_label),
//                    partners
//                )
//            )
//        )
//        speakersBlock.update(
//            listOf(
//                EventDetailSpeakersBlock(
//                    getString(R.string.speakers),
//                    list
//                ) { presenterNew.onSpeakerClick(it) })
//        )

        setMainData(eventData)
        speakersAdapter.update(
            listOf(
                Section().apply {
                    add(EventDetailBlocksLabelItem(getString(R.string.information)))
                    add(EventPageItemNew(1, getString(R.string.how_to_go)) { presenterNew.onMapPageSelected() })
                    if (!pages.isNullOrEmpty()){
                        addAll(pages.map {item ->
                            EventPageItemNew(
                                item.id ?: 0,
                                item.name ?: ""
                            ) { presenterNew.onPageClick(it)  }
                        })
                    }
                },
                Section().apply {
                    if (!eventData?.binds?.member.isNullOrEmpty()){
                        add(EventDetailBlocksLabelItem(getString(R.string.speakers)))
                        add(SpeakersHorizontalListItem(eventData?.binds?.member) {
                            presenterNew.onSpeakerClick(it)
                        })
                    }
            }))


        otherContentAdapter.update(listOf(
            subEventsBlock,
            Section().apply {
                add(EventDetailShowActivitiesButtonBlock {
                    findNavController().navigate(AboutEventFragmentNewDirections.actionAboutEventFragmentToActivitiesFragment(eventData?.id?:0))
                })
                if (!partners.isNullOrEmpty()){
                    add(EventDetailPartnersBlock(getString(R.string.partners_label), partners) {
                        presenterNew.onPartnerClick(it)
                    })
                }
            }
        ))
//        activitiesBlock.update(
//            listOf(
//                EventDetailActivitiesBlock(eventData,onSubEventClickListener),
//                EventDetailShowActivitiesButtonBlock {},
//                EventDetailPartnersBlock(getString(R.string.partners_label), partners, {presenterNew.onPartnerClick(it)})
//
//            ))



        //swipeToRefresh.isRefreshing = false
    }


    override fun changeEventSubscription(isSubscribed: Boolean) {
        decorAddOrganizationToFavoriteButton(isSubscribed)
        //aboutItem?.notifyChanged(isSubscribed)
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
                    if (!message.isNullOrEmpty()) presenterNew.onWriteToOrganizationMessage(message)
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
                presenterNew.onWriteToOrganizationEmailChosen(email)
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
            presenterNew.onShowEditProfileClick()
        }.show()
    }

    override fun showEditProfile(id: String) {
        findNavController().navigate(
            R.id.user_profile_fragment,
            UserFragmentArgs.Builder(id).build().toBundle()
        )
    }

    override fun showShare() {
        showToast("Share")
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

    override fun showContacts(eventName: String, phones: List<EventPhoneModel>, emails: List<EventPhoneModel>, webLinks: List<String>?, socialLinks: List<String>?, address: String?, place: String?, mapInfo: MapInfo?, places: List<Place>?) {
        findNavController().navigate(R.id.contacts_fragment, EventContactsFragmentArgs.Builder(eventName, phones.toTypedArray(), emails.toTypedArray(), webLinks?.toTypedArray() ?: arrayOf(), socialLinks?.toTypedArray() ?: arrayOf(), address, place, mapInfo, places?.toTypedArray()).build().toBundle())
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


    @SuppressLint("ResourceType")
    private fun setMainData(eventData: EventNew?){
        main_content.isVisible = eventData != null


        tvDate.text = eventData?.holdingDate?.from.formatToEventDatesIntervalOnMain(eventData?.holdingDate?.to) ?: ""
        tvLocation.text = eventData?.address?.getShortAddress()
        tvEventTitle.text = eventData?.name
        ivLogo.setImage(eventData?.image?.uri)

        tvDescription.text = eventData?.description

        val limitDate = eventData?.requestsApply?.dateLimit
            ?.parseAndFormat(defaultServerDateFormatter, dateFormatterShortDayFullMothShortYear)
        tvRequestsDate.text = "Заявки принимаются до $limitDate"

        tvAddress.text = eventData?.address?.fullValue ?: ""

        if (!eventData?.phone.isNullOrEmpty()) {
            phone_ln.isVisible = true
            tvPhone.text = eventData?.phone?.get(0)?.value
        }

        if (!eventData?.email.isNullOrEmpty()) {
            email_ln.isVisible = true
            tvEmail.text = eventData?.email?.get(0)?.value
        }
        if (!eventData?.site.isNullOrEmpty()) {
            link_ln.isVisible = true
            tvLink.text = eventData?.site?.get(0)?.value
        }
        if (!eventData?.socialLink.isNullOrEmpty()) {
            network_ln.isVisible = true
            tvSocialNetwork.text = eventData?.socialLink?.get(0)?.value
        }

        val organizationName = eventData?.binds?.organization?.legalInformation?.name?.short
            ?: eventData?.binds?.organization?.legalInformation?.name?.full

        var isFavorite: Boolean = eventData?.binds?.userFavorite != null

        val organizationLogo = eventData?.binds?.organization?.logo?.uri
            ?: "https://upload.wikimedia.org/wikipedia/commons/a/af/Sberbank_logo_2020_en.png?20210830141558"

        tvOrganizationLabel.text = organizationName
        ivOrganizationLogo.apply {
            setImage(organizationLogo)
            setOnClickListener {
                presenterNew.onOrganizationClick(eventData?.organization.toString())
            }
        }

        setTags(eventData)
        decorActionButton(eventData)
        decorAddOrganizationToFavoriteButton(isFavorite)
    }


    private fun decorAddOrganizationToFavoriteButton(isSubscribed : Boolean){
        btnActionFavorite.apply {
            if (isSubscribed) btnActionFavorite.setActionUnfavorite()
            else btnActionFavorite.setActionFavorite()
            setOnClickListener {
                presenterNew.onChangeFavoriteClick()
            }
        }

    }

    private fun decorActionButton(eventData: EventNew?) {

        val eventRegistrationState = eventData?.binds?.eventRegistrationState
        val userAgreement: String? =
            eventData?.userAgreement?.name ?: eventData?.userAgreement?.uri

        if (eventRegistrationState != null) {
            val actions = if (eventRegistrationState.availableActions.isNullOrEmpty())
                arrayListOf("") else eventRegistrationState.availableActions
            when {
                eventRegistrationState.prohibitions?.registrationClosed == false -> {
                    when (actions.get(0)) {
                        "register" -> {
                            btnEventAction.apply {
                                setText(R.string.event_action_participate)
                                setOnClickListener {
                                    eventRegistrationState!!.prohibitions?.profileLevelToLow?.value.checkStateLevel {
                                        if (/*!BuildConfig.REGISTER_AGREEMENT_ENABLED ||*/ userAgreement.isNullOrEmpty()) {
                                            presenterNew.onGoToEventClick()
                                        } else {
                                            showAgreementRegisterDialog(
                                                requireContext(),
                                                userAgreement
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        "withdraw" -> {
                            btnEventAction.apply {
                                setText(R.string.event_action_cancel_request)
                                setOnClickListener {
                                    eventRegistrationState!!.prohibitions?.profileLevelToLow?.value.checkStateLevel {
                                        presenterNew.onActionCancel()
                                    }
                                }
                            }
                        }
                        "view" -> {
                            btnEventAction.apply {
                                setText(R.string.my_events_accepted)
//                                setOnClickListener {
//                                    eventRegistrationState!!.prohibitions?.profileLevelToLow?.value.checkStateLevel {
//                                        //onEventClickListener.onActionShowEvent(eventId)
//                                    }
//                                }
                                isEnabled = false
                            }
                        }
                    }
                }
                else -> {
                    if (actions?.get(0) ?: "" == "view") {
                        btnEventAction.apply {
                            setText(R.string.my_events_accepted)
//                            setOnClickListener {
//                                eventRegistrationState?.prohibitions?.profileLevelToLow?.value.checkStateLevel {
//                                }
//                            }
                            isEnabled = false
                        }
                    } else {
                        btnEventAction.apply {
                            setText(R.string.about_event_registration_closed)
                            isEnabled = false
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("ResourceType")
    private fun setTags(eventData: EventNew?){
        chip_group.apply {
            val createChip: (EventTagModel) -> CompoundButton = {
                TagChipNew(context).apply {
                    id = it.id
                    text = it.name
                    isChecked = false
                    isClickable = true
                    setOnClickListener {
                        showToast(id.toString())
                    }
                }
            }

            removeAllViews()

            val listTags = eventData?.binds?.tag
            if (!listTags.isNullOrEmpty()){
                if (listTags.size > 6) {
                    val otherSize = listTags.size - 5
                    for (i in listTags.indices) {
                        listTags[i].apply {
                            addView(createChip(this))
                        }
                        if (i == 5) break
                    }
                    addView(TagChipNew(context).apply {
                        id = 1
                        text = "Еще $otherSize "
                        isChecked = false
                        isClickable = true
                        val img: Drawable =
                            context.resources.getDrawable(R.drawable.ic_arrow_down_for_tags)
                        setCompoundDrawablesWithIntrinsicBounds(null, null, img, null)
                        setOnClickListener {
                            for (i in 6 until listTags.size) {
                                listTags[i].apply {
                                    addView(createChip(this))
                                }
                            }
                            visibility = View.GONE
                        }

                    })
                } else {
                    listTags.forEach {
                        addView(createChip(it))
                    }
                }
            }
        }
    }

    private fun showAgreementRegisterDialog(context: Context, url: String) {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.dialog_event_registration_agreement_form, null).apply {
                val agreementText =
                    SpannableString(context.getString(R.string.auth_agree_user_agreement)).apply {
                        val linkStart = 11
                        val linkEnd = length
                        setSpan(ClickableSpan(drawUnderline = false) {
                            showUserAgreement(context, url)
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

        AlertDialog.Builder(context)
            .setView(view)
            .create()
            .apply {
                setOnShowListener {
                    view.apply {
                        btnPositive.apply {
                            isEnabled = false
                            setOnClickListener {
                                presenterNew.onGoToEventClick()
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

    private fun showUserAgreement(context: Context, url: String) {
        try {
            val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(viewIntent)
        } catch (e: Throwable) {
            Toast.makeText(context, R.string.about_event_agreement_open_error, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun Boolean?.checkStateLevel(hasLevel: () -> Unit) {
        if (this == false) {
            hasLevel()
        } else {
            showStateErrorMessage(StateType.BASE, false, null)
        }
    }

    override fun onStart() {
        super.onStart()
        if (nestedScrollView != null){
            mDy = nestedScrollView.scrollY
        }
    }

    override fun onPause() {
        super.onPause()
        mDy = 0
    }

    override fun onStop() {
        super.onStop()
        mDy = 0
    }

    companion object {
        const val ABOUT_FROM_EVENT = 1
        const val ABOUT_FROM_OTHER = 2
        const val IsSuccessCheckCreating = "isSuccessCheckCreating"
    }

    override val isLightStatus: Boolean
        get() = mLightStatus

    override fun getFragmentBackgroundDrawable(): Drawable? {
        return null
    }

    @ColorInt
    fun adjustAlpha(@ColorInt color: Int, factor: Float): Int {
        val alpha = Math.round(Color.alpha(color) * factor)
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }
}

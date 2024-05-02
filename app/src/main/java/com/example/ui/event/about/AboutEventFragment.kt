package com.example.ui.event.about

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.BuildConfig
import com.example.R
import com.example.data.models.AboutEventData
import com.example.data.models.EventActivityModel
import com.example.data.models.EventFormModel
import com.example.data.models.EventNew
import com.example.data.models.NewTags
import com.example.data.models.Tag
import com.example.databinding.FragmentAboutEventNewBinding
import com.example.extensions.findItemBy
import com.example.extensions.updateItem
import com.example.extensions.updateItems
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventActivityItem
import com.example.holders.redesign.EventPartnerItem
import com.example.ui.base.BaseFragment
import com.example.ui.event.about.items.EventDetailBlocksLabelItem
import com.example.ui.event.about.items.EventDetailImageItem
import com.example.ui.event.about.items.EventDetailOrganizationItem
import com.example.ui.event.about.items.EventDetailShowActivitiesItem
import com.example.ui.event.about.items.SpeakersHorizontalListItem
import com.example.ui.event.about.items.TagsItem
import com.example.ui.event.activities.ActivitiesFragmentArgs
import com.example.ui.event.formResult.EventFormResultFragment
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.event.speakers.list.EventSpeakersFragmentArgs
import com.example.ui.event.speakers.member.UserSpeakerFragmentArgs
import com.example.ui.organizations.detail.OrganizationFragmentArgs
import com.example.ui.partner.PartnerFragmentArgs
import com.example.ui.subevent.SubEventFragmentArgs
import com.example.ui.views.LinearLayoutManagerAccurateOffset
import com.example.ui.views.dialogs.StateType
import com.example.ui.views.dialogs.MessageDialogWithBrownButton
import com.example.util.openDeviceCalendarApp
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import eightbitlab.com.blurview.BlurAlgorithm
import eightbitlab.com.blurview.RenderEffectBlur
import eightbitlab.com.blurview.RenderScriptBlur
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import com.example.extensions.onScrolled
import com.example.extensions.setOnClickListener
import com.example.extensions.statusBarColorValue
import com.example.ui.views.dialogs.EventDetailInformationBottomSheetDialog
import com.example.ui.views.dialogs.EventAgreementBottomDialog
import com.example.ui.views.dialogs.EventRegistrationSuccessBottomDialog
import com.example.ui.views.dialogs.MessageDialogWithTwoButtons
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.abs


class AboutEventFragment() : BaseFragment<FragmentAboutEventNewBinding>(),
    AboutEventContract.View {

    override fun layout() = R.layout.fragment_about_event_new

    @InjectPresenter
    lateinit var presenter: AboutEventPresenter

    @Inject
    lateinit var presenterProvider: Provider<AboutEventPresenter>

    @ProvidePresenter
    fun providePresenter(): AboutEventPresenter = presenterProvider.get().apply {
        eventId = AboutEventFragmentArgs.fromBundle(requireArguments()).eventId
    }


    private val eventMainSection by lazy {
        Section().apply { updateItem(PlaceholderItem(PlaceholderItem.Type.EVENT_MAIN)) }
    }
    private val eventSpeakersSection by lazy {
        Section().apply {
            setHeader(EventDetailBlocksLabelItem(getString(R.string.speakers)))
            setHideWhenEmpty(true)
        }
    }
    private val eventProgramSection by lazy {
        Section().apply {
            setHeader(EventDetailBlocksLabelItem(getString(R.string.event_program)))
            setFooter(EventDetailShowActivitiesItem { presenter.onShowEventActivitiesClick() })
            setHideWhenEmpty(true)
        }
    }
    private val eventPartnersSection by lazy {
        Section().apply {
            setHeader(EventDetailBlocksLabelItem(getString(R.string.partners_label)))
            setHideWhenEmpty(true)
        }
    }

    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(eventMainSection)
            add(eventSpeakersSection)
            add(eventProgramSection)
            add(eventPartnersSection)
        }
    }


    private val onActionClickListener = object : EventDetailImageItem.OnActionClickListener {
        override fun onActionRegister(url: String?) = presenter.onActionRegister(url)
        override fun onActionCancel() = presenter.onActionCancel()
        override fun onShowUpdateState() = showStateErrorMessage(StateType.BASE, false, null)
        override fun onSubscribeEvent(subscribe: Boolean) = presenter.onSubscribeEventClick(subscribe)
    }

    private val onSubEventClickListener = object : EventActivityItem.OnEventActivityClickListener {
        override fun onSubEventClick(eventId: String, subEvent: EventActivityModel) =
            presenter.onSubEventClick(subEvent)

        override fun onAddToScheduleClick(subEvent: EventActivityModel) =
            presenter.onAddToScheduleClick(subEvent)

        override fun onRemoveFromScheduleClick(subEvent: EventActivityModel) =
            presenter.onAddToScheduleClick(subEvent)
    }

    private val customLayoutManager by lazy { LinearLayoutManagerAccurateOffset(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            eventContentList.apply {
                setItemViewCacheSize(50)
                adapter = groupAdapter
                layoutManager = customLayoutManager
                onScrolled { _, _ ->
                    presenter.changeAppBarBackgroundColorValue(this.computeVerticalScrollOffset())
                }
            }
            toolbar.apply {
                ivShare.setOnClickListener(presenter::onShareClick)
                ivBack.setOnClickListener(findNavController()::navigateUp)
                btnAddToCalendar.setOnClickListener(presenter::onAddEventToCalendarClick)
            }
            swipeToRefreshLayout.setOnRefreshListener { presenter.onRefreshRequest() }
        }
        setupBlurView()
    }

    override fun setEventData(eventData: AboutEventData) {
        setEventFavoriteButton(eventData.event.binds?.userFavorite != null)
        mBinding.toolbar.tbContent.isVisible = true
        mBinding.swipeToRefreshLayout.isRefreshing = false

        eventMainSection.updateItems(
            EventDetailImageItem(eventData.event, requireContext(), onActionClickListener,
                { showEventInformationDialog(eventData.event) },
                { showEventFormResult(eventData.event) }
            ),
            EventDetailOrganizationItem(eventData.event.binds?.organization,
                { presenter.onAddOrganizationToFavoriteClick() },
                { presenter.onOrganizationClick(it) }
            )
        )

        if (!eventData.speakers.isNullOrEmpty()) {
            eventSpeakersSection.updateItem(
                SpeakersHorizontalListItem(
                    eventData.speakers,
                    eventData.showMoreSpeakers,
                    { presenter.onSpeakerClick(it) },
                    { presenter.onShowAllSpeakersClick() })
            )
        }

        eventProgramSection.updateItems(
            if (eventData.tags.isNotEmpty()) TagsItem(eventData.tags) { presenter.onTagSelected() }
            else null,
            eventData.subEvents.map {
                EventActivityItem(
                    eventData.event.id.toString(),
                    it,
                    emptyList(),
                    onSubEventClickListener,
                    eventData.getUserRegistrationState()
                )
            }
        )
        eventPartnersSection.update(
            eventData.partners.map {
                EventPartnerItem(
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

    override fun setActionButton(event: EventNew?) {
        val item = eventMainSection.findItemBy<EventDetailImageItem> { true }
        item?.notifyChanged(event)
    }

    override fun showAgreementRegisterDialog(event: String, url: String) {
        EventAgreementBottomDialog(requireContext(), url)
            .setSelectCallback { presenter.onAcceptRegistrationAgreement(event) }
            .show()
    }

    override fun showEventRegistrationSuccessDialog() {
        EventRegistrationSuccessBottomDialog(requireContext())
            .setSelectCallback { }
            .show()
    }

    private fun showEventInformationDialog(event: EventNew) {
        EventDetailInformationBottomSheetDialog(requireActivity(), event)
            .show()
    }

    private fun showEventFormResult(event: EventNew) {
        val formResult = event.binds?.userFormResult
            ?.firstOrNull { e -> e.formType?.type == EventFormModel.Type.PARTICIPATION } ?: return
        EventFormResultFragment(formResult).show(requireActivity().supportFragmentManager)
    }

    override fun showEventSubscribedDialog(isSubscribed: Boolean?) {
        if (isSubscribed == true)
            MessageDialogWithTwoButtons(requireContext(), "Спасибо", "Мы пришлем Вам уведомление")
        else MessageDialogWithTwoButtons(
            requireContext(),
            null,
            "Вы отписались от уведомления о старте приема заявок на мероприятие",
            "Отмена",
            "Ок"
        ).setSelectCallback {
            if (it) presenter.onSubscribeEventClick(isSubscribed ?: false)
        }
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
            R.id.organization_fragment_new,
            OrganizationFragmentArgs.Builder(organization).build().toBundle()
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

    override fun setEventFavoriteButton(isSubscribed: Boolean) {
        mBinding.toolbar.ivAddToFavorite.apply {
            setImageResource(
                if (!isSubscribed) R.drawable.ic_star else R.drawable.ic_star_filled
            )
            setOnClickListener { presenter.onAddEventToFavoriteClick() }
        }
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

    override fun showCustomLoading() {
        val button = eventMainSection.findItemBy<EventDetailImageItem> { true }?.getActionButton()
        if (button != null) button.showProgressLoading(true)
        else showCustomProgressDialog()
    }

    override fun hideCustomLoading() {
        val button = eventMainSection.findItemBy<EventDetailImageItem> { true }?.getActionButton()
        if (button != null) button.showProgressLoading(false)
        else hideCustomProgressDialog()
    }

    private fun setupBlurView() {
        val algorithm: BlurAlgorithm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            RenderEffectBlur()
        } else RenderScriptBlur(requireContext())

        val windowBackground = requireActivity().window.decorView.background
        mBinding.toolbar.tbBackground.setupWith(mBinding.eventContentList, algorithm)
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(15f)
    }

    override fun updateAppBarBackgroundColorValue(value: Int) {
        mBinding.toolbar.apply {
            if (value <= 0) {
                statusBarColorValue = 0
                tbBackground.alpha = 0f
            } else {
                tbBackground.apply { alpha = abs(value / (1450).toFloat()) }
                mBinding.appBar.apply {
                    if (value >= 1450) {
                        statusBarColorValue = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        changeAppBarElevation(abs(value / 100f))
                    } else {
                        statusBarColorValue = 0
                        changeAppBarElevation(0f)
                    }
                }
            }
        }
    }


    companion object {
        const val ABOUT_FROM_EVENT = 1
        const val ABOUT_FROM_OTHER = 2
    }

}

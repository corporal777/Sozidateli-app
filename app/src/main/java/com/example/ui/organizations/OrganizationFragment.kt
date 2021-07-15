package com.example.ui.organizations

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.*
import com.example.extensions.findItemBy
import com.example.holders.EventDataListItem
import com.example.holders.EventGroup
import com.example.holders.EventStatusItem
import com.example.holders.UserItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.event.about.AboutEventFragment.Companion.ABOUT_FROM_OTHER
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.image.ImageViewActivityArgs
import com.example.ui.search.tabs.SearchTabsFragmentArgs
import com.example.ui.user.UserFragmentArgs
import com.example.ui.views.EventRegistrationProfileFieldsDialog
import com.example.ui.views.UserSubscribeButton
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_organization.*
import kotlinx.android.synthetic.main.fragment_organization.btnAction
import kotlinx.android.synthetic.main.fragment_organization.llContent
import removeUrlUnderline
import javax.inject.Inject
import javax.inject.Provider

class OrganizationFragment : BaseFragment(), OrganizationContract.View, ToolbarFragment {

    override val title: CharSequence
        get() = requireContext().resources.getString(R.string.profile_work_organization)

    @InjectPresenter
    lateinit var presenter: OrganizationPresenter

    @Inject
    lateinit var presenterProvider: Provider<OrganizationPresenter>

    @ProvidePresenter
    fun providePresenter(): OrganizationPresenter = presenterProvider.get().apply {
        organizationId = OrganizationFragmentArgs.fromBundle(requireArguments()).organizationId
    }

    private val onEventClickListener = object : EventStatusItem.OnEventClickListener {
        override fun onActionRegister(event: String) = presenter.onActionRegister(event)
        override fun onActionShowEvent(event: String) = presenter.onActionShowEvent(event)
        override fun onActionCancel(event: String, registrationId: String?) = presenter.onActionCancel(event, registrationId)
        override fun onActionWriteToOrganization(emails: List<EventPhoneModel/*EmailAffiliation*/>) = presenter.onActionWriteToOrganization(emails)
        override fun onShowEventClick(view: View, event: String) = presenter.onShowEventClick(event)
        override fun onShowFilterClick(format: Int) = presenter.onShowFilterClick(format)
    }

    private val usersAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scrollContainer.setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, _: Int ->
            presenter.onScrollPositionChange(scrollY)
        }

        llContent.isVisible = false

        btnAction.apply {
            setOnClickListener {
                when (action) {
                    UserSubscribeButton.Action.FAVORITE -> presenter.onSubscribeClick()
                    UserSubscribeButton.Action.UNFAVORITE -> presenter.onUnsubscribeClick()
                    else -> throw IllegalArgumentException("Wrong action: $it for organization")
                }
            }
        }

        swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
    }

    override fun setOrganization(logo: Bitmap?, background: Bitmap?, organization: OrganizationNew/*Organization, events: List<Event>, users: List<OrganizationMember>*/) {
        ivBackground.apply {
            clipToOutline = true
            if (background == null) {
                isVisible = false
            } else {
                isVisible = true
                setImageBitmap(background)
                setOnImageClickListener(this, organization.backgroundColor?.value/*organization.background*/)
            }
        }
        ivLogo.apply {
            clipToOutline = true
            if (logo == null) {
                isInvisible = true
            } else {
                isInvisible = false
                setImageBitmap(logo)
                setOnImageClickListener(this, organization.logo?.uri/*organization.logo*/)
            }
        }

        tvOrganizationImageName.apply {
            text = /*organization.name*/organization.legalInformation?.name?.full ?: organization.legalInformation?.name?.short
            clipToOutline = true
            ViewCompat.setBackgroundTintList(this, ColorStateList.valueOf(Color.parseColor(if (organization.backgroundColor?.value.isNullOrEmpty()) "#000000" else organization.backgroundColor?.value)
                    ?: ResourcesCompat.getColor(resources, R.color.colorAccent, null)/*ColorStateList.valueOf(organization.backgroundColor.parseColor()
                    ?: ResourcesCompat.getColor(resources, R.color.colorAccent, null)*/))
        }

        tvName.text = /*organization.name*/organization.legalInformation?.name?.full ?: organization.legalInformation?.name?.short

        val links = /*organization.webLinks?.joinToString(separator = "\n")*/organization.site?.joinToString(separator = "\n") { it.getAffiliationString() }
        val hasLinks = !links.isNullOrEmpty()
        tvLinksTitle.isVisible = hasLinks
        tvLinks.apply {
            isVisible = hasLinks
            text = links
            removeUrlUnderline()
        }

        val snLinks = /*organization.socialLinks?.joinToString(separator = "\n")*/organization.socialLink?.joinToString(separator = "\n") { it.getAffiliationString() }
        val hasSnLinks = !snLinks.isNullOrEmpty()
        tvSnLinksTitle.isVisible = hasSnLinks
        tvSnLinks.apply {
            isVisible = hasSnLinks
            text = snLinks
            removeUrlUnderline()
        }

        val emails = /*organization.emails?.joinToString(separator = "\n") { it.getAffiliationString() }*/organization.email?.joinToString(separator = "\n") { it.getAffiliationString() }
        val hasEmails = !emails.isNullOrEmpty()
        tvEmailTitle.isVisible = hasEmails
        tvEmail.apply {
            isVisible = hasEmails
            text = emails
            removeUrlUnderline()
        }

        val phones = /*organization.phones?.joinToString(separator = "\n") { it.getAffiliationString() }*/organization.phone?.joinToString(separator = "\n") { it.getAffiliationString() }
        val hasPhones = !phones.isNullOrEmpty()
        tvPhoneTitle.isVisible = hasPhones
        tvPhone.apply {
            isVisible = hasPhones
            text = phones
            removeUrlUnderline()
        }
        if (!organization.address.isNullOrEmpty()) {
            val hasAddress = /*!organization.addressShort.isNullOrEmpty()*/!organization.address[0].fullValue.isNullOrEmpty() ||
                    !organization.address.isNullOrEmpty()

            tvAddressTitle.isVisible = hasAddress
            tvAddress.apply {
                isVisible = hasAddress
                text = /*organization.addressShort ?: organization.address*/organization.address[0].fullValue// ?: organization.address
            }
        }

        tvDescription.apply {
            isVisible = /*!organization.descriptionFull.isNullOrEmpty()*/!organization.description.isNullOrEmpty()
            text = /*organization.descriptionFull*/organization.description
        }

        tvPeoples.text = getString(R.string.organization_peoples).format(/*organization.totalMembers*/organization.binds?.membersSize)
        rvPeoples.adapter = usersAdapter.apply {
            /*update(users.mapNotNull {
                val user = it.user ?: return@mapNotNull null
                UserItem(
                        user.user_id,
                        user.fullName,
                        user.user_city,
                        user.user_avatar,
                        { presenter.onUserClick(user) },
                        user.getUserSubscribeAction(),
                        { presenter.onUserActionCLick(user) })
            })*/
            organization.binds?.member?.mapNotNull {
                UserItem(
                        it.user?: 0,
                        it.binds?.user?.fullName?: "",
                        it.binds?.user?.address?.city,
                        it.binds?.user?.image?.uri,
                        { presenter.onUserClick(it.binds?.user) },
                        it.binds?.user?.getUserSubscribeAction(),
                        { presenter.onUserActionCLick(it.binds?.user) })
            }?.let { update(it) }
        }
        btnPeoples.apply {
            isVisible = (organization.binds?.membersSize?:0) > 3
            setOnClickListener { presenter.onShowMoreUsersClick() }
        }

        //tvEvents.text = getString(R.string.organization_events)/*.format(organization.totalEvents)*/
        tvEvents.text = getString(R.string.organization_events).format(organization.binds?.eventsSize)
        rvEvents.apply {
            adapter = GroupAdapter<GroupieViewHolder>().apply {
                organization.binds?.events?.map(::createItem)?.let { update(it) }
            }
        }
        btnEvents.apply {
            isVisible = (organization.binds?.eventsSize?:0) > 3
            setOnClickListener { presenter.onShowMoreEventsClick() }
        }

        llContent.isVisible = true
        swipeToRefresh.isRefreshing = false
    }

    private fun createItem(itemData: EventNew?): Group {
       return EventGroup(
                /*itemData.id,
                itemData.status,
                itemData.userRegistration,
                itemData.backgroundColor,
                itemData.backgroundImage,
                itemData.takeFormat(),
                itemData.organization?.emails,
                !itemData.canRegister,
                onEventClickListener,
                EventDataListItem(
                        -itemData.id.toLong(),
                        itemData.name,
                        itemData.shortAddress ?: itemData.addressCity,
                        itemData.conferenceStart,
                        itemData.conferenceFirstActivityStart
                ).apply {
                    showStartTime = false
                },
                itemData.userAgreement*/
                itemData?.id.toString(),
                itemData?.status?.value,
                /*if (itemData?.binds?.userRegister?.isNotEmpty() == true) itemData.binds?.userRegister?.get(0)?.status?.value else null*/itemData?.binds?.currentUserRegistration?.status?.value,
                itemData?.binds?.organization?.backgroundColor?.value,
                itemData?.image?.uri,
                EventFormat(name = if (itemData?.format?.name.isNullOrEmpty()) itemData?.format?.custom?: "" else itemData?.format?.name?: ""),
                itemData?.binds?.organization?.email,
                /*!itemData?.binds?.rights?.registration!!*/(itemData?.status?.value?: "") != Event.Status.REGISTRATION,
                onEventClickListener,
                EventDataListItem(
                        -(itemData?.id?.toLong()?: 0),
                        itemData?.name,
                        itemData?.address?.getShortAddress(),
                        itemData?.holdingDate?.from,
                        itemData?.binds?.getFirstActionStartDate()
                ).apply {
                    showStartTime = false
                },
                itemData?.userAgreement?.name?: itemData?.userAgreement?.uri
        )
    }

    private fun setOnImageClickListener(imageView: ImageView, url: String?) {
        imageView.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(),
                    Pair(it, it.transitionName)
            )

            findNavController().navigate(
                    R.id.image_view_activity,
                    ImageViewActivityArgs.Builder(url, null, null, it.transitionName).build().toBundle(),
                    null,
                    ActivityNavigatorExtras(options)
            )
        }
    }

    override fun setSubscribed(isSubscribed: Boolean) {
        btnAction.apply {
            setAction(if (isSubscribed) UserSubscribeButton.Action.UNFAVORITE else UserSubscribeButton.Action.FAVORITE)
        }
    }

    override fun updateUser(user: /*User*/UserDetail?) {
        val idLong = user?.id?.toLong()
        val item = usersAdapter.findItemBy { userItem: UserItem -> userItem.id == idLong } ?: return
        item.notifyChanged(user?.getUserSubscribeAction())
    }

    override fun showWriteToOrganizationEmails(emails: List<EventPhoneModel/*EmailAffiliation*/>) {
        AlertDialog.Builder(requireContext())
                .setItems(emails.map { it.getAffiliationString() }.toTypedArray()) { dialog, which ->
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

    override fun showAboutEvent(event: String) {
        findNavController().navigate(R.id.about_event_fragment, AboutEventFragmentArgs.Builder(event, ABOUT_FROM_OTHER).build().toBundle())
    }

    override fun showEventRequest(event: String) {
        findNavController().navigate(R.id.request_fragment, EventRegistrationFragmentArgs.Builder(event).build().toBundle())
    }

    override fun selectEvent() {
        findNavController().navigate(R.id.event_tabs_fragment, null, NavOptions.Builder()
                .setPopUpTo(R.id.main_navigation, true)
                .build())
    }

    override fun showSearch(format: Int) {
        val filter = SearchFilter.Event().apply { this.format = format }
        findNavController().navigate(R.id.search_tabs_fragment, SearchTabsFragmentArgs.Builder(filter).build().toBundle())
    }

    override fun showEvents(organizationId: String) {
        findNavController().navigate(OrganizationFragmentDirections.organizationToOrganizationEvents(organizationId))
    }

    override fun showAboutEvent(event: Event) {
        findNavController().navigate(R.id.about_event_fragment, AboutEventFragmentArgs.Builder(event.id, ABOUT_FROM_OTHER).build().toBundle())
    }

    override fun showEventRequest(event: Event) {
        findNavController().navigate(R.id.request_fragment, EventRegistrationFragmentArgs.Builder(event.id).build().toBundle())
    }

    override fun showUsers(organizationId: String) {
        findNavController().navigate(OrganizationFragmentDirections.organizationToOrganizationUsers(organizationId))
    }

    override fun showUser(id: String) {
        findNavController().navigate(OrganizationFragmentDirections.organizationToUser(id))
    }

    override fun changeScrollY(scroll: Int) {
        scrollContainer.scrollTo(0, scroll)
    }

    override fun showRegistrationFieldsRequest(fields: List<String>) {
        EventRegistrationProfileFieldsDialog(requireContext(), fields) {
            presenter.onShowEditProfileClick()
        }
                .show()
    }

    override fun showEditProfile(id: String) {
        findNavController().navigate(R.id.user_profile_fragment, UserFragmentArgs.Builder(id).build().toBundle())
    }

    override fun layout() = R.layout.fragment_organization
}

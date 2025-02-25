package com.example.ui.organizations.detail

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app.R
import com.example.app.databinding.FragmentOrganizationBinding
import com.example.data.models.EventNew
import com.example.data.models.OrganizationMemberModel
import com.example.data.models.OrganizationNew
import com.example.extensions.findItemBy
import com.example.extensions.updateItem
import com.example.extensions.updateItems
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventListItem
import com.example.ui.base.BaseToolbarFragment
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.image.ImageViewActivityArgs
import com.example.ui.organizations.detail.items.EventsTitleItem
import com.example.ui.organizations.detail.items.OrganizationHeaderItem
import com.example.ui.organizations.detail.items.OrganizationInfoItem
import com.example.ui.organizations.detail.items.OrganizationMemberItem
import com.example.ui.organizations.detail.items.ShowButtonItem
import com.example.ui.organizations.events.OrganizationEventsFragmentArgs
import com.example.ui.organizations.members.OrganizationMembersFragmentArgs
import com.example.ui.user.UserFragmentArgs
import com.example.ui.views.dialogs.EventAgreementBottomSheet
import com.example.ui.views.dialogs.StateType
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.Section
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class OrganizationFragment : BaseToolbarFragment<FragmentOrganizationBinding>(),
    OrganizationContract.View {

    @InjectPresenter
    lateinit var presenter: OrganizationPresenter

    @Inject
    lateinit var presenterProvider: Provider<OrganizationPresenter>

    @ProvidePresenter
    fun providePresenter(): OrganizationPresenter = presenterProvider.get().apply {
        organizationId = OrganizationFragmentArgs.fromBundle(requireArguments()).organizationId
    }

    private val imageClickListener = { url: String, imageView: ImageView ->
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            requireActivity(),
            Pair(imageView, imageView.transitionName)
        )

        findNavController().navigate(
            R.id.image_view_activity,
            ImageViewActivityArgs.Builder(url, null, null, imageView.transitionName).build()
                .toBundle(),
            null,
            ActivityNavigatorExtras(options)
        )
    }

    private val onEventListener = object : EventListItem.OnEventClickListener {
        override fun onActionRegister(event: EventNew) = presenter.onActionRegister(event, false)
        override fun onActionCancel(event: EventNew) = presenter.onActionCancel(event)
        override fun onShowEventClick(eventId: String) = presenter.onShowEventClick(eventId)
        override fun onShowUpdateState() = showStateErrorMessage(StateType.BASE, false, null)
        override fun onShowNeedAuth(eventId: String) = presenter.onShowAuthorization(eventId)
    }

    private val mainDataSection by lazy {
        Section().apply { updateItem(PlaceholderItem(PlaceholderItem.Type.ORGANIZATION_MAIN)) }
    }
    private val eventsDataSection by lazy { Section().apply { setHideWhenEmpty(true) } }
    private val membersDataSection by lazy { Section().apply { setHideWhenEmpty(true) } }


    private val groupAdapter by lazy {
        GroupieAdapter().apply {
            add(mainDataSection)
            add(eventsDataSection)
            add(membersDataSection)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            rvOrganization.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = groupAdapter
            }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }
    }

    override fun setOrganizationsData(organization: OrganizationNew) {
        mBinding.swipeToRefresh.isRefreshing = false
        mainDataSection.updateItems(
            OrganizationHeaderItem(
                organization,
                imageClickListener
            ) { presenter.onAddOrganizationFavoriteClick(it) },
            OrganizationInfoItem(
                organization.id,
                organization.site?.joinToString(separator = "\n") { it.getAffiliationString() },
                organization.socialLink?.joinToString(separator = "\n") { it.getAffiliationString() },
                organization.email?.joinToString(separator = "\n") { it.getAffiliationString() },
                organization.phone?.joinToString(separator = "\n") { it.getAffiliationString() },
                organization.address?.firstOrNull()?.fullValue
            )
        )
    }


    override fun setEventsData(events: List<EventNew>, totalSize: Int) {
        eventsDataSection.apply {
            setHeader(EventsTitleItem(getString(R.string.organization_events)))
            if (totalSize > 3)
                setFooter(ShowButtonItem(getString(R.string.organization_events_watch)) { presenter.onShowMoreEventsClick() })
            update(events.map { EventListItem(it, presenter.isTemporaryUser(), onEventListener) })
        }
    }


    override fun setMembersData(members: List<OrganizationMemberModel>, totalSize: Int) {
        membersDataSection.apply {
            setHeader(EventsTitleItem(getString(R.string.organization_peoples).format(totalSize)))
            if (totalSize > 3)
                setFooter(ShowButtonItem(getString(R.string.organization_peoples_watch)) { presenter.onShowMoreUsersClick() })
            update(members.map { member ->
                OrganizationMemberItem(
                    member.user,
                    member.binds?.user?.nameLastName,
                    member.binds?.user?.address?.shortAddres,
                    member.binds?.user?.loadUserImage(),
                    member.binds?.userFavorite != null,
                    presenter.isCurrentUser(member.binds?.user?.id.toString()),
                    { user -> presenter.onUserClick(user.toString()) },
                    { id -> presenter.onAddUserFavoriteCLick(member) }
                )
            })
        }
    }


    override fun updateOrganization(organization: OrganizationNew) {
        val item = mainDataSection.findItemBy<OrganizationHeaderItem> { true }
        item?.notifyChanged(organization)
    }

    override fun updateUser(member: OrganizationMemberModel) {
        val idLong = member.id?.toLong()
        val item = membersDataSection.findItemBy<OrganizationMemberItem> { it.id == idLong }
        item?.notifyChanged(member.binds?.userFavorite != null)
    }

    override fun updateEvent(event: EventNew) {
        val idLong = event.id?.toLong()
        val item = eventsDataSection.findItemBy<EventListItem> { i -> i.id == idLong } ?: return
        item.notifyChanged(event)
    }


    override fun showAllUsers(organizationId: String) {
        val args = OrganizationMembersFragmentArgs.Builder(organizationId).build().toBundle()
        findNavController().navigate(R.id.organization_members_fragment, args)
    }

    override fun showAllEvents(organizationId: String) {
        val args = OrganizationEventsFragmentArgs.Builder(organizationId).build().toBundle()
        findNavController().navigate(R.id.organization_events_fragment, args)
    }

    override fun showUser(id: String) {
        val args = UserFragmentArgs.Builder(id).build().toBundle()
        findNavController().navigate(R.id.user_fragment, args)
    }

    override fun showCurrentUser() {
        findNavController().navigate(R.id.user_profile_fragment)
    }

    override fun showAboutEvent(event: String) {
        val args = AboutEventFragmentArgs.Builder(event).build().toBundle()
        findNavController().navigate(R.id.about_event_fragment, args)
    }

    override fun showEventRequest(event: String) {
        val args = EventRegistrationFragmentArgs.Builder(event).build().toBundle()
        findNavController().navigate(R.id.request_fragment, args)
    }

    override fun showAuthorization() {
        findNavController().navigate(R.id.authorization_fragment)
    }

    override fun showAgreementRegisterDialog(event: EventNew) {
        EventAgreementBottomSheet(requireContext(), event.userAgreement?.uri ?: "")
            .setSelectCallback { if (it) presenter.onActionRegister(event, true) }
            .show()
    }

    override fun animationType(): AnimType = AnimType.FADE
    override fun layout(): Int = R.layout.fragment_organization
    override fun binding() = FragmentOrganizationBinding::class.java
    override val title: CharSequence by lazy { getString(R.string.profile_work_organization) }
    override fun scrollingView(): View = mBinding.rvOrganization
}
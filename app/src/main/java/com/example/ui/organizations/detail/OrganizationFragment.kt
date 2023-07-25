package com.example.ui.organizations.detail

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.EventNew
import com.example.data.models.OrganizationMemberModel
import com.example.data.models.OrganizationNew
import com.example.databinding.FragmentOrganizationBinding
import com.example.extensions.findItemBy
import com.example.extensions.updateItem
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventItemNew
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.event.about.AboutEventFragmentNewArgs
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.image.ImageViewActivityArgs
import com.example.ui.organizations.detail.items.*
import com.example.ui.organizations.events.OrganizationEventsFragmentArgs
import com.example.ui.organizations.members.OrganizationMembersFragmentArgs
import com.example.ui.user.UserFragmentArgs
import com.example.ui.views.StateType
import com.example.ui.views.dialogs_new.EventAgreementRegisterDialog
import com.example.ui.views.toolbar.ToolbarContent
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import javax.inject.Inject
import javax.inject.Provider

class OrganizationFragment : BaseFragment<FragmentOrganizationBinding>(),
    OrganizationContract.View, ToolbarFragment {

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

    private val onEventClickListener = object : EventItemNew.OnEventClickListener {
        override fun onActionRegister(event: String, agreementUrl: String?) =
            presenter.onActionRegister(event, agreementUrl)

        override fun onActionCancel(event: String, registrationId: String?) =
            presenter.onActionCancel(event, registrationId)

        override fun onShowEventClick(view: View, event: String) = presenter.onShowEventClick(event)
        override fun onShowUpdateState() = showStateErrorMessage(StateType.BASE, false, null)
    }

    private val mainDataSection by lazy {
        Section().apply {
            setPlaceholder(PlaceholderItem(PlaceholderItem.Type.ORGANIZATION_MAIN))
        }
    }
    private val infoDataSection = Section()
    private val eventsDataSection by lazy {
        Section().apply {
            setHeader(
                EventsTitleItem(
                    getString(R.string.organization_events),
                    pTop = 20,
                    pBottom = 10
                )
            )
            setFooter(ShowButtonItem(getString(R.string.organization_events_watch)) {
                presenter.onShowMoreEventsClick()
            })
            setHideWhenEmpty(true)
        }
    }
    private val membersDataSection by lazy {
        Section().apply {
            setFooter(ShowButtonItem(getString(R.string.organization_peoples_watch)) {
                presenter.onShowMoreUsersClick()
            })
            setHideWhenEmpty(true)
        }
    }


    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(mainDataSection)
            add(infoDataSection)
            add(eventsDataSection)
            add(membersDataSection)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            rvOrganization.apply {
                adapter = groupAdapter
            }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }
    }


    override fun setMainData(organization: OrganizationNew) {
        mainDataSection.updateItem(
            OrganizationHeaderItem(
                organization,
                imageClickListener
            ) { presenter.onAddOrganizationFavoriteClick(it) }
        )
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun setInformationData(organization: OrganizationNew) {
        infoDataSection.updateItem(
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

    override fun setEventsData(events: List<EventNew>) {
        eventsDataSection.update(events.map {
            EventItemNew(
                it,
                onEventClickListener,
            )
        })
    }


    override fun setMembersData(members: List<OrganizationMemberModel>, totalSize: Int) {
        membersDataSection.apply {
            update(
                listOf(EventsTitleItem(
                    getString(R.string.organization_peoples).format(totalSize),
                    pBottom = 10
                )).plus(
                    members.map { member ->
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
                    }
                )
            )
        }
    }


    override fun updateOrganizationSubscription(organization: OrganizationNew) {
        val item = mainDataSection.findItemBy<OrganizationHeaderItem> { true }
        item?.notifyChanged(organization)
    }

    override fun updateUserSubscription(userId: Int, isSubscribed: Boolean) {
        val idLong = userId.toLong()
        val item =
            membersDataSection.findItemBy<OrganizationMemberItem> { userItem -> userItem.id == idLong }
                ?: return
        item.notifyChanged(isSubscribed)
    }

    override fun updateEvent(event: EventNew) {
        val idLong = event.id?.toLong()
        val item = eventsDataSection.findItemBy<EventItemNew> { i -> i.id == idLong } ?: return
        item.notifyChanged(event)
    }


    override fun showAllUsers(organizationId: String) {
        findNavController().navigate(
            R.id.organization_members_fragment,
            OrganizationMembersFragmentArgs.Builder(organizationId).build().toBundle()
        )
    }

    override fun showAllEvents(organizationId: String) {
        findNavController().navigate(
            R.id.organization_events_fragment,
            OrganizationEventsFragmentArgs.Builder(organizationId).build().toBundle()
        )
    }

    override fun showUser(id: String) {
        findNavController().navigate(
            R.id.user_fragment,
            UserFragmentArgs.Builder(id).build().toBundle()
        )
    }

    override fun showCurrentUser(id: String) {
        findNavController().navigate(R.id.user_profile_fragment)
    }

    override fun showAboutEvent(event: String) {
        findNavController().navigate(
            R.id.about_event_fragment_new,
            AboutEventFragmentNewArgs.Builder(event).build().toBundle()
        )
    }

    override fun showEventRequest(event: String) {
        findNavController().navigate(
            R.id.request_fragment,
            EventRegistrationFragmentArgs.Builder(event).build().toBundle()
        )
    }

    override fun showAgreementRegisterDialog(event: String, url: String) {
        EventAgreementRegisterDialog(requireContext(), url).setSelectCallback {
            presenter.onAcceptRegistrationAgreement(event)
        }
    }

    override fun layout(): Int = R.layout.fragment_organization
    override val title: CharSequence by lazy { getString(R.string.profile_work_organization) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: (value: Int) -> Unit) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}

}
package com.example.ui.organizations.members

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.OrganizationMemberModel
import com.example.databinding.LayoutListBinding
import com.example.holders.PlaceholderItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.organizations.detail.items.OrganizationMemberItem
import com.example.ui.user.UserFragmentArgs
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import javax.inject.Inject
import javax.inject.Provider

class OrganizationMembersFragment : BaseFragment<LayoutListBinding>(),
    OrganizationMembersContract.View, ToolbarFragment {

    @InjectPresenter
    lateinit var presenter: OrganizationMembersPresenter

    @Inject
    lateinit var presenterProvider: Provider<OrganizationMembersPresenter>

    @ProvidePresenter
    fun providePresenter(): OrganizationMembersPresenter = presenterProvider.get().apply {
        organizationId = OrganizationMembersFragmentArgs.fromBundle(requireArguments()).organizationId
    }

    private val adapter = PaginationListGroupAdapter<GroupieViewHolder>().apply {
        setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
            override fun onItemTake(position: Int) {
                presenter.onItemTake(position)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            recyclerView.apply {
                adapter = this@OrganizationMembersFragment.adapter
            }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }
    }

    override fun setData(members: List<OrganizationMemberModel?>) {
        adapter.update(
            members.map { member ->
                if (member == null) PlaceholderItem(PlaceholderItem.Type.USER)
                else {
                    OrganizationMemberItem(
                        member.user,
                        member.binds?.user?.nameLastName,
                        member.binds?.user?.address?.shortAddres,
                        member.binds?.user?.loadUserImage(),
                        member.binds?.userFavorite != null,
                        presenter.isCurrentUser(member.binds?.user?.id.toString()),
                        { user -> presenter.onMemberClick(user) },
                        { user -> presenter.onAddUserFavoriteCLick(member) }
                    )
                }
            }
        )
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun showUser(userId: String) {
        val args = UserFragmentArgs.Builder(userId).build().toBundle()
        findNavController().navigate(R.id.user_fragment, args)
    }

    override fun showCurrentUser(userId: String) {
        findNavController().navigate(R.id.user_profile_fragment)
    }

    override fun layout() = R.layout.layout_list
    override val title: CharSequence by lazy { getString(R.string.organization_members) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: (value: Int) -> Unit) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}

package com.example.ui.organizations.members

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.OrganizationNewMemberModel
import com.example.databinding.LayoutListBinding
import com.example.holders.OrganizationUserItem
import com.example.ui.base.BaseFragmentNew
import com.example.ui.views.toolbar.SimpleTitleToolbar
import com.example.util.PositionOffsetScrollListener
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import javax.inject.Inject
import javax.inject.Provider

class OrganizationMembersFragment : BaseFragmentNew<LayoutListBinding>(),
    OrganizationMembersContract.View, SimpleTitleToolbar {

    @InjectPresenter
    lateinit var presenter: OrganizationMembersPresenter

    @Inject
    lateinit var presenterProvider: Provider<OrganizationMembersPresenter>

    @ProvidePresenter
    fun providePresenter(): OrganizationMembersPresenter = presenterProvider.get().apply {
        organizationId =
            OrganizationMembersFragmentArgs.fromBundle(requireArguments()).organizationId
    }

    val adapter = PaginationListGroupAdapter<GroupieViewHolder>().apply {
        setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
            override fun onItemTake(position: Int) {
                presenter.onItemTake(position)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarTitle("")
        mBinding.apply {
            recyclerView.apply {
                adapter = this@OrganizationMembersFragment.adapter
                addOnScrollListener(PositionOffsetScrollListener { position, offset ->
                    presenter.onScrollChange(position, offset)
                })
            }

            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }
    }

    override fun setData(members: List</*OrganizationMember*/OrganizationNewMemberModel>) {
        adapter.update(members.mapNotNull {
            val user = it.binds?.user ?: return@mapNotNull null
            OrganizationUserItem(
                it.binds.user.id,
                user.nameLastName,
                user.image?.uri,
                it.position?.value
            ) { presenter.onMemberClick(it) }
        })
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun scrollToPositionWithOffset(position: Int, offset: Int) {
        (mBinding.recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
            position,
            offset
        )
    }

    override fun showUser(userId: String) {
        findNavController().navigate(
            OrganizationMembersFragmentDirections.organizationMembersToUser(
                userId
            )
        )
    }

    override fun layout() = R.layout.layout_list
}

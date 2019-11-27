package com.example.ui.organizations.members

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.OrganizationMember
import com.example.holders.OrganizationUserItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.util.LayoutListWithPlaceholderUtil
import com.example.util.PositionOffsetScrollListener
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.layout_list_with_placeholder.*
import javax.inject.Inject
import javax.inject.Provider

class OrganizationMembersFragment : BaseFragment(), OrganizationMembersContract.View, ToolbarFragment {

    override val title = ""

    @InjectPresenter
    lateinit var presenter: OrganizationMembersPresenter

    @Inject
    lateinit var presenterProvider: Provider<OrganizationMembersPresenter>

    @ProvidePresenter
    fun providePresenter(): OrganizationMembersPresenter = presenterProvider.get().apply {
        organizationId = OrganizationMembersFragmentArgs.fromBundle(arguments!!).organizationId
    }

    private lateinit var placeholderUtil: LayoutListWithPlaceholderUtil

    val adapter = PaginationListGroupAdapter<GroupieViewHolder>().apply {
        setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
            override fun onItemTake(position: Int) {
                presenter.onItemTake(position)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = this@OrganizationMembersFragment.adapter
            addOnScrollListener(PositionOffsetScrollListener { position, offset ->
                presenter.onScrollChange(position, offset)
            })
        }

        placeholderUtil = LayoutListWithPlaceholderUtil(view).apply { setDefault() }
        swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
    }

    override fun setData(members: List<OrganizationMember>) {
        adapter.update(members.map {
            val user = it.user
            OrganizationUserItem(it.id, user.fullName, user.user_avatar, it.position) { presenter.onMemberClick(it) }
        })
        placeholderUtil.isDataLoad = true
        swipeToRefresh.isRefreshing = false
    }

    override fun scrollToPositionWithOffset(position: Int, offset: Int) {
        (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, offset)
    }

    override fun showUser(userId: String) {
        findNavController().navigate(OrganizationMembersFragmentDirections.organizationMembersToUser(userId))
    }

    override fun layout() = R.layout.layout_list_with_placeholder
}

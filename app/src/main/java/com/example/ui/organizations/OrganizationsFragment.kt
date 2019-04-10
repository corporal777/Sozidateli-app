package com.example.ui.organizations

import android.os.Bundle
import android.view.View
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import com.example.R
import com.example.adapters.SimplePagingRecyclerViewAdapter
import com.example.adapters.ViewHolder
import com.example.data.models.Organization
import com.example.extensions.dp
import com.example.ui.base.BaseFragment
import com.example.util.LayoutListWithPlaceholderUtil
import com.example.util.PositionOffsetScrollListener
import kotlinx.android.synthetic.main.item_organization.*
import kotlinx.android.synthetic.main.layout_list_with_placeholder.*
import setCircleImageWithPlaceholder

abstract class OrganizationsFragment<P : OrganizationsPresenter> : BaseFragment(), OrganizationsContract.View {

    open lateinit var presenter: P

    private lateinit var placeholderUtil: LayoutListWithPlaceholderUtil

    private val adapter: SimplePagingRecyclerViewAdapter<Organization> by lazy {
        object : SimplePagingRecyclerViewAdapter<Organization>(
                { oldItem, newItem -> oldItem.id == newItem.id },
                { oldItem, newItem -> oldItem == newItem }
        ) {
            override fun getItemLayout(itemView: Int) = R.layout.item_organization

            override fun onBindItem(viewHolder: ViewHolder, item: Organization?, position: Int) {
                item!!
                viewHolder.apply {
                    ivLogo.setCircleImageWithPlaceholder(item.logo, R.drawable.ic_launcher_background)

                    tvLabel.text = item.name
                    btnAction.apply {
                        text = getActionText(item)
                        setOnClickListener { onItemActionClick(item) }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = this@OrganizationsFragment.adapter
            if (itemDecorationCount == 0) {
                addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(context, VERTICAL))
            }
            addOnScrollListener(PositionOffsetScrollListener { position, offset ->
                presenter.onScrollChange(position, offset)
            })

            clipToPadding = false
            setPadding(0, 16.dp, 0, 0)
        }

        placeholderUtil = LayoutListWithPlaceholderUtil(view).apply { setDefault() }
    }

    override fun setData(data: PagedList<Organization>) {
        adapter.submitList(data)
        placeholderUtil.isDataLoad = true
    }

    override fun scrollToPositionWithOffset(position: Int, offset: Int) {
        (recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).scrollToPositionWithOffset(position, offset)
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.layout_list_with_placeholder

    abstract fun onItemActionClick(organization: Organization)
    abstract fun getActionText(organization: Organization): String
}

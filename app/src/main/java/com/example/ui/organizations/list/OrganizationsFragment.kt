package com.example.ui.organizations.list

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.OrganizationNew
import com.example.data.models.OrganizationsFilter
import com.example.databinding.LayoutListBinding
import com.example.extensions.findItemBy
import com.example.extensions.updateItem
import com.example.holders.*
import com.example.ui.base.BaseFragmentNew
import com.example.ui.event.list.recommendations.items.NoEventItem
import com.example.ui.organizations.detail.OrganizationFragmentArgs
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import onScrolled
import javax.inject.Inject
import javax.inject.Provider

class OrganizationsFragment : BaseFragmentNew<LayoutListBinding>(), OrganizationsContract.View{

    @InjectPresenter
    lateinit var presenter: OrganizationsPresenter

    @Inject
    lateinit var presenterProvider: Provider<OrganizationsPresenter>

    @ProvidePresenter
    fun providePresenter(): OrganizationsPresenter = presenterProvider.get().apply {
        filter = OrganizationsFragmentArgs.fromBundle(requireArguments()).filter
    }

    private val headGroup = Section()
    private val organizationSection = Section()

    private val adapter by lazy {
        PaginationListGroupAdapter<GroupieViewHolder>().apply {
            add(headGroup)
            add(organizationSection)
            setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
                override fun onItemTake(position: Int) {
                    val headCount = headGroup.itemCount
                    if (position < headCount) return
                    presenter.onItemTake(position - headCount)
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            recyclerView.apply {
                adapter = this@OrganizationsFragment.adapter
            }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }
    }

    override fun setNoFilterHeader() {
        headGroup.update(
            listOf(
                ScreenLabelItem(getString(R.string.tab_organizations_title)),
                OrganizationsHeaderItem(object : OrganizationsHeaderItem.OnFilterClickListener {
                    override fun onFavoritesClick() = presenter.onFavoritesClick()
                })
            )
        )
    }

    override fun setFavoritesHeader() {
        headGroup.updateItem(ScreenLabelItem(getString(R.string.organizations_favorites)))
    }

    override fun changeSubscription(organization: OrganizationNew/*Organization*/) {
        val idLong = organization.id?.toLong()
        adapter.findItemBy { it: OrganizationItem -> it.id == idLong }?.notifyChanged()
    }

    override fun setOrganizations(organizations: List<OrganizationNew/*Organization*/?>) {
        organizationSection.update(organizations.map {
            if (it == null) PlaceholderItem(PlaceholderItem.Type.ORGANIZATION)
            else OrganizationItem(
                it,
                { presenter.onOrganizationClick(it) },
                { presenter.onRemoveFromFavoriteClick(it) }
            )
        })
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun showNoFilterEmptyListPlaceholder() {
        organizationSection.updateItem(NoDataItem(getString(R.string.empty_list_placeholder_message)))
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun showFavoritesEmptyListPlaceholder() {
        organizationSection.updateItem(
            NoEventItem(
                getString(R.string.blank_list_error),
                getString(R.string.organizations_favorites_empty_list_description)
            )
        )
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun showOrganization(organization: OrganizationNew/*Organization*/) {
        findNavController().navigate(
            R.id.organization_fragment_new,
            OrganizationFragmentArgs.Builder(organization.id.toString()).build().toBundle()
        )
    }

    override fun showFavorites() {
        findNavController().navigate(
            OrganizationsFragmentDirections.organizationsFragmentToSelf(
                OrganizationsFilter.FAVORITES
            )
        )
    }

    override fun layout() = R.layout.layout_list
}

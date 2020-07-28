package com.example.ui.organizations.list

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Organization
import com.example.data.models.OrganizationsFilter
import com.example.holders.*
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.organizations.OrganizationFragmentArgs
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.layout_list.*
import javax.inject.Inject
import javax.inject.Provider

class OrganizationsFragment : BaseFragment(), OrganizationsContract.View, ToolbarFragment {

    override val title = ""

    @InjectPresenter
    lateinit var presenter: OrganizationsPresenter

    @Inject
    lateinit var presenterProvider: Provider<OrganizationsPresenter>

    @ProvidePresenter
    fun providePresenter(): OrganizationsPresenter = presenterProvider.get().apply {
        filter = OrganizationsFragmentArgs.fromBundle(arguments!!).filter
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
        recyclerView.apply {
            adapter = this@OrganizationsFragment.adapter
        }
        swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
    }

    override fun setNoFilterHeader() {
        headGroup.update(listOf(
                ScreenLabelItem(getString(R.string.tab_organizations_title)),
                OrganizationsHeaderItem(object : OrganizationsHeaderItem.OnFilterClickListener {
                    override fun onFavoritesClick() = presenter.onFavoritesClick()
                })
        ))
    }

    override fun setFavoritesHeader() {
        headGroup.update(listOf(
                ScreenLabelItem(getString(R.string.organizations_favorites))
        ))
    }

    override fun setOrganizations(organizations: List<Organization?>) {
        organizationSection.update(organizations.map {
            if (it == null) PlaceholderItem(PlaceholderItem.Type.ORGANIZATION)
            else OrganizationItem(
                    it,
                    { presenter.onOrganizationClick(it) },
                    { presenter.onRemoveFromFavoriteClick(it) }
            )
        })
        swipeToRefresh.isRefreshing = false
    }

    override fun showNoFilterEmptyListPlaceholder() {
        organizationSection.update(listOf(NoDataItem(getString(R.string.empty_list_placeholder_message))))
        swipeToRefresh.isRefreshing = false
    }

    override fun showFavoritesEmptyListPlaceholder() {
        organizationSection.update(listOf(NoDataItem(
                getString(R.string.empty_list_placeholder_message),
                getString(R.string.organizations_favorites_empty_list_description)
        )))
        swipeToRefresh.isRefreshing = false
    }

    override fun showOrganization(organization: Organization) {
        findNavController().navigate(R.id.organization_fragment, OrganizationFragmentArgs.Builder(organization.id).build().toBundle())
    }

    override fun showFavorites() {
        findNavController().navigate(OrganizationsFragmentDirections.organizationsFragmentToSelf(OrganizationsFilter.FAVORITES))
    }

    override fun layout() = R.layout.layout_list
}

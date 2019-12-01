package com.example.ui.organizations.favorites

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Organization
import com.example.holders.OrganizationItem
import com.example.holders.PlaceholderItem
import com.example.holders.TitledSection
import com.example.ui.base.BaseFragment
import com.example.ui.organizations.OrganizationFragmentArgs
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.layout_list.*
import javax.inject.Inject
import javax.inject.Provider

class FavoriteOrganizationsFragment : BaseFragment(), FavoriteOrganizationsContract.View {

    @InjectPresenter
    lateinit var presenter: FavoriteOrganizationsPresenter

    @Inject
    lateinit var presenterProvider: Provider<FavoriteOrganizationsPresenter>

    @ProvidePresenter
    fun providePresenter(): FavoriteOrganizationsPresenter = presenterProvider.get()

    private val organizationSection = TitledSection(-100L)

    private val adapter by lazy {
        PaginationListGroupAdapter<GroupieViewHolder>().apply {
            add(organizationSection)
            setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
                override fun onItemTake(position: Int) {
                    if (position > 0) presenter.onItemTake(position - 1)
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = this@FavoriteOrganizationsFragment.adapter
        }
        swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
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

    override fun showOrganization(organization: Organization) {
        val args = OrganizationFragmentArgs.Builder(organization.id).build().toBundle()
        findNavController().navigate(R.id.organization_fragment, args)
    }

    override fun layout() = R.layout.layout_list
}

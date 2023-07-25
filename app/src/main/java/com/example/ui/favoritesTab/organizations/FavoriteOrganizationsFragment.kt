package com.example.ui.favoritesTab.organizations

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.OrganizationNew
import com.example.databinding.LayoutListBinding
import com.example.extensions.findItemBy
import com.example.extensions.updateItem
import com.example.holders.*
import com.example.ui.base.BaseFragment
import com.example.ui.event.list.recommendations.items.NoEventItem
import com.example.ui.organizations.detail.OrganizationFragmentArgs
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import javax.inject.Inject
import javax.inject.Provider

class FavoriteOrganizationsFragment : BaseFragment<LayoutListBinding>(),
    FavoriteOrganizationsContract.View {

    @InjectPresenter
    lateinit var presenter: FavoriteOrganizationsPresenter

    @Inject
    lateinit var presenterProvider: Provider<FavoriteOrganizationsPresenter>

    @ProvidePresenter
    fun providePresenter(): FavoriteOrganizationsPresenter = presenterProvider.get()

    private val adapter by lazy {
        PaginationListGroupAdapter<GroupieViewHolder>().apply {
            setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
                override fun onItemTake(position: Int) {
                    presenter.onItemTake(position)
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            recyclerView.apply {
                adapter = this@FavoriteOrganizationsFragment.adapter
            }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }
    }


    override fun setOrganizations(organizations: List<OrganizationNew?>) {
        adapter.update(organizations.map {
            if (it == null) PlaceholderItem(PlaceholderItem.Type.ORGANIZATION)
            else OrganizationItem(
                it,
                { presenter.onOrganizationClick(it) },
                { presenter.onRemoveFromFavoriteClick(it) }
            )
        })
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun changeSubscription(organization: OrganizationNew) {
        val idLong = organization.id?.toLong()
        adapter.findItemBy { it: OrganizationItem -> it.id == idLong }?.notifyChanged()
    }

    override fun showFavoritesEmptyListPlaceholder() {
        adapter.updateItem(
            NoEventItem(
                getString(R.string.blank_list_error),
                getString(R.string.organizations_favorites_empty_list_description)
            )
        )
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun showOrganization(organization: OrganizationNew) {
        findNavController().navigate(
            R.id.organization_fragment_new,
            OrganizationFragmentArgs.Builder(organization.id.toString()).build().toBundle()
        )
    }


    override fun layout() = R.layout.layout_list
}

package com.example.ui.organizations.favorites

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Organization
import com.example.holders.OrganizationItem
import com.example.holders.TitledSection
import com.example.ui.base.BaseFragment
import com.example.ui.organizations.OrganizationFragmentArgs
import com.example.util.LayoutListWithPlaceholderUtil
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.layout_list_with_placeholder.*
import javax.inject.Inject
import javax.inject.Provider

class FavoriteOrganizationsFragment : BaseFragment(), FavoriteOrganizationsContract.View {

    @InjectPresenter
    lateinit var presenter: FavoriteOrganizationsPresenter

    @Inject
    lateinit var presenterProvider: Provider<FavoriteOrganizationsPresenter>

    @ProvidePresenter
    fun providePresenter(): FavoriteOrganizationsPresenter = presenterProvider.get()


    private lateinit var placeholderUtil: LayoutListWithPlaceholderUtil

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

            placeholderUtil = LayoutListWithPlaceholderUtil(view).apply {
                doNotShowUntilDataLoad = true
                setMessage(getString(R.string.banned_empty_message))
                setImage(R.drawable.ic_neutral_face)
            }
        }
    }

    override fun setOrganizations(organizations: List<Organization>) {
        organizationSection.update(organizations.map {
            OrganizationItem(
                    it,
                    { presenter.onOrganizationClick(it) },
                    { presenter.onRemoveFromFavoriteClick(it) }
            )
        })
        placeholderUtil.isDataLoad = true
    }

    override fun showOrganization(organization: Organization) {
        val args = OrganizationFragmentArgs.Builder(organization.id).build().toBundle()
        findNavController().navigate(R.id.organization_fragment, args)
    }

    override fun layout() = R.layout.layout_list_with_placeholder
}

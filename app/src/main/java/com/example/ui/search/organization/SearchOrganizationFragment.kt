package com.example.ui.search.organization

import android.annotation.SuppressLint
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Organization
import com.example.data.models.OrganizationNew
import com.example.data.models.SearchFilter
import com.example.extensions.findItemBy
import com.example.holders.OrganizationItem
import com.example.holders.PlaceholderItem
import com.example.ui.search.SearchFragment
import com.xwray.groupie.Group
import kotlinx.android.synthetic.main.layout_filter_organization.view.*
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class SearchOrganizationFragment : SearchFragment<SearchOrganizationPresenter, OrganizationNew, SearchFilter.OrganizationNew>(), SearchOrganizationContract.View {

    @InjectPresenter
    override lateinit var presenter: SearchOrganizationPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchOrganizationPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchOrganizationPresenter = presenterProvider.get()

    override fun showOrganization(organization: OrganizationNew) {
        findNavController().navigate(R.id.organization_fragment, bundleOf("organizationId" to organization.id.toString()))
    }

    override fun changeSubscription(organization: OrganizationNew) {
        val idLong = organization.id
        adapter.findItemBy { it: OrganizationItem -> it.id == idLong }?.notifyChanged()
    }

    override fun createItem(itemData: OrganizationNew?): Group {
        return if (itemData == null) PlaceholderItem(PlaceholderItem.Type.ORGANIZATION)
        else OrganizationItem(
                itemData,
                { presenter.onOrganizationClick(itemData) },
                { presenter.onOrganizationSubscriptionClick(itemData) }
        )
    }

    @SuppressLint("InflateParams")
    override fun createFilterView(filter: SearchFilter.OrganizationNew): View {
        return layoutInflater.inflate(R.layout.layout_filter_organization, null).apply {
            etAddress.apply {
                setTextWithoutSearch(filter.address)
                onTextChanged { filter.address = it.toString() }
            }
            initTextFilter(etOrganizationName, filter.name) { filter.name = it }
            initTextFilter(etInn, filter.inn) { filter.inn = it }
        }
    }

    override fun clearFilterView(filterView: View) {
        filterView.apply {
            etAddress.text = null
            etOrganizationName.text = null
            etInn.text = null
        }
    }
}
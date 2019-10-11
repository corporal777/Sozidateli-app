package com.example.ui.search.organization

import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Organization
import com.example.data.models.SearchFilter
import com.example.holders.OrganizationItem
import com.example.ui.search.SearchFragment
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.layout_filter_organization.view.*
import javax.inject.Inject
import javax.inject.Provider

class SearchOrganizationFragment : SearchFragment<SearchOrganizationPresenter, Organization, SearchFilter.Organization>(), SearchOrganizationContract.View {

    @InjectPresenter
    override lateinit var presenter: SearchOrganizationPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchOrganizationPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchOrganizationPresenter = presenterProvider.get()

    override fun showOrganization(organization: Organization) {
        findNavController().navigate(R.id.organization_fragment, bundleOf("organizationId" to organization.id))
    }

    override fun createItem(itemData: Organization): Item {
        return OrganizationItem(
                itemData,
                { presenter.onOrganizationClick(itemData) }
        )
    }

    override fun createFilterView(filter: SearchFilter.Organization): View {
        return layoutInflater.inflate(R.layout.layout_filter_organization, null).apply {
            initTextFilter(etAddress, filter.address) { filter.address = it }
            initTextFilter(etOrganizationName, filter.name) { filter.name = it }
            initTextFilter(etInn, filter.inn) { filter.inn = it }
            initDropDownView(tvType, resources.getStringArray(R.array.organization_types).toList(), filter.type, { it }, { filter.type = it })
            initBiFilter(tvSubscription, resources.getStringArray(R.array.subscription_status).toList(), filter.subscription) { filter.subscription = it }
        }
    }

    override fun clearFilterView(filterView: View) {
        filterView.apply {
            etAddress.text = null
            etOrganizationName.text = null
            etInn.text = null
            tvType.setText(filterNotChosenVariant)
            tvSubscription.setText(filterNotChosenVariant)
        }
    }
}
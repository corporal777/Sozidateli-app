package com.example.ui.search.organization

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.OrganizationNew
import com.example.data.models.SearchFilter
import com.example.extensions.findItemBy
import com.example.holders.OrganizationItem
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.SearchItemLabel
import com.example.ui.search.SearchFragment
import com.xwray.groupie.Group
import com.xwray.groupie.Section
import kotlinx.android.synthetic.main.layout_filter_organization.view.*
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class SearchOrganizationFragment : SearchFragment<SearchOrganizationPresenter, OrganizationNew/*Organization*/, SearchFilter.OrganizationNew/*Organization*/>(), SearchOrganizationContract.View {

    @InjectPresenter
    override lateinit var presenter: SearchOrganizationPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchOrganizationPresenter>


    @ProvidePresenter
    fun providePresenter(): SearchOrganizationPresenter = presenterProvider.get()

    override fun showOrganization(organization: OrganizationNew/*Organization*/) {
        findNavController().navigate(R.id.organization_fragment, bundleOf("organizationId" to organization.id.toString()))
    }

    override fun changeSubscription(organization: OrganizationNew/*Organization*/) {
        val idLong = organization.id?.toLong()
        adapter.findItemBy { it: OrganizationItem -> it.id == idLong }?.notifyChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCanShowEventAndOrganizations = true
    }
    override fun createItemNew(itemData: List<OrganizationNew?>): Group {
        var label = ""
        var title = ""

        val mSection = Section()
        mSection.update(itemData.map {
            if (it == null) {
                label = ""
                PlaceholderItem(PlaceholderItem.Type.ORGANIZATION)
            } else {
                title = when(itemData.size){
                    1 -> {
                        "организация найдена"
                    }
                    2 -> {
                        "организации найдено"
                    }
                    else -> "организаций найдено"
                }
                label = itemData.size.toString() + " " + title
                OrganizationItem(
                    it,
                    { presenter.onOrganizationClick(it) },
                    { presenter.onOrganizationSubscriptionClick(it) }
                )
            }
        })
        headerSection.update(listOf(SearchItemLabel(label)))
        return mSection
    }

    override fun createItem(itemData: OrganizationNew/*Organization*/?): Group {

        return if (itemData == null) PlaceholderItem(PlaceholderItem.Type.ORGANIZATION)
        else {
            OrganizationItem(
                itemData,
                { presenter.onOrganizationClick(itemData) },
                { presenter.onOrganizationSubscriptionClick(itemData) }
            )
        }
    }

    @SuppressLint("InflateParams")
    override fun createFilterView(filter: SearchFilter.OrganizationNew/*Organization*/): View {
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
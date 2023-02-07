package com.example.ui.search.organization

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.OrganizationNew
import com.example.data.models.SearchFilter
import com.example.databinding.LayoutFilterEventBinding
import com.example.databinding.LayoutFilterOrganizationBinding
import com.example.extensions.findItemBy
import com.example.holders.OrganizationItem
import com.example.holders.PlaceholderItem
import com.example.ui.search.SearchFragment
import com.xwray.groupie.Group
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

    override fun showOrganization(organization: OrganizationNew) {
        findNavController().navigate(R.id.organization_fragment_new, bundleOf("organizationId" to organization.id.toString()))
    }

    override fun changeSubscription(organization: OrganizationNew) {
        val idLong = organization.id?.toLong()
        adapter.findItemBy { it: OrganizationItem -> it.id == idLong }?.notifyChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun createItem(itemData: OrganizationNew?): Group {
        return if (itemData == null) PlaceholderItem(PlaceholderItem.Type.ORGANIZATION)
        else {
            OrganizationItem(
                itemData,
                { presenter.onOrganizationClick(itemData) },
                { presenter.onOrganizationSubscriptionClick(itemData) }
            )
        }
    }

    override fun createFilterView(filter: SearchFilter.OrganizationNew): View {
        return LayoutFilterOrganizationBinding.inflate(LayoutInflater.from(requireContext()), null, false).apply {
            etAddress.apply {
                setTextWithoutSearch(filter.address)
                onTextChanged {
                    filter.address = it.toString()
                }
            }
            initTextFilter(etOrganizationName, filter.name) { filter.name = it }
            initTextFilter(etInn, filter.inn) { filter.inn = it }
        }.root
    }

    override fun clearFilterView(filterView: View) {
        LayoutFilterOrganizationBinding.bind(filterView).apply {
            etAddress.text = null
            etOrganizationName.text = null
            etInn.text = null
        }
    }


}
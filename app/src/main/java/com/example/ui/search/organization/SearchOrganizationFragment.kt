package com.example.ui.search.organization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.app.R
import com.example.data.models.OrganizationNew
import com.example.data.models.SearchFilter
import com.example.app.databinding.LayoutFilterOrganizationSearchBinding
import com.example.extensions.findItemBy
import com.example.holders.OrganizationItem
import com.example.holders.PlaceholderItem
import com.example.ui.search.SearchFragment
import com.example.ui.views.filters.event.EventFiltersBottomSheetDialog
import com.example.ui.views.filters.organization.OrgFiltersBottomSheetDialog
import com.xwray.groupie.Group
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class SearchOrganizationFragment :
    SearchFragment<SearchOrganizationPresenter, SearchFilter.Organization>(),
    SearchOrganizationContract.View {

    @InjectPresenter
    override lateinit var presenter: SearchOrganizationPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchOrganizationPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchOrganizationPresenter = presenterProvider.get()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun changeSubscription(organization: OrganizationNew) {

    }

    override fun showFilter(filter: SearchFilter.Organization) {
        OrgFiltersBottomSheetDialog(requireContext(), filter)
            .setFiltersSelected { presenter.onFiltersApplyClick(it) }
            .show()
    }

    override fun showOrganization(organization: OrganizationNew) {
        findNavController().navigate(
            R.id.organization_fragment_new,
            bundleOf("organizationId" to organization.id.toString())
        )
    }
}
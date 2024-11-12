package com.example.ui.search.organization

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.app.R
import com.example.data.models.OrganizationNew
import com.example.data.models.SearchFilter
import com.example.app.databinding.LayoutListSearchBinding
import com.example.ui.search.SearchFragment
import com.example.ui.views.filters.organization.OrgFiltersBottomSheetDialog
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class SearchOrganizationFragment :
    SearchFragment<SearchOrganizationPresenter, SearchFilter.Organization>(R.layout.layout_list_search),
    SearchOrganizationContract.View {

    @InjectPresenter
    override lateinit var presenter: SearchOrganizationPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchOrganizationPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchOrganizationPresenter = presenterProvider.get()

    private val viewBinding: LayoutListSearchBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun invalidatePagingData() {

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
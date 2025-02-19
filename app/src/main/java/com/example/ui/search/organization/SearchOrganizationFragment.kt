package com.example.ui.search.organization

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.adapters.organization.OrganizationPagingAdapter
import com.example.adapters.organization.OrganizationPagingAdapter.Companion.withLoadStateAdapters
import com.example.adapters.organization.OrganizationPlaceholderAdapter
import com.example.app.R
import com.example.app.databinding.LayoutListSearchBinding
import com.example.data.models.OrganizationNew
import com.example.data.models.SearchFilter
import com.example.extensions.isVisibleAnim
import com.example.ui.organizations.detail.OrganizationFragmentArgs
import com.example.ui.search.SearchFragment
import com.example.ui.views.filters.organization.OrgFiltersBottomSheetDialog
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class SearchOrganizationFragment :
    SearchFragment<LayoutListSearchBinding, SearchOrganizationPresenter>(),
    SearchOrganizationContract.View {

    @InjectPresenter
    override lateinit var presenter: SearchOrganizationPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchOrganizationPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchOrganizationPresenter = presenterProvider.get()


    private val pagingAdapter by lazy(LazyThreadSafetyMode.NONE) {
        OrganizationPagingAdapter(
            { presenter.onOrganizationClick(it) },
            { presenter.onOrganizationSubscriptionClick(it) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            searchList.adapter = pagingAdapter.withLoadStateAdapters(
                OrganizationPlaceholderAdapter(5),
                OrganizationPlaceholderAdapter(1)
            ) { setDataEmpty(it) }
            setDataEmpty(isEmptyData)
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }
    }


    override fun setData(data: PagingData<OrganizationNew>) {
        pagingAdapter.submitData(lifecycle, data)
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun updateOrganization(organization: OrganizationNew) {
        pagingAdapter.updateOrganization(organization)
    }

    override fun showFilter(filter: SearchFilter.Organization) {
        OrgFiltersBottomSheetDialog(requireContext(), filter)
            .setFiltersSelected { presenter.onFiltersApplyClick(it) }
            .show()
    }

    override fun showOrganization(organization: OrganizationNew) {
        val args = OrganizationFragmentArgs.Builder(organization.id.toString()).build().toBundle()
        findNavController().navigate(R.id.organization_fragment_new, args)
    }

    override fun setDataEmpty(show: Boolean) {
        super.setDataEmpty(show)
        mBinding.tvEmptyData.isVisibleAnim = show
    }

    override fun binding() = LayoutListSearchBinding::class.java
    override fun layout() = R.layout.layout_list_search
}
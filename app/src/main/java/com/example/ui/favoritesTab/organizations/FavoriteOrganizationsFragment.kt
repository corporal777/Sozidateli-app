package com.example.ui.favoritesTab.organizations

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adapters.organization.OrganizationPagingAdapter
import com.example.adapters.organization.OrganizationPagingAdapter.Companion.withLoadStateAdapters
import com.example.adapters.organization.OrganizationPlaceholderAdapter
import com.example.app.R
import com.example.app.databinding.LayoutDataListBinding
import com.example.data.models.OrganizationNew
import com.example.extensions.isVisibleAnim
import com.example.ui.base.BaseVBFragment
import com.example.ui.organizations.detail.OrganizationFragmentArgs
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class FavoriteOrganizationsFragment : BaseVBFragment<LayoutDataListBinding>(),
    FavoriteOrganizationsContract.View {

    @InjectPresenter
    lateinit var presenter: FavoriteOrganizationsPresenter

    @Inject
    lateinit var presenterProvider: Provider<FavoriteOrganizationsPresenter>

    @ProvidePresenter
    fun providePresenter(): FavoriteOrganizationsPresenter = presenterProvider.get()


    private val pagingAdapter by lazy(LazyThreadSafetyMode.NONE) {
        OrganizationPagingAdapter(
            { presenter.onOrganizationClick(it) },
            { presenter.onRemoveFromFavoriteClick(it) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            dataListView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = pagingAdapter.withLoadStateAdapters(
                    OrganizationPlaceholderAdapter(5),
                    OrganizationPlaceholderAdapter(1)
                ) { setEmptyDataPlaceholder(it) }
                setEmptyDataPlaceholder(isEmptyData)
            }
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

    override fun setEmptyDataPlaceholder(show: Boolean) {
        super.setEmptyDataPlaceholder(show)
        mBinding.tvEmptyDataTitle.apply {
            isVisibleAnim = show
            text = getString(R.string.blank_list_error)
        }
        mBinding.tvEmptyDataDescription.apply {
            isVisibleAnim = show
            text = getString(R.string.organizations_favorites_empty_list_description)
        }
        mBinding.swipeToRefresh.isRefreshing = false
    }


    override fun showOrganization(organization: OrganizationNew) {
        val args = OrganizationFragmentArgs.Builder(organization.id.toString()).build().toBundle()
        findNavController().navigate(R.id.organization_fragment_new, args)
    }

    override fun binding() = LayoutDataListBinding::class.java
    override fun layout() = R.layout.layout_data_list
}

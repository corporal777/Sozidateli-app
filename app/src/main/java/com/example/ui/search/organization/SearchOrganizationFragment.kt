package com.example.ui.search.organization

import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.adapters.NoFilterArrayAdapter
import com.example.data.models.Organization
import com.example.data.models.SearchFilter
import com.example.holders.OrganizationItem
import com.example.ui.search.SearchFragment
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.layout_filter_organization.view.*
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class SearchOrganizationFragment : SearchFragment<SearchOrganizationPresenter, Organization, SearchFilter.Organization>(), SearchOrganizationContract.View {

    @InjectPresenter
    override lateinit var presenter: SearchOrganizationPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchOrganizationPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchOrganizationPresenter = presenterProvider.get()

    private val filterNotChosenVariant by lazy {
        getString(R.string.search_filters_not_chosen)
    }

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
            etAddress.apply {
                onTextChanged { filter.address = it?.toString() }
                setText(filter.address)
            }

            etOrganizationName.apply {
                onTextChanged { filter.name = it?.toString() }
                setText(filter.name)
            }

            etInn.apply {
                onTextChanged { filter.inn = it?.toString() }
                setText(filter.inn)
            }

            val types = resources.getStringArray(R.array.organization_types)
            tvType.apply {
                keyListener = null
                setAdapter(NoFilterArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, types.plus(filterNotChosenVariant)))
                setText(filter.type ?: filterNotChosenVariant, false)
                setOnItemClickListener { _, _, position, _ ->
                    filter.type = types.getOrNull(position)
                }

                onTextChanged {
                    val type = it.toString()
                    filter.type = if (types.contains(type)) type else null
                }
            }

            val subscriptions = resources.getStringArray(R.array.subscription_status)
            tvSubscription.apply {
                keyListener = null
                setAdapter(NoFilterArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, subscriptions.plus(filterNotChosenVariant)))
                setText(when (filter.subscription) {
                    true -> subscriptions[0]
                    false -> subscriptions[1]
                    else -> filterNotChosenVariant
                }, false)
                onTextChanged {
                    val position = subscriptions.indexOf(it.toString())
                    filter.subscription = when (position) {
                        0 -> true
                        1 -> false
                        else -> null
                    }
                }
            }
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
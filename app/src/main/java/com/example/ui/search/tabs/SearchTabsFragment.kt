package com.example.ui.search.tabs

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.app.R
import com.example.app.databinding.FragmentSearchTabsBinding
import com.example.interfaces.SearchInterfaceProvider
import com.example.ui.search.SearchInterface
import com.example.ui.search.event.SearchEventFragment
import com.example.ui.search.organization.SearchOrganizationFragment
import com.example.ui.search.user.SearchUserFragment
import com.example.util.SearchInput
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import com.example.extensions.onPageChanged
import com.example.ui.base.BaseBindingFragment
import javax.inject.Inject
import javax.inject.Provider

class SearchTabsFragment : BaseBindingFragment(R.layout.fragment_search_tabs),
    SearchTabsContract.View,
    SearchInterfaceProvider {

    @InjectPresenter
    lateinit var presenter: SearchTabsPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchTabsPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchTabsPresenter = presenterProvider.get().apply {
        searchInterface = this@SearchTabsFragment.searchInterface
    }



    private val viewBinding: FragmentSearchTabsBinding by viewBinding()
    private val searchInterface = SearchInterface()

    private val fragments by lazy {
        listOf(
            SearchEventFragment(),
            SearchOrganizationFragment(),
            SearchUserFragment()
        )
    }
    private val pageChangeListener = onPageChanged { position ->
        selectTab(position)
        setupQrScannerButton(position)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.viewPager.apply {
            addOnPageChangeListener(pageChangeListener)
            adapter = object : FragmentStatePagerAdapter(
                childFragmentManager,
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
            ) {
                override fun getItem(position: Int) = fragments[position]
                override fun getCount() = fragments.size
            }

            selectTab(currentItem)
            setupQrScannerButton(currentItem)
        }

        viewBinding.tvCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        viewBinding.etSearch.apply {
            SearchInput(this).apply {
                setOnTextChange {
                    viewBinding.btnClear.isVisible = !it.isNullOrEmpty()
                    presenter.onSearchTextChange(it)
                }
                setOnTextChangeDone {
                    presenter.onSearchTextSubmit(it)
                    hideKeyboard(viewBinding.etSearch)
                }
                setOnFocusChange { hasFocus ->
                    viewBinding.clSearch.setBackgroundResource(
                        if (hasFocus) R.drawable.background_search_field_rounded_focused
                        else R.drawable.background_search_field_rounded_normal
                    )
                }
            }
        }

        viewBinding.apply {
            btnClear.apply {
                isVisible = !etSearch.text.isNullOrEmpty()
                setOnClickListener { viewBinding.etSearch.text = null }
            }

            btnFilter.setOnClickListener { presenter.onFilterClick() }
            cardQrScanner.setOnClickListener { presenter.onScanClick() }
            btnTabEvents.setOnClickListener { viewPager.currentItem = 0 }
            btnTabOrganizations.setOnClickListener { viewPager.currentItem = 1 }
            btnTabUsers.setOnClickListener { viewPager.currentItem = 2 }
        }

    }

    fun setFiltersChosen(isHas: Boolean) {
        viewBinding.btnFilter.apply {
            if (isHas) setImageResource(R.drawable.ic_filters_selected)
            else setImageResource(R.drawable.ic_filters_new)
        }
    }

    fun showEmptyDataText(show: Boolean, title: String) {
        viewBinding.apply {
            tvEmptyData.isVisible = show
            tvEmptyData.text = title
        }
    }

    private fun selectTab(position: Int) {
        viewBinding.clTabs.apply {
            for (p in 0 until childCount) {
                getChildAt(p).isSelected = p == position
            }
        }
    }


    private fun setupQrScannerButton(position: Int) {
        viewBinding.cardQrScanner.isVisible = position == 0
    }

    override fun provideSearchInterface(): SearchInterface {
        return searchInterface
    }

    override fun showQrScanner() {
        findNavController().navigate(R.id.qr_scanner_fragment)
    }
}

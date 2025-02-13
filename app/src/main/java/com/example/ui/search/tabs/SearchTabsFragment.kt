package com.example.ui.search.tabs

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.adapters.PagerStateAdapter
import com.example.app.R
import com.example.app.databinding.FragmentSearchTabsBinding
import com.example.extensions.onPageChanged
import com.example.interfaces.SearchInterfaceProvider
import com.example.ui.base.BaseVBFragment
import com.example.ui.search.SearchInterface
import com.example.ui.search.event.SearchEventFragment
import com.example.ui.search.organization.SearchOrganizationFragment
import com.example.ui.search.user.SearchUserFragment
import com.example.util.SearchInput
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class SearchTabsFragment : BaseVBFragment<FragmentSearchTabsBinding>(), SearchTabsContract.View,
    SearchInterfaceProvider {


    @InjectPresenter
    lateinit var presenter: SearchTabsPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchTabsPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchTabsPresenter = presenterProvider.get().apply {
        searchInterface = this@SearchTabsFragment.searchInterface
    }


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
        mBinding.viewPager.run {
            adapter = object : PagerStateAdapter(childFragmentManager){
                override fun getItem(position: Int) = fragments[position]
                override fun getCount() = fragments.size
            }
            addOnPageChangeListener(pageChangeListener)
            selectTab(currentItem)
            setupQrScannerButton(currentItem)
        }

        mBinding.tvCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        mBinding.etSearch.apply {
            SearchInput(this).apply {
                setOnTextChange {
                    mBinding.btnClear.isVisible = !it.isNullOrEmpty()
                    presenter.onSearchTextChange(it)
                }
                setOnTextChangeDone {
                    presenter.onSearchTextSubmit(it)
                    hideKeyboard(mBinding.etSearch)
                }
                setOnFocusChange { hasFocus ->
                    mBinding.clSearch.setBackgroundResource(
                        if (hasFocus) R.drawable.background_search_field_rounded_focused
                        else R.drawable.background_search_field_rounded_normal
                    )
                }
            }
        }

        mBinding.apply {
            btnClear.apply {
                isVisible = !etSearch.text.isNullOrEmpty()
                setOnClickListener { mBinding.etSearch.text = null }
            }

            btnFilter.setOnClickListener { presenter.onFilterClick() }
            cardQrScanner.setOnClickListener {
                presenter.onScanClick()
            }
            btnTabEvents.setOnClickListener { viewPager.currentItem = 0 }
            btnTabOrganizations.setOnClickListener { viewPager.currentItem = 1 }
            btnTabUsers.setOnClickListener { viewPager.currentItem = 2 }
        }

    }

    fun setFiltersChosen(isHas : Boolean){
        mBinding.btnFilter.apply {
            if (isHas) setImageResource(R.drawable.ic_filters_selected)
            else setImageResource(R.drawable.ic_filters_new)
        }
    }

    private fun selectTab(position: Int) {
        mBinding.clTabs.apply {
            for (p in 0 until childCount) {
                getChildAt(p).isSelected = p == position
            }
        }
    }


    private fun setupQrScannerButton(position: Int) {
        mBinding.cardQrScanner.isVisible = position == 0
    }

    override fun provideSearchInterface(): SearchInterface {
        return searchInterface
    }

    override fun showQrScanner() {
        findNavController().navigate(R.id.qr_scanner_fragment)
    }

    override fun binding() = FragmentSearchTabsBinding::class.java
    override fun layout() = R.layout.fragment_search_tabs
}

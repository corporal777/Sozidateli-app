package com.example.ui.search.tabs

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.app.R
import com.example.app.databinding.FragmentSearchTabsBinding
import com.example.extensions.onFocusChanged
import com.example.interfaces.SearchInterfaceProvider
import com.example.ui.search.SearchInterface
import com.example.ui.search.event.SearchEventFragment
import com.example.ui.search.organization.SearchOrganizationFragment
import com.example.ui.search.user.SearchUserFragment
import com.example.util.SearchInput
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import com.example.extensions.onPageSelected
import com.example.extensions.setFiltersBackground
import com.example.ui.base.BaseVBFragment
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
    private val pageChangeListener = onPageSelected { position -> selectTab(position) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.viewPager.apply {
            registerOnPageChangeCallback(pageChangeListener)
            adapter = object : FragmentStateAdapter(this@SearchTabsFragment) {
                override fun getItemCount(): Int = fragments.size
                override fun createFragment(position: Int): Fragment = fragments[position]
            }
            selectTab(currentItem)
        }

        mBinding.etSearch.apply {
            SearchInput(this).apply {
                setOnAfterTextChange {
                    mBinding.btnClear.isVisible = it.isNotEmpty()
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
            tvCancel.setOnClickListener { findNavController().navigateUp() }
            btnFilter.setOnClickListener { presenter.onFilterClick() }
            btnTabEvents.setOnClickListener { viewPager.currentItem = 0 }
            btnTabOrganizations.setOnClickListener { viewPager.currentItem = 1 }
            btnTabUsers.setOnClickListener { viewPager.currentItem = 2 }
        }

    }

    fun setFiltersChosen(isHas: Boolean) = mBinding.btnFilter.setFiltersBackground(isHas)

    private fun selectTab(position: Int) {
        mBinding.clTabs.run {
            for (p in 0 until childCount) getChildAt(p).isSelected = p == position
        }
    }

    override fun provideSearchInterface(): SearchInterface = searchInterface

    override fun binding() = FragmentSearchTabsBinding::class.java
    override fun layout() = R.layout.fragment_search_tabs
}

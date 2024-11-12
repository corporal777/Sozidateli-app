package com.example.ui.search.tabs

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
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
import com.example.extensions.onPageSelected
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
    private val pageChangeListener = onPageSelected { position -> selectTab(position) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.viewPager.apply {
            registerOnPageChangeCallback(pageChangeListener)
            adapter = object : FragmentStateAdapter(this@SearchTabsFragment) {
                override fun getItemCount(): Int = fragments.size
                override fun createFragment(position: Int): Fragment = fragments[position]
            }
            selectTab(currentItem)
        }

        viewBinding.tvCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        viewBinding.etSearch.apply {
            SearchInput(this).apply {
                setOnTextChange {
                    viewBinding.btnClear.isVisible = it.isNotEmpty()
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
        try {
            viewBinding.apply {
                tvEmptyData.isVisible = show
                tvEmptyData.text = title
            }
        }catch (e : Exception){
            e.printStackTrace()
        }

    }

    private fun selectTab(position: Int) {
        viewBinding.clTabs.run {
            for (p in 0 until childCount) getChildAt(p).isSelected = p == position
        }
    }

    override fun provideSearchInterface(): SearchInterface = searchInterface
}

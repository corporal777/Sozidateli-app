package com.example.ui.search.tabs

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.interfaces.SearchInterfaceProvider
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.search.SearchInterface
import com.example.ui.search.event.SearchEventFragment
import com.example.ui.search.organization.SearchOrganizationFragment
import com.example.ui.search.user.SearchUserFragment
import com.example.ui.views.toolbar.ToolbarButton
import com.example.ui.views.toolbar.ToolbarContentActionBar
import com.example.util.SearchInput
import kotlinx.android.synthetic.main.fragment_search_tabs.*
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class SearchTabsFragment : BaseFragment(), SearchTabsContract.View, ToolbarFragment, SearchInterfaceProvider {

    override val title = ""

    @InjectPresenter
    lateinit var presenter: SearchTabsPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchTabsPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchTabsPresenter = presenterProvider.get().apply {
        searchInterface = this@SearchTabsFragment.searchInterface.apply {
            initWithFilter = SearchTabsFragmentArgs.fromBundle(arguments!!).filter
        }
    }

    private val searchInterface = SearchInterface()

    private val fragments by lazy {
        listOf(
                SearchEventFragment(),
                SearchOrganizationFragment(),
                SearchUserFragment()
        )
    }

    private val pageChangeListener = object : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            selectTab(position)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.run {
            addOnPageChangeListener(pageChangeListener)
            adapter = object : FragmentStatePagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
                override fun getItem(position: Int) = fragments[position]

                override fun getCount() = fragments.size
            }
            selectTab(currentItem)
        }

        etSearch.apply {
            SearchInput(this).apply {
                setOnTextChange { presenter.onSearchTextChange(it) }
                setOnTextChangeDone {
                    presenter.onSearchTextSubmit(it)
                    hideKeyboard(etSearch)
                }
            }

            onTextChanged {
                btnClear.isVisible = !it.isNullOrEmpty()
            }

            onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                clSearch.setBackgroundResource(
                        if (hasFocus) R.drawable.background_input_focused
                        else R.drawable.background_input_normal
                )
            }
        }

        btnClear.apply {
            btnClear.isVisible = !etSearch.text.isNullOrEmpty()
            setOnClickListener { etSearch.text = null }
        }

        btnFilter.setOnClickListener { presenter.onFilterClick() }

        btnTabEvents.setOnClickListener { viewPager.currentItem = 0 }
        btnTabOrganizations.setOnClickListener { viewPager.currentItem = 1 }
        btnTabUsers.setOnClickListener { viewPager.currentItem = 2 }
    }

    private fun selectTab(position: Int) {
        clTabs.apply {
            for (p in 0 until childCount) {
                getChildAt(p).isSelected = p == position
            }
        }
    }

    override fun setupToolbarContent(toolbarContentActionBar: ToolbarContentActionBar) {
        super.setupToolbarContent(toolbarContentActionBar)
        toolbarContentActionBar.apply {
            addRightView(ToolbarButton(requireContext()).apply {
                setImageResource(R.drawable.ic_scan)
                setOnClickListener { presenter.onScanClick() }
            })
        }
    }

    override fun provideSearchInterface(): SearchInterface {
        return searchInterface
    }

    override fun showQrScanner() {
        findNavController().navigate(SearchTabsFragmentDirections.searchTabsToQrScanner())
    }

    override fun layout() = R.layout.fragment_search_tabs
}

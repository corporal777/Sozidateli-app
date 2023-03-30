package com.example.ui.search.tabs

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.databinding.FragmentSearchTabsBinding
import com.example.interfaces.SearchInterfaceProvider
import com.example.ui.base.BaseFragment
import com.example.ui.base.BaseFragmentNew
import com.example.ui.search.SearchInterface
import com.example.ui.search.event.SearchEventFragment
import com.example.ui.search.organization.SearchOrganizationFragment
import com.example.ui.search.user.SearchUserFragment
import com.example.ui.views.toolbar.ToolbarButton
import com.example.ui.views.toolbar.ToolbarContentActionBar
import com.example.util.SearchInput
import onPageChanged
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class SearchTabsFragment : BaseFragmentNew<FragmentSearchTabsBinding>(), SearchTabsContract.View,
    SearchInterfaceProvider {


    @InjectPresenter
    lateinit var presenter: SearchTabsPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchTabsPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchTabsPresenter = presenterProvider.get().apply {
        searchInterface = this@SearchTabsFragment.searchInterface.apply {
            initWithFilter = SearchTabsFragmentArgs.fromBundle(requireArguments()).filter
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
    private val pageChangeListener = onPageChanged { position ->
        presenter.currentPosition = position
        selectTab(position)
        setupQrScannerButton(position)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.viewPager.run {
            isSaveEnabled = false
            addOnPageChangeListener(pageChangeListener)
            adapter = object : FragmentStatePagerAdapter(
                childFragmentManager,
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
            ) {
                override fun getItem(position: Int) = fragments[position] as Fragment

                override fun getCount() = fragments.size
            }
            selectTab(currentItem)
            setupQrScannerButton(currentItem)
        }

        mBinding.tvCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        mBinding.etSearch.apply {
            SearchInput(this).apply {
                setOnTextChange { presenter.onSearchTextChange(it) }
                setOnTextChangeDone {
                    presenter.onSearchTextSubmit(it)
                    hideKeyboard(mBinding.etSearch)
                }
            }

            onTextChanged {
                mBinding.btnClear.isVisible = !it.isNullOrEmpty()
            }

            onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                mBinding.clSearch.setBackgroundResource(
                    if (hasFocus) R.drawable.background_search_field_rounded_focused
                    else R.drawable.background_search_field_rounded_normal
                )
            }
        }

        mBinding.apply {
            btnClear.apply {
                btnClear.isVisible = !etSearch.text.isNullOrEmpty()
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

    override fun setCurrentFragment(position: Int) {
        mBinding.viewPager.apply {
            if (currentItem == position) {
                return
            } else {
                currentItem = position
                selectTab(position)
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
        findNavController().navigate(SearchTabsFragmentDirections.searchTabsToQrScanner())
    }

    override fun layout() = R.layout.fragment_search_tabs
}

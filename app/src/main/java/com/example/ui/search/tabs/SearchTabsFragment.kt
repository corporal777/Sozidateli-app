package com.example.ui.search.tabs

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.adapters.TabsFragmentAdapter
import com.example.interfaces.SearchInterfaceProvider
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.search.SearchInterface
import com.example.ui.search.event.SearchEventFragment
import com.example.ui.search.organization.SearchOrganizationFragment
import com.example.ui.search.user.SearchUserFragment
import com.example.ui.views.toolbar.ToolbarContentActionBar
import com.example.util.SearchInput
import kotlinx.android.synthetic.main.fragment_search_tabs.*
import javax.inject.Inject
import javax.inject.Provider

class SearchTabsFragment : BaseFragment(), SearchTabsContract.View, ToolbarFragment, SearchInterfaceProvider {

    override val title: String
        get() = getString(R.string.search_label)

    @InjectPresenter
    lateinit var presenter: SearchTabsPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchTabsPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchTabsPresenter = presenterProvider.get().apply {
        searchInterface = this@SearchTabsFragment.searchInterface
    }

    private val searchInterface = SearchInterface()

    private val pages by lazy {
        listOf(
                SearchOrganizationFragment() to getString(R.string.search_events),
                SearchEventFragment() to getString(R.string.search_organizations),
                SearchUserFragment() to getString(R.string.search_users)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.run {
            adapter = TabsFragmentAdapter(pages, childFragmentManager)
            tabLayout.setupWithViewPager(this)
        }

        etSearch.apply {
            SearchInput(this).apply {
                setOnTextChange { presenter.onSearchTextChange(it) }
                setOnTextChangeDone { presenter.onSearchTextSubmit(it) }
            }
        }

        tilSearch.apply {
            setEndIconOnClickListener { presenter.onFilterClick() }
        }
    }

    override fun setupToolbarContent(toolbarContentActionBar: ToolbarContentActionBar) {
        super.setupToolbarContent(toolbarContentActionBar)
        val imageSize = resources.getDimensionPixelSize(R.dimen.toolbar_content_button_size)
        toolbarContentActionBar.apply {
            addRightView(AppCompatImageView(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(imageSize, imageSize)
                setImageResource(R.drawable.ic_scan)
                setOnClickListener { }
            })
        }
    }

    override fun provideSearchInterface(): SearchInterface {
        return searchInterface
    }

    override fun layout() = R.layout.fragment_search_tabs
}

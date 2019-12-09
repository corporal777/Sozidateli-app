package com.example.ui.contactsSearch

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.user.User
import com.example.holders.ListSectionNameItem
import com.example.holders.PlaceholderItem
import com.example.holders.UserItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.util.PositionOffsetScrollListener
import com.example.util.SearchInput
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_search_tabs.*
import kotlinx.android.synthetic.main.layout_list.*
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class ContactsSearchFragment : BaseFragment(), ContactsSearchContract.View, ToolbarFragment {

    override val title: String
        get() = getString(R.string.contact_search_title)

    @InjectPresenter
    lateinit var presenter: ContactsSearchPresenter

    @Inject
    lateinit var presenterProvider: Provider<ContactsSearchPresenter>

    @ProvidePresenter
    fun providePresenter(): ContactsSearchPresenter = presenterProvider.get().apply {
        val args = arguments?.let { ContactsSearchFragmentArgs.fromBundle(it) }
        startAction = args?.searchAction ?: SEARCH_ACTION_NONE
    }

    private val favoritesHeader by lazy {
        ListSectionNameItem(-100L, getString(R.string.search_contact_section_favorites))
    }
    private val favoritesSection by lazy {
        Section().apply {
            setHeader(favoritesHeader)
            setHideWhenEmpty(true)
        }
    }

    private val chatsHeader by lazy {
        ListSectionNameItem(-200L, getString(R.string.search_contact_section_chats))
    }
    private val chatsSection by lazy {
        Section().apply {
            setHeader(chatsHeader)
            setHideWhenEmpty(true)
        }
    }

    private val anotherHeader by lazy {
        ListSectionNameItem(-300L, getString(R.string.search_contact_section_another))
    }
    private val anotherSection by lazy {
        Section().apply {
            setHeader(anotherHeader)
            setHideWhenEmpty(true)
        }
    }

    private val placeholderSection by lazy {
        Section()
    }

    private val adapter by lazy {
        PaginationListGroupAdapter<GroupieViewHolder>().apply {
            setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
                override fun onItemTake(position: Int) {
                    if (placeholderSection.itemCount == 0) presenter.onItemTake(findItemPositionWithoutHeaders(position))
                }
            })
            add(favoritesSection)
            add(chatsSection)
            add(anotherSection)
            add(placeholderSection)
        }
    }

    private lateinit var searchInput: SearchInput

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        recyclerView.apply {
            adapter = this@ContactsSearchFragment.adapter
            addOnScrollListener(PositionOffsetScrollListener { position, offset ->
                presenter.onScrollChange(position, offset)
            })

            adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    if (positionStart == 0) {
                        layoutManager?.scrollToPosition(0)
                    }
                }
            })
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            itemAnimator = null
        }

        etSearch.apply {
            searchInput = SearchInput(this).apply {
                setOnTextChange { presenter.onQueryTextChange(it) }
                setOnTextChangeDone {
                    presenter.onQueryTextSubmit(it)
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

        swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
    }

    private fun findItemPositionWithoutHeaders(position: Int): Int {
        var positionWithoutHeaders = position
        if (favoritesSection.itemCount > 0
                && positionWithoutHeaders > adapter.getAdapterPosition(favoritesSection)) --positionWithoutHeaders
        if (chatsSection.itemCount > 0
                && positionWithoutHeaders > adapter.getAdapterPosition(chatsSection)) --positionWithoutHeaders
        if (anotherSection.itemCount > 0
                && positionWithoutHeaders > adapter.getAdapterPosition(anotherSection)) --positionWithoutHeaders
        return positionWithoutHeaders
    }

    override fun openUserInfo(userId: String) {
        findNavController().navigate(ContactsSearchFragmentDirections.openUser(userId))
    }

    override fun setItems(favorites: List<User>, chats: List<User>, another: List<User>) {
        placeholderSection.update(emptyList())
        val mapToItem = { user: User -> UserItem(user.user_id, user.fullName, null, user.user_avatar, { presenter.onUserClick(user) }) }
        favoritesSection.update(favorites.map(mapToItem))
        chatsHeader.withTopMargin = favorites.isNotEmpty()
        chatsSection.update(chats.map(mapToItem))
        anotherHeader.withTopMargin = chats.isNotEmpty()
        anotherSection.update(another.map(mapToItem))
        swipeToRefresh.isRefreshing = false
    }

    override fun setPlaceholders(size: Int) {
        favoritesSection.update(emptyList())
        chatsSection.update(emptyList())
        anotherSection.update(emptyList())
        placeholderSection.update(List(size) { PlaceholderItem(PlaceholderItem.Type.USER) })
    }

    override fun showNeedMoreSymbols(symbolsLimit: Int) {
        Toast.makeText(requireContext(), getString(R.string.contacts_need_more_symbols_message, symbolsLimit), Toast.LENGTH_SHORT).show()
    }

    override fun scrollToPositionWithOffset(position: Int, offset: Int) {
        (recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).scrollToPositionWithOffset(position, offset)
    }

    override fun focusOnInput(showKeyboard: Boolean) {
        searchInput.view.apply {
            post {
                showSoftInputOnFocus = showKeyboard
                requestFocus()
                showSoftInputOnFocus = true
                if (showKeyboard) showKeyboard(this)
            }
        }
    }

    override fun showFilter() {

    }

    override fun hideFilter() {

    }

    override fun layout() = R.layout.fragment_contacts_search

    companion object {
        const val SEARCH_ACTION_NONE = 0
        const val SEARCH_ACTION_INPUT = 1
        const val SEARCH_ACTION_FILTER = 2
    }
}

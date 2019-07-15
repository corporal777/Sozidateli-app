package com.example.ui.contactsSearch

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.user.User
import com.example.holders.ListSectionNameItem
import com.example.holders.UserItem
import com.example.ui.base.BaseFragment
import com.example.util.LayoutListWithPlaceholderUtil
import com.example.util.PositionOffsetScrollListener
import com.example.util.SearchInput
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.layout_list_with_placeholder.*
import javax.inject.Inject
import javax.inject.Provider

class ContactsSearchFragment : BaseFragment(), ContactsSearchContract.View {

    @InjectPresenter
    lateinit var presenter: ContactsSearchPresenter

    @Inject
    lateinit var presenterProvider: Provider<ContactsSearchPresenter>

    @ProvidePresenter
    fun providePresenter(): ContactsSearchPresenter = presenterProvider.get().apply {
        val args = arguments?.let { ContactsSearchFragmentArgs.fromBundle(it) }
        startAction = args?.searchAction ?: SEARCH_ACTION_NONE
    }

    private lateinit var placeholderUtil: LayoutListWithPlaceholderUtil

    private val favoritesSection by lazy {
        Section().apply {
            setHeader(ListSectionNameItem(-100L, getString(R.string.search_contact_section_favorites)))
            setHideWhenEmpty(true)
        }
    }
    private val chatsSection by lazy {
        Section().apply {
            setHeader(ListSectionNameItem(-200L, getString(R.string.search_contact_section_chats)))
            setHideWhenEmpty(true)
        }
    }
    private val anotherSection by lazy {
        Section().apply {
            setHeader(ListSectionNameItem(-300L, getString(R.string.search_contact_section_another)))
            setHideWhenEmpty(true)
        }
    }

    private val adapter by lazy {
        PaginationListGroupAdapter<ViewHolder>().apply {
            setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
                override fun onItemTake(position: Int) {
                    presenter.onItemTake(findItemPositionWithoutHeaders(position))
                }
            })
            add(favoritesSection)
            add(chatsSection)
            add(anotherSection)
        }
    }

    private lateinit var searchInput: SearchInput

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        recyclerView.apply {
            adapter = this@ContactsSearchFragment.adapter
            if (itemDecorationCount == 0) addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(context, VERTICAL))
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

        searchInput = SearchInput(view.findViewById(R.id.search) as EditText).apply {
            setOnTextChange { presenter.onQueryTextChange(it) }
            setOnTextChangeDone { presenter.onQueryTextSubmit(it) }
        }

        placeholderUtil = LayoutListWithPlaceholderUtil(view).apply {
            doNotShowUntilDataLoad = true
            setMessage(getString(R.string.search_empty_list))
            setImage(R.drawable.ic_neutral_face)
        }
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
        val mapToItem = { user: User -> UserItem(user.user_id, user.fullName, user.user_avatar) { presenter.onUserClick(user) } }
        favoritesSection.update(favorites.map(mapToItem))
        chatsSection.update(chats.map(mapToItem))
        anotherSection.update(another.map(mapToItem))
        placeholderUtil.isDataLoad = true
    }

    override fun scrollToPositionWithOffset(position: Int, offset: Int) {
        (recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).scrollToPositionWithOffset(position, offset)
    }

    override fun focusOnInput() {
        searchInput.requestFocus()
    }

    override fun showFilter() {

    }

    override fun hideFilter() {

    }

    override fun isShowToolbar() = true
    override fun layout() = R.layout.fragment_contacts_search

    companion object {
        const val SEARCH_ACTION_NONE = 0
        const val SEARCH_ACTION_INPUT = 1
        const val SEARCH_ACTION_FILTER = 2
    }
}

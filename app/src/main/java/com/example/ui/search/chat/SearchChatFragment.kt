package com.example.ui.search.chat

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserDetail
import com.example.data.models.user.User
import com.example.holders.ListSectionNameItem
import com.example.holders.NoDataItem
import com.example.holders.PlaceholderItem
import com.example.holders.UserItem
import com.example.interfaces.SearchInterfaceProvider
import com.example.interfaces.ToolbarFragment
import com.example.ui.search.SearchInterface
import com.example.ui.search.user.AbstractSearchUserFragment
import com.example.util.SearchInput
import com.xwray.groupie.Group
import com.xwray.groupie.Section
import kotlinx.android.synthetic.main.fragment_search_tabs.*
import kotlinx.android.synthetic.main.layout_list.*
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class SearchChatFragment : AbstractSearchUserFragment<SearchChatPresenter>(), SearchChatContract.View, ToolbarFragment, SearchInterfaceProvider {

    override val title: String
        get() = getString(R.string.contact_search_title)

    @InjectPresenter
    override lateinit var presenter: SearchChatPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchChatPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchChatPresenter = presenterProvider.get()

    private val searchInterface = SearchInterface()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etSearch.apply {
            SearchInput(this).apply {
                setOnTextChange { onSearchTextSubmit(it) }
                setOnTextChangeDone {
                    onSearchTextSubmit(it)
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

        btnFilter.setOnClickListener { searchInterface.showFilterCallback?.invoke() }
    }

    override fun setData(data: List<UserDetail?>) {
        super.setData(data)
        favoritesSection.clear()
        chatsSection.clear()
        anotherSection.clear()
    }

    override fun createItem(itemData: UserDetail?): Group {
        return if (itemData == null) PlaceholderItem(PlaceholderItem.Type.USER)
        else UserItem(itemData.id, itemData.fullName, null, itemData.image?.uri, { presenter.onUserClick(itemData) })
    }

    override fun setItems(favorites: List<UserDetail>, chats: List<UserDetail>, another: List<UserDetail>) {
        if (favorites.isEmpty() && chats.isEmpty() && another.isEmpty()){
            adapter.update(listOf(NoDataItem(getString(R.string.schedule_my_empty_day_placeholder_title), getString(R.string.search_no_data_description))))
        } else {
            favoritesSection.update(favorites.map(::createItem))
            chatsHeader.withTopMargin = favorites.isNotEmpty()
            chatsSection.update(chats.map(::createItem))
            anotherHeader.withTopMargin = chats.isNotEmpty()
            anotherSection.update(another.map(::createItem))
            adapter.update(listOf(favoritesSection, chatsSection, anotherSection))
        }

        swipeToRefresh.isRefreshing = false
    }

    private fun onSearchTextSubmit(text: String) {
        searchInterface.apply {
            searchText = text
            searchTextCallback?.invoke()
        }
    }

    override fun provideSearchInterface(): SearchInterface = searchInterface

    override fun layout() = R.layout.fragment_chat_search
}
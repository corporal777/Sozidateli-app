package com.example.ui.contactsSearch

import androidx.paging.PagedList
import android.graphics.Typeface
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.SearchView
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.adapters.SimplePagingRecyclerViewAdapter
import com.example.adapters.ViewHolder
import com.example.data.models.ContactSearch
import com.example.ui.base.BaseFragment
import com.example.util.CropCircleTransformation
import com.example.util.PositionOffsetScrollListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_events_list.*
import kotlinx.android.synthetic.main.item_search_contact.*
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan
import uk.co.chrisjenx.calligraphy.TypefaceUtils
import javax.inject.Inject
import javax.inject.Provider


class ContactsSearchFragment : BaseFragment(), ContactsSearchContract.View {

    @InjectPresenter
    lateinit var presenter: ContactsSearchPresenter

    @Inject
    lateinit var presenterProvider: Provider<ContactsSearchPresenter>

    @ProvidePresenter
    fun providePresenter(): ContactsSearchPresenter = presenterProvider.get()


    private lateinit var typefaceBold:CalligraphyTypefaceSpan

    private val adapter: SimplePagingRecyclerViewAdapter<ContactSearch> by lazy {
        object : SimplePagingRecyclerViewAdapter<ContactSearch>(
                { oldItem, newItem -> oldItem.user.id == newItem.user.id },
                { oldItem, newItem -> oldItem == newItem }
        ) {
            override fun getItemLayout(itemView: Int) = R.layout.item_search_contact

            override fun onBindItem(viewHolder: ViewHolder, item: ContactSearch?, position: Int) {
                item!!
                viewHolder.apply {
                    Picasso.get().load(item.user.image).transform(CropCircleTransformation()).into(ivUserAvatar)
                    tvUserName.text = makeSectionOfTextBold(item.user.name, item.searchText)

                    val showTitle = position == 0 || getItem(position - 1)?.contactType != item.contactType

                    val visibility: Int
                    val textRes: Int?
                    if (showTitle) {
                        visibility = View.VISIBLE
                        textRes = when (item.contactType) {
                            ContactSearch.Type.FAVORITE -> R.string.contacts_search_favorites
                            ContactSearch.Type.CHAT -> R.string.contacts_search_chats
                            ContactSearch.Type.CONTACT -> R.string.contacts_search_another
                        }
                    } else {
                        visibility = View.GONE
                        textRes = null
                    }

                    tvContactType.apply {
                        this.visibility = visibility
                        text = textRes?.let { getString(it) }
                    }

                    typeDivider.apply { this.visibility = visibility }
                }
            }

            private fun makeSectionOfTextBold(text: String, textToBold: String): CharSequence {
                return SpannableStringBuilder(text).apply {
                    if (textToBold.isBlank()) return@apply

                    var start = text.indexOf(string = textToBold, ignoreCase = true)
                    while (start > 0) {
                        val end = start + textToBold.length
                        setSpan(typefaceBold, start, end, 0)
                        start = text.indexOf(textToBold, start + 1, true)
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        typefaceBold = CalligraphyTypefaceSpan(uk.co.chrisjenx.calligraphy.TypefaceUtils.load(context!!.assets,"fonts/OpenSans-Bold.ttf"))
        recyclerView.apply {
            adapter = this@ContactsSearchFragment.adapter
            if (itemDecorationCount == 0) addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(context, VERTICAL))
            addOnScrollListener(PositionOffsetScrollListener { position, offset ->
                presenter.onScrollChange(position, offset)
            })
        }
    }

    override fun setData(contactSearch: PagedList<ContactSearch>) {
        adapter.submitList(contactSearch)
    }

    override fun scrollToPositionWithOffset(position: Int, offset: Int) {
        (recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).scrollToPositionWithOffset(position, offset)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_chat_search, menu)

        val searchItem = menu.findItem(R.id.action_search)?.apply {
            expandActionView()
            setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem?) = true

                override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                    presenter.onSearchCollapsed()
                    return true
                }
            })
        }
        val searchView = searchItem?.actionView as SearchView?
        searchView?.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    p0?.let { presenter.onQueryTextSubmit(it) }
                    return false
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    p0?.let { presenter.onQueryTextChange(it) }
                    return false
                }
            })
        }
    }

    override fun isShowToolbar() = true
    override fun layout() = R.layout.fragment_contacts_search
}

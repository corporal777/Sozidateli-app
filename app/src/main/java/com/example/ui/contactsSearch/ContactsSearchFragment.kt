package com.example.ui.contactsSearch

import android.content.Context
import androidx.paging.PagedList
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.SearchView
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.adapters.SimplePagingRecyclerViewAdapter
import com.example.adapters.ViewHolder
import com.example.data.models.ChatStartResponse
import com.example.data.models.ContactSearch
import com.example.data.models.user.User
import com.example.ui.base.BaseFragment
import com.example.ui.chatList.ChatListFragmentDirections
import com.example.util.CropCircleTransformation
import com.example.util.PositionOffsetScrollListener
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_contacts_search.*
import kotlinx.android.synthetic.main.item_search_contact.*
import timber.log.Timber
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


    private lateinit var typefaceBold: CalligraphyTypefaceSpan

    private val adapter: SimplePagingRecyclerViewAdapter<User> by lazy {
        object : SimplePagingRecyclerViewAdapter<User>(
                { oldItem, newItem -> oldItem.user_id == newItem.user_id },
                { oldItem, newItem -> oldItem == newItem }
        ) {
            override fun getItemLayout(itemView: Int) = R.layout.item_search_contact

            override fun onBindItem(viewHolder: ViewHolder, item: User?, position: Int) {
                item!!
                viewHolder.apply {
                        Picasso.get().load(item.user_avatar.let { if(it.isNullOrEmpty()) null else it })
                                .placeholder(R.drawable.avatar_placeholder).networkPolicy(NetworkPolicy.NO_CACHE)
                                .transform(CropCircleTransformation()).into(ivUserAvatar)

                    tvUserName.text = makeSectionOfTextBold(item.fullName, this@ContactsSearchFragment.etSearchText.text.toString())

                    val showTitle = false /*position == 0 || getItem(position - 1)?.contactType != item.contactType*/

                    val visibility: Int
                    val textRes: Int?
                    if (showTitle) {
                        visibility = View.VISIBLE
                        /*  textRes = when (item.contactType) {
                              ContactSearch.Type.FAVORITE -> R.string.contacts_search_favorites
                              ContactSearch.Type.CHAT -> R.string.contacts_search_chats
                              ContactSearch.Type.CONTACT -> R.string.contacts_search_another
                          }*/
                    } else {
                        visibility = View.GONE
                        textRes = null
                    }

                    tvContactType.apply {
                        this.visibility = visibility
                        //text = textRes?.let { getString(it) }
                    }

                    typeDivider.apply { this.visibility = visibility }

                    itemView.setOnClickListener { presenter.onUserClick(item) }
                }
            }

            private fun makeSectionOfTextBold(text: String, textToBold: String): CharSequence {
                return SpannableStringBuilder(text).apply {
                    if (textToBold.isBlank()) return@apply

                    var start = text.indexOf(string = textToBold, ignoreCase = true)
                    while (start >= 0) {
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
        typefaceBold = CalligraphyTypefaceSpan(uk.co.chrisjenx.calligraphy.TypefaceUtils.load(context!!.assets, "fonts/OpenSans-Bold.ttf"))
        recyclerView.apply {
            adapter = this@ContactsSearchFragment.adapter
            if (itemDecorationCount == 0) addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(context, VERTICAL))
            addOnScrollListener(PositionOffsetScrollListener { position, offset ->
                presenter.onScrollChange(position, offset)
            })

            adapter?.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver(){
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    if(positionStart==0){
                        layoutManager?.scrollToPosition(0)
                    }
                }
            })
        }

        etSearchText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                p0?.let {
                    presenter.onQueryTextChange(it.toString())
                }

                if (!p0.isNullOrEmpty()) {
                    etSearchText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0,
                            R.drawable.ic_circle_close_search, 0)
                    setTouchListener(true)

                } else {
                    etSearchText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0,
                            0, 0)
                    setTouchListener(false)
                }
            }
        })

        etSearchText.setOnEditorActionListener(TextView.OnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                presenter.onQueryTextSubmit(etSearchText.text.toString())
                return@OnEditorActionListener true
            }
            return@OnEditorActionListener false
        })
    }

    private fun setTouchListener(isSetTouchListener: Boolean) {
        if (!isSetTouchListener) {
            etSearchText.setOnTouchListener(null)
        } else {
            etSearchText.setOnTouchListener { view, motionEvent ->
                val DRAWABLE_RIGHT = 2

                if (motionEvent.action == MotionEvent.ACTION_UP) {
                    if (motionEvent.rawX >= (etSearchText.right - etSearchText.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                        etSearchText.setText("")

                        return@setOnTouchListener true
                    }
                }
                return@setOnTouchListener false
            }
        }
    }

    override fun openChat(chatId: String, userId: String, userName: String) {
        findNavController().navigate(ContactsSearchFragmentDirections.userListToChat(userName, chatId, userId))
    }

    override fun setData(contactSearch: PagedList<User>) {
        adapter.submitList(contactSearch)

    }

    override fun scrollToPositionWithOffset(position: Int, offset: Int) {
        (recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).scrollToPositionWithOffset(position, offset)
    }


    override fun isShowToolbar() = true
    override fun layout() = R.layout.fragment_contacts_search
}

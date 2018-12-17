package com.example.ui.mySchedule.usersList

import android.arch.paging.PagedList
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.adapters.SimplePagingRecyclerViewAdapter
import com.example.adapters.ViewHolder
import com.example.data.models.User
import com.example.ui.base.BaseNestedNavigationFragment
import com.example.util.CropCircleTransformation
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_news_list.*
import kotlinx.android.synthetic.main.item_user_list.*
import javax.inject.Inject
import javax.inject.Provider

class UserListFragment : BaseNestedNavigationFragment(), UserListContract.View {

    @InjectPresenter
    lateinit var presenter: UserListPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserListPresenter>

    @ProvidePresenter
    fun providePresenter(): UserListPresenter = presenterProvider.get().apply {
        subevent_id = UserListFragmentArgs.fromBundle(arguments).subeventId
    }


    private val adapter: SimplePagingRecyclerViewAdapter<User> by lazy {
        object : SimplePagingRecyclerViewAdapter<User>(
                { oldItem, newItem -> oldItem.id == newItem.id },
                { oldItem, newItem -> oldItem == newItem }
        ) {
            override fun getItemLayout(itemView: Int) = R.layout.item_user_list

            override fun onBindItem(viewHolder: ViewHolder, item: User?, position: Int) {
                item!!
                viewHolder.apply {
                    if(!item.image.isNullOrEmpty()) {
                        Picasso.get()
                                .load(item.image)
                                .transform(CropCircleTransformation())
                                .placeholder(R.drawable.ic_launcher)
                                .into(ivAvatar)
                    }

                    tvName.text = item.name

                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@UserListFragment.adapter
            if (itemDecorationCount == 0) addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }


    override fun setUsers(users: PagedList<User>) {
        adapter.submitList(users)
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_user_list
}

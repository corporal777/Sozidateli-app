package com.example.ui.subevent.users

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.holders.PagedListGroup
import com.example.holders.UserItem
import com.example.ui.base.BaseFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_subevent_users.*
import javax.inject.Inject
import javax.inject.Provider

class SubeventUserListFragment : BaseFragment(), SubeventUserListContract.View {

    @InjectPresenter
    lateinit var presenter: SubeventUserListPresenter

    @Inject
    lateinit var presenterProvider: Provider<SubeventUserListPresenter>

    @ProvidePresenter
    fun providePresenter(): SubeventUserListPresenter = presenterProvider.get().apply {
        val args = SubeventUserListFragmentArgs.fromBundle(arguments!!)
        event = args.eventId
        subevent = args.subeventId
    }

    private val usersGroup = PagedListGroup<UserItem>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = GroupAdapter<GroupieViewHolder>().apply { add(usersGroup) }
            if (itemDecorationCount == 0) addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(context, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL))
        }
    }

    override fun setUsers(users: PagedList<UserItem>) {
        usersGroup.submitList(users)
    }

    override fun openChat(userName: String, userAvatar: String?, chatId: String) {
        findNavController().navigate(SubeventUserListFragmentDirections.subeventUserListFragmentToChat(userName, chatId).apply {
            setUserAvatar(userAvatar)
        })
    }

    override fun showEmptyListPlaceholder() {
        recyclerView.visibility = View.GONE
        placeholder.visibility = View.VISIBLE
        tvMessage.text = getString(R.string.subevent_user_list_empty)
    }

    override fun layout() = R.layout.fragment_subevent_users
}

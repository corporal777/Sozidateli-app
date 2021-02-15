package com.example.ui.search.user

import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.R
import com.example.data.models.SearchFilter
import com.example.data.models.user.User
import com.example.extensions.findItemBy
import com.example.holders.PlaceholderItem
import com.example.holders.UserItem
import com.example.ui.search.SearchFragment
import com.xwray.groupie.Group
import kotlinx.android.synthetic.main.layout_filter_user.view.*
import onTextChanged

abstract class AbstractSearchUserFragment<P : SearchUserContract.Presenter> : SearchFragment<P, User, SearchFilter.User>(), SearchUserContract.View {

    override fun createItem(itemData: User?): Group {
        return if (itemData == null) PlaceholderItem(PlaceholderItem.Type.USER)
        else UserItem(
                itemData.user_id,
                itemData.fullName,
                itemData.user_city,
                itemData.user_avatar,
                { presenter.onUserClick(itemData) },
                //itemData.getUserSubscribeAction(),
                { presenter.onUserActionCLick(itemData) }
        )
    }

    override fun updateUser(user: User) {
        val idLong = user.user_id.toLong()
        val item = adapter.findItemBy { userItem: UserItem -> userItem.id == idLong } ?: return
        item.notifyChanged(user.getUserSubscribeAction())
    }

    override fun showUser(user: User) {
        findNavController().navigate(R.id.user_fragment, bundleOf("userId" to user.user_id.toString()))
    }

    override fun createFilterView(filter: SearchFilter.User): View {
        return layoutInflater.inflate(R.layout.layout_filter_user, null).apply {
            etAddress.apply {
                setTextWithoutSearch(filter.address)
                onTextChanged { filter.address = it.toString() }
            }

            val interests = filter.interests
            if (interests.isNullOrEmpty()) {
                tilTheme.isVisible = false
                tilSpec.isVisible = false
            } else {
                initInterests(interests, tvTheme, tilSpec, tvSpec, filter.theme, filter.spec) { theme, spec ->
                    filter.theme = theme
                    filter.spec = spec
                }
                tilTheme.isVisible = true
                tilSpec.isVisible = true
            }
        }
    }

    override fun clearFilterView(filterView: View) {
        filterView.apply {
            etAddress.text = null
            tvTheme.text = null
            tvSpec.text = null
        }
    }
}
package com.example.ui.search.user

import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.SearchFilter
import com.example.data.models.user.User
import com.example.holders.SearchUserItem
import com.example.ui.search.SearchFragment
import com.xwray.groupie.kotlinandroidextensions.Item
import javax.inject.Inject
import javax.inject.Provider

class SearchUserFragment : SearchFragment<SearchUserPresenter, User, SearchFilter.User>(), SearchUserContract.View {

    @InjectPresenter
    override lateinit var presenter: SearchUserPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchUserPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchUserPresenter = presenterProvider.get()

    override fun createItem(itemData: User): Item {
        return SearchUserItem(
                itemData.user_id,
                itemData.fullName,
                itemData.user_avatar,
                itemData.user_address
        ) { presenter.onUserClick(itemData) }
    }

    override fun showUser(user: User) {
        findNavController().navigate(R.id.user_fragment, bundleOf("userId" to user.user_id.toString()))
    }

    override fun createFilterView(filter: SearchFilter.User): View {
        return View(requireContext())
    }

    override fun clearFilterView(filterView: View) {

    }
}
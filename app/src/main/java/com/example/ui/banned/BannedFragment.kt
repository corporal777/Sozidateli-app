package com.example.ui.banned

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserChat
import com.example.holders.UserUnblockItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.util.LayoutListWithPlaceholderUtil
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.layout_list_with_placeholder.*
import javax.inject.Inject
import javax.inject.Provider

class BannedFragment : BaseFragment(), BannedContract.View, ToolbarFragment {

    override val title: CharSequence
        get() = getString(R.string.profile_banned)

    @InjectPresenter
    lateinit var presenter: BannedPresenter

    @Inject
    lateinit var presenterProvider: Provider<BannedPresenter>

    @ProvidePresenter
    fun providePresenter(): BannedPresenter = presenterProvider.get()

    private lateinit var placeholderUtil: LayoutListWithPlaceholderUtil

    private val adapter = GroupAdapter<ViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        recyclerView.apply {
            adapter = this@BannedFragment.adapter
        }

        placeholderUtil = LayoutListWithPlaceholderUtil(view).apply {
            doNotShowUntilDataLoad = true
            setMessage(getString(R.string.banned_empty_message))
            setImage(R.drawable.ic_neutral_face)
        }
    }

    override fun setItems(userChats: List<UserChat>) {
        adapter.update(userChats.map {
            UserUnblockItem(
                    it.id,
                    it.user.fullName,
                    it.user.user_avatar,
                    { presenter.onUserClick(it) },
                    { presenter.onUnblockLick(it) }
            )
        })
        placeholderUtil.isDataLoad = true
    }

    override fun openUserInfo(userId: String) {
        findNavController().navigate(BannedFragmentDirections.bannedFragmentToUserFragment(userId))
    }

    override fun layout() = R.layout.layout_list_with_placeholder
}

package com.example.ui.user

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.user.User
import com.example.holders.UserProfileItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.UserSubscribeButton.Companion.ACTION_SUBSCRIBE
import com.example.ui.views.UserSubscribeButton.Companion.ACTION_UNBLOCK
import com.example.ui.views.UserSubscribeButton.Companion.ACTION_UNSUBSCRIBE
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_chat_list.*
import javax.inject.Inject
import javax.inject.Provider

class UserFragment : BaseFragment(), UserContract.View, ToolbarFragment {

    override val title
        get() = getString(R.string.profile_label)

    @InjectPresenter
    lateinit var presenter: UserPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserPresenter>

    @ProvidePresenter
    fun providePresenter(): UserPresenter = presenterProvider.get().apply {
        val args = UserFragmentArgs.fromBundle(arguments!!)
        userId = args.userId
    }

    private val adapter = GroupAdapter<ViewHolder>()

    private var userProfileItem: UserProfileItem? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = this@UserFragment.adapter
        }
    }

    override fun setUser(user: User) {
        initProfileItem(user)

        adapter.update(listOf(
                userProfileItem
        ))
    }

    private fun initProfileItem(user: User) {
        userProfileItem = UserProfileItem(
                100L,
                user.user_avatar,
                user.fullName,
                user.user_id,
                when {
                    user.user_banned -> ACTION_UNBLOCK
                    user.is_in_favorite -> ACTION_UNSUBSCRIBE
                    else -> ACTION_SUBSCRIBE
                },
                {
                    presenter.apply {
                        when (it) {
                            ACTION_UNBLOCK -> onUnblockClick()
                            ACTION_SUBSCRIBE -> onSubscribeClick()
                            ACTION_UNSUBSCRIBE -> onUnsubscribeClick()
                        }
                    }
                },
                {
                    presenter.onWriteMessageClick()
                })
    }

    override fun setActionSubscribe() {
        userProfileItem?.notifyChanged(ACTION_SUBSCRIBE)
    }

    override fun setActionUnsubscribe() {
        userProfileItem?.notifyChanged(ACTION_UNSUBSCRIBE)
    }

    override fun setActionUnblock() {
        userProfileItem?.notifyChanged(ACTION_UNBLOCK)
    }

    override fun openChat(userName: String, userAvatar: String?, chatId: String) {
        findNavController().apply {
            if (!popBackStack(R.id.chat_fragment, false)) {
                navigate(UserFragmentDirections.userToChat(userName, chatId).apply {
                    setUserAvatar(userAvatar)
                })
            }
        }
    }

    override fun layout() = R.layout.fragment_user
}

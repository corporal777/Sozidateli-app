package com.example.ui.user

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Organization
import com.example.data.models.user.User
import com.example.extensions.defaultDateFormatter
import com.example.extensions.defaultServerDateFormatter
import com.example.holders.*
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.UserSubscribeButton.Companion.ACTION_SUBSCRIBE
import com.example.ui.views.UserSubscribeButton.Companion.ACTION_UNBLOCK
import com.example.ui.views.UserSubscribeButton.Companion.ACTION_UNSUBSCRIBE
import com.xwray.groupie.Group
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

    private lateinit var profileUserItem: ProfileUserItem

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = this@UserFragment.adapter
        }
    }

    override fun setUser(user: User) {
        initProfileItem(user)
        adapter.update(
                mutableListOf<Group>(profileUserItem)
                        .addPersonalDataItems(user)
                        .addEducation(user)
                        .addWorkExperience(user)
        )
    }

    private fun initProfileItem(user: User) {
        profileUserItem = ProfileUserItem(
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

    private fun MutableList<Group>.addPersonalDataItems(user: User): MutableList<Group> {
        val organizations: List<Organization>? = emptyList()
        val email = user.user_email
        val workPhone = user.user_phone_work
        val mobilePhone = user.user_phone
        val gender = user.user_gender
        val birthday = user.user_birthday?.let { string ->
            val date = try {
                defaultServerDateFormatter.parse(string)
            } catch (e: Throwable) {
                null
            }

            date?.let { defaultDateFormatter.format(it) }
        }
        val city = user.user_address_city
        val socialNetworks = user.user_social_links

        if (!organizations.isNullOrEmpty() || email != null || workPhone != null || mobilePhone != null
                || gender != null || city != null || birthday != null || !socialNetworks.isNullOrEmpty()) {
            this += ProfilePersonalDataItem(
                    organizations,
                    email,
                    workPhone,
                    mobilePhone,
                    gender,
                    birthday,
                    city,
                    socialNetworks
            ) {

                //TODO SHOW ORGANIZATION SCREEN
                showToast("CLICK: ${it.name}")
            }
        }

        return this
    }

    private fun MutableList<Group>.addEducation(user: User): MutableList<Group> {
        val education = user.education
        if (!education.isNullOrEmpty()) {
            this += ProfileExpandableTitleGroup(getString(R.string.profile_title_education)).apply {
                addAll(education.mapIndexed { index, socialRoles -> ProfileEducationDataItem(socialRoles, index == 0) })
            }
        }
        return this
    }

    private fun MutableList<Group>.addWorkExperience(user: User): MutableList<Group> {
        val work = user.work
        if (!work.isNullOrEmpty()) {
            this += ProfileExpandableTitleGroup(getString(R.string.profile_work_experience)).apply {
                addAll(work.mapIndexed { index, socialRoles -> ProfileWorkExperienceDataItem(socialRoles, index == 0) })
            }
        }
        return this
    }

    override fun setActionSubscribe() {
        profileUserItem.notifyChanged(ACTION_SUBSCRIBE)
    }

    override fun setActionUnsubscribe() {
        profileUserItem.notifyChanged(ACTION_UNSUBSCRIBE)
    }

    override fun setActionUnblock() {
        profileUserItem.notifyChanged(ACTION_UNBLOCK)
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

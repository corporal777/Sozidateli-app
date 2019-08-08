package com.example.ui.user

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isEmpty
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Interest
import com.example.data.models.Organization
import com.example.data.models.user.RecommendationFile
import com.example.data.models.user.User
import com.example.extensions.defaultDateFormatter
import com.example.extensions.defaultServerDateFormatter
import com.example.holders.*
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.UserSubscribeButton.Companion.ACTION_SUBSCRIBE
import com.example.ui.views.UserSubscribeButton.Companion.ACTION_UNBLOCK
import com.example.ui.views.UserSubscribeButton.Companion.ACTION_UNSUBSCRIBE
import com.example.ui.views.toolbar.ToolbarContentActionBar
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_chat_list.*
import setSelectableItemBackgroundBorderless
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

    private val onFileClickListener: (RecommendationFile) -> Unit = {
        presenter.onFileClick(it)
    }

    private val onOrganizationClickListener: (Organization) -> Unit = {
        presenter.onOrganizationClick(it)
    }

    private val onItemExpandChange: OnExpandChange<*> = {
        if (it.isExpanded) {
            val position = adapter.getAdapterPosition(it.titleItem)
            (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, 0)
        }
    }

    private val adapter = GroupAdapter<ViewHolder>()

    private lateinit var toolbarContentActionBar: ToolbarContentActionBar
    private lateinit var menuImageView: ImageView

    private lateinit var profileUserItem: ProfileDataUserItem

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = this@UserFragment.adapter
        }
    }

    override fun setUser(user: User, interests: Map<Interest, List<Interest>>?) {
        initProfileItem(user)
        adapter.update(
                mutableListOf<Group>(profileUserItem)
                        .addPersonalDataItems(user)
                        .addEducation(user)
                        .addWorkExperience(user)
                        .addInterests(interests)
                        .addAdditionalInformation(user)
        )
    }

    private fun initProfileItem(user: User) {
        profileUserItem = ProfileDataUserItem(
                100L,
                user.user_avatar,
                user.fullName,
                user.user_id,
                when {
                    user.chat?.isBannedByYou == true -> ACTION_UNBLOCK
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
        val organizations: List<Organization>? = user.organisations
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
            this += ProfileDataPersonalItem(
                    organizations,
                    email,
                    workPhone,
                    mobilePhone,
                    gender,
                    birthday,
                    city,
                    socialNetworks,
                    onOrganizationClickListener)
        }

        return this
    }

    private fun MutableList<Group>.addEducation(user: User): MutableList<Group> {
        val education = user.education
        if (!education.isNullOrEmpty()) {
            this += ProfileExpandableTitleGroup(getString(R.string.profile_title_education), onItemExpandChange).apply {
                addAll(education.mapIndexed { index, socialRoles -> ProfileDataEducationItem(socialRoles, index == 0) })
            }
        }
        return this
    }

    private fun MutableList<Group>.addWorkExperience(user: User): MutableList<Group> {
        val work = user.work
        if (!work.isNullOrEmpty()) {
            this += ProfileExpandableTitleGroup(getString(R.string.profile_work_experience), onItemExpandChange).apply {
                addAll(work.mapIndexed { index, socialRoles -> ProfileDataWorkExperienceItem(socialRoles, index == 0) })
            }
        }
        return this
    }

    private fun MutableList<Group>.addInterests(interests: Map<Interest, List<Interest>>?): MutableList<Group> {
        if (!interests.isNullOrEmpty()) {
            this += ProfileExpandableTitleGroup(getString(R.string.profile_interests), onItemExpandChange).apply {
                titleItem.hideDividerOnExpand = false
                addAll(interests.map {
                    val parent = it.key
                    val childList = it.value
                    ProfileExpandableSubtitleGroup(parent.value, onItemExpandChange).apply {
                        addAll(childList.mapIndexed { index, interest -> ProfileDataInterestItem(interest, index != childList.size - 1) })
                    }
                })
            }
        }
        return this
    }

    private fun MutableList<Group>.addAdditionalInformation(user: User): MutableList<Group> {
        val notes = user.user_notes
        val files = user.attached_recomendation_files

        val subgroups = mutableListOf<Group>()
        if (!notes.isNullOrBlank()) {
            subgroups.add(ProfileExpandableSubtitleGroup(getString(R.string.profile_notes), onItemExpandChange).apply {
                add(ProfileDataNotesItem(notes))
            })
        }

        if (!files.isNullOrEmpty()) {
            subgroups.add(ProfileExpandableSubtitleGroup(getString(R.string.profile_files), onItemExpandChange).apply {
                addAll(files.mapIndexed { index, file -> ProfileDataFileItem(file, index != files.size - 1, onFileClickListener) })
            })
        }

        if (subgroups.isNotEmpty()) {
            this += ProfileExpandableTitleGroup(getString(R.string.profile_additional_data), onItemExpandChange).apply {
                titleItem.hideDividerOnExpand = false
                addAll(subgroups)
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
        findNavController().navigate(UserFragmentDirections.userToChat(userName, chatId).apply {
            setUserAvatar(userAvatar)
        })
    }

    override fun showOrganization(organization: Organization) {
        TODO()
    }

    override fun downloadFile(file: String) {
        val uri = Uri.parse(file)
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (e: ActivityNotFoundException) {
            showToast(R.string.error_title)
        }
    }

    override fun showUserMenuButton(show: Boolean) {
        if (show) {
            val imageSize = resources.getDimensionPixelSize(R.dimen.toolbar_content_button_size)
            menuImageView = AppCompatImageButton(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(imageSize, ViewGroup.LayoutParams.MATCH_PARENT)
                setSelectableItemBackgroundBorderless()
                setImageResource(R.drawable.ic_menu)
                setOnClickListener { presenter.onMenuButtonUserClick() }
            }

            toolbarContentActionBar.addRightView(menuImageView)
        } else {
            toolbarContentActionBar.removeAllRightViews()
        }
    }

    override fun showUserMenu(isBlocked: Boolean) {
        PopupMenu(requireContext(), menuImageView).apply {
            menu.apply {
                val text = if (isBlocked) R.string.unblock else R.string.block
                val blockItem = if (isEmpty()) add(text) else getItem(0).apply {
                    this.setTitle(text)
                }

                blockItem.setOnMenuItemClickListener {
                    if (isBlocked) presenter.onUnblockClick() else presenter.onBlockClick()
                    return@setOnMenuItemClickListener true
                }
            }
            show()
        }
    }

    override fun showBlockConfirmation() {
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.user_ban_confirmation_title)
                .setMessage(R.string.user_ban_confirmation_message)
                .setPositiveButton(R.string.ok) { _, _ -> presenter.onBlockConfirm() }
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    override fun setupToolbarContent(toolbarContentActionBar: ToolbarContentActionBar) {
        super.setupToolbarContent(toolbarContentActionBar)
        this.toolbarContentActionBar = toolbarContentActionBar
    }

    override fun layout() = R.layout.fragment_user
}

package com.example.ui.user

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
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
import com.example.data.models.ProfileUserData
import com.example.data.models.UserEditDataType
import com.example.data.models.user.RecommendationFile
import com.example.data.models.user.User
import com.example.extensions.formatToDefaultDate
import com.example.holders.*
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.UserSubscribeButton.Companion.ACTION_SUBSCRIBE
import com.example.ui.views.UserSubscribeButton.Companion.ACTION_UNBLOCK
import com.example.ui.views.UserSubscribeButton.Companion.ACTION_UNSUBSCRIBE
import com.example.ui.views.toolbar.ToolbarContentActionBar
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
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

    private val personalDataSection = Section()
    private val educationDataSection = Section()
    private val workDataSection = Section()
    private val interestsDataSection = Section()

    private val dataSection = Section().apply {
        add(personalDataSection)
        add(educationDataSection)
        add(workDataSection)
        add(interestsDataSection)
    }
    private val adapter = GroupAdapter<ViewHolder>().apply {
        add(dataSection)
    }

    private lateinit var toolbarContentActionBar: ToolbarContentActionBar
    private lateinit var menuImageView: ImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = this@UserFragment.adapter
        }
    }

    override fun setUser(profileUserData: ProfileUserData) {
        val user = profileUserData.user
        val avatar = profileUserData.avatar
        val interests = profileUserData.interests
        val editable = profileUserData.editable
        val headerItem = if (editable) initEditableProfileItem(user, avatar)
        else initProfileItem(user, avatar)
        dataSection.setHeader(headerItem)
        personalDataSection.update(listOfNotNull(initPersonalDataItem(user, editable)))
        educationDataSection.update(listOfNotNull(initEducationDataItem(user, editable)))
        workDataSection.update(listOfNotNull(initWorkExperience(user, editable)))
        interestsDataSection.update(listOfNotNull(initInterests(interests, editable)))
    }

    private fun initEditableProfileItem(user: User, avatar: Bitmap?): Item {
        return ProfileDataUserEditableItem(
                HEADER_ITEM_ID,
                avatar,
                user.fullName,
                user.user_id,
                user.user_status,
                { presenter.onEditMainDataClick() },
                { presenter.onStatusClick() }
        )
    }

    private fun initProfileItem(user: User, avatar: Bitmap?): ProfileDataUserItem {
        return ProfileDataUserItem(
                HEADER_ITEM_ID,
                avatar,
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

    private fun initPersonalDataItem(user: User, editable: Boolean): Group? {
        return if (editable) {
            ProfileExpandableTitleGroup(
                    getString(R.string.profile_title_general_info),
                    onItemExpandChange,
                    { presenter.onEditPersonalDataClick() }
            ).apply {
                add(initProfileDataPersonalItem(user, false))
            }
        } else {
            initProfileDataPersonalItem(user, true)
        }
    }

    private fun initProfileDataPersonalItem(user: User, withOrganizations: Boolean): ProfileDataPersonalItem {
        val organizations: List<Organization>? = if (withOrganizations) user.organisations else null
        val email = user.user_email
        val workPhone = user.user_phone_work
        val mobilePhone = user.user_phone
        val gender = user.user_gender
        val birthday = user.user_birthday?.formatToDefaultDate()
        val city = user.user_address_city
        val socialNetworks = user.social_links

        return ProfileDataPersonalItem(
                organizations,
                email,
                workPhone,
                mobilePhone,
                gender,
                birthday,
                city,
                socialNetworks?.map { it.value },
                onOrganizationClickListener)
    }

    private fun initEducationDataItem(user: User, editable: Boolean): Group? {
        val education = user.education ?: emptyList()
        return if (editable) {
            ProfileExpandableTitleGroup(
                    getString(R.string.profile_title_education),
                    onItemExpandChange,
                    { presenter.onEditEducationClick() }
            ).apply {
                add(Section().apply {
                    setHeader(ProfileDataEducationLevelItem(user.user_education))
                    addAll(education.mapIndexed { index, socialRoles ->
                        ProfileDataEducationItem(socialRoles, index == 0)
                    })
                })
            }
        } else null
    }

    private fun initWorkExperience(user: User, editable: Boolean): Group? {
        val work = user.work ?: emptyList()
        return if (editable) {
            ProfileExpandableTitleGroup(
                    getString(R.string.profile_work_experience),
                    onItemExpandChange,
                    { presenter.onEditWorkClick() }
            ).apply {
                add(Section().apply {
                    addAll(work.mapIndexed { index, socialRoles ->
                        ProfileDataWorkExperienceItem(socialRoles, index == 0)
                    })
                })
            }
        } else null
    }

    private fun initInterests(interests: Map<Interest, List<Interest>>?, editable: Boolean): Group? {
        val nonNullInterests = interests ?: emptyMap()
        return if (editable) {
            ProfileExpandableTitleGroup(
                    getString(R.string.profile_interests),
                    onItemExpandChange,
                    { presenter.onEditInterestsClick() }
            ).apply {
                add(Section().apply {
                    addAll(nonNullInterests.map {
                        val parent = it.key
                        val childList = it.value
                        ProfileExpandableSubtitleGroup(parent.value, onItemExpandChange).apply {
                            addAll(childList.mapIndexed { index, interest -> ProfileDataInterestItem(interest, index != childList.size - 1) })
                        }
                    })
                })
            }
        } else null
    }

    private fun MutableList<Group>.addAdditionalInformation(user: User, editable: Boolean): MutableList<Group> {
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

    private fun ExpandableGroup.getGroupChild(): List<Group> {
        val itemsCount = groupCount
        val childList = mutableListOf<Group>()
        for (index in 1 until itemsCount) {
            childList.add(getGroup(index))
        }

        return childList
    }

    override fun setActionSubscribe() {
        dataSection.notifyItemChanged(0, ACTION_SUBSCRIBE)
    }

    override fun setActionUnsubscribe() {
        dataSection.notifyItemChanged(0, ACTION_UNSUBSCRIBE)
    }

    override fun setActionUnblock() {
        dataSection.notifyItemChanged(0, ACTION_UNBLOCK)
    }

    override fun openChat(userName: String, userAvatar: String?, chatId: String) {
        findNavController().navigate(UserFragmentDirections.userToChat(userName, chatId).apply {
            setUserAvatar(userAvatar)
        })
    }

    override fun showStatus() {
        findNavController().navigate(UserFragmentDirections.userToStatus())
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

    override fun showUpdateError(message: String?) {
        val title = getString(R.string.profile_edit_request_error)
        Toast.makeText(requireContext(), message?.let { "$title: $it" }
                ?: title, Toast.LENGTH_SHORT).show()
    }

    override fun showDataEditor(type: UserEditDataType) {
        findNavController().navigate(UserFragmentDirections.userToEdit(type))
    }

    override fun setupToolbarContent(toolbarContentActionBar: ToolbarContentActionBar) {
        super.setupToolbarContent(toolbarContentActionBar)
        this.toolbarContentActionBar = toolbarContentActionBar
    }

    override fun layout() = R.layout.fragment_user

    companion object {
        private const val HEADER_ITEM_ID = 100L
    }
}

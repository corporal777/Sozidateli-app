package com.example.ui.user

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.isEmpty
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Interest
import com.example.data.models.Organization
import com.example.data.models.ProfileUserData
import com.example.data.models.UserEditDataType
import com.example.data.models.user.User
import com.example.extensions.formatToDefaultDate
import com.example.holders.*
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.image.ImageViewActivityArgs
import com.example.ui.views.UserSubscribeButton
import com.example.ui.views.toolbar.ToolbarButton
import com.example.ui.views.toolbar.ToolbarContentActionBar
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
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

    private val onOrganizationClickListener: (Organization) -> Unit = {
        presenter.onOrganizationClick(it)
    }

    private val onItemExpandChange: OnExpandChange<*> = {
        if (it.isExpanded) {
            val position = adapter.getAdapterPosition(it.titleItem)
            (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, 0)
        }
    }

    private val mainDataSection = Section()
    private val personalDataSection = Section()
    private val educationDataSection = Section()
    private val workDataSection = Section()
    private val interestsDataSection = Section()
    private val additionalDataSection = Section()

    private val adapter = GroupAdapter<GroupieViewHolder>().apply {
        add(mainDataSection)
        add(personalDataSection)
        add(educationDataSection)
        add(workDataSection)
        add(interestsDataSection)
        add(additionalDataSection)
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
        mainDataSection.update(listOf(headerItem))
        personalDataSection.update(listOfNotNull(initPersonalDataItem(user, editable)))
        educationDataSection.update(listOfNotNull(initEducationDataItem(user, editable)))
        workDataSection.update(listOfNotNull(initWorkExperience(user, editable)))
        interestsDataSection.update(listOfNotNull(initInterests(interests, editable)))
        additionalDataSection.update(listOfNotNull(initAdditionalInformation(user, editable)))
    }

    private fun initEditableProfileItem(user: User, avatar: Bitmap?): Item {
        return ProfileDataUserEditableItem(
                HEADER_ITEM_ID,
                user.user_avatar,
                avatar,
                user.fullName,
                user.user_id,
                user.user_status ?: User.Status.LOW_PROTECTION,
                { presenter.onEditMainDataClick() },
                { presenter.onStatusClick() },
                { imageView ->
                    val url = user.user_avatar ?: return@ProfileDataUserEditableItem
                    onAvatarClick(imageView, url)
                }
        )
    }

    private fun initProfileItem(user: User, avatar: Bitmap?): ProfileDataUserItem {
        return ProfileDataUserItem(
                HEADER_ITEM_ID,
                user.user_avatar,
                avatar,
                user.fullName,
                user.user_id,
                when {
                    user.chat?.isBannedByYou == true -> UserSubscribeButton.Action.UNBLOCK
                    user.is_in_favorite -> UserSubscribeButton.Action.UNFAVORITE
                    else -> UserSubscribeButton.Action.FAVORITE
                },
                {
                    presenter.apply {
                        when (it) {
                            UserSubscribeButton.Action.FAVORITE -> onSubscribeClick()
                            UserSubscribeButton.Action.UNFAVORITE -> onUnsubscribeClick()
                            UserSubscribeButton.Action.UNBLOCK -> onUnblockClick()
                            else -> throw IllegalArgumentException("Wrong action: $it for user")
                        }
                    }
                },
                {
                    presenter.onWriteMessageClick()
                },
                { imageView ->
                    val url = user.user_avatar ?: return@ProfileDataUserItem
                    onAvatarClick(imageView, url)
                }
        )
    }

    private fun onAvatarClick(imageView: ImageView, url: String) {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(),
                Pair(imageView, imageView.transitionName)
        )

        findNavController().navigate(
                R.id.image_view_activity,
                ImageViewActivityArgs.Builder(url, null, null, imageView.transitionName).build().toBundle(),
                null,
                ActivityNavigatorExtras(options)
        )
    }

    private fun initPersonalDataItem(user: User, editable: Boolean): Group? {
        return if (editable) {
            ProfileExpandableTitleGroup(
                    getString(R.string.profile_title_general_info),
                    onExpandChange = onItemExpandChange,
                    editClickListener = { presenter.onEditPersonalDataClick() }
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
        val educationLevel = user.user_education
        val education = user.education ?: emptyList()
        return if (editable || education.isNotEmpty() || !educationLevel.isNullOrEmpty()) {
            val editClick: OnEditClickListener = { presenter.onEditEducationClick() }
            ProfileExpandableTitleGroup(
                    getString(R.string.profile_title_education),
                    onExpandChange = onItemExpandChange,
                    editClickListener = if (editable) editClick else null
            ).apply {
                add(Section().apply {
                    setHeader(ProfileDataEducationLevelItem(educationLevel.let { if (it.isNullOrEmpty()) "-" else it }))
                    if (education.isEmpty() && editable) {
                        add(ActionButtonItem(0L, ActionButtonItem.ACTION_ADD_RECORD, editClick))
                    } else {
                        addAll(education.map { ProfileDataEducationItem(it) })
                    }
                })
            }
        } else null
    }

    private fun initWorkExperience(user: User, editable: Boolean): Group? {
        val work = user.work ?: emptyList()
        return if (editable || work.isNotEmpty()) {
            val editClick: OnEditClickListener = { presenter.onEditWorkClick() }
            ProfileExpandableTitleGroup(
                    getString(R.string.profile_work_experience),
                    onExpandChange = onItemExpandChange,
                    editClickListener = if (editable) editClick else null
            ).apply {
                add(Section().apply {
                    if (work.isEmpty() && editable) {
                        add(ActionButtonItem(0L, ActionButtonItem.ACTION_ADD_RECORD, editClick))
                    } else {
                        addAll(work.mapIndexed { index, socialRoles ->
                            ProfileDataWorkExperienceItem(socialRoles, index == 0)
                        })
                    }
                })
            }
        } else null
    }

    private fun initInterests(interests: Map<Interest, List<Interest>>?, editable: Boolean): Group? {
        val nonNullInterests = interests ?: emptyMap()
        return if (editable || nonNullInterests.isNotEmpty()) {
            val editClick: OnEditClickListener = { presenter.onEditInterestsClick() }
            ProfileExpandableTitleGroup(
                    getString(R.string.profile_interests),
                    onExpandChange = onItemExpandChange,
                    editClickListener = if (editable) editClick else null
            ).apply {
                add(Section().apply {
                    if (nonNullInterests.isEmpty() && editable) {
                        add(ActionButtonItem(0L, ActionButtonItem.ACTION_ADD_RECORD, editClick))
                    } else {
                        addAll(nonNullInterests.map {
                            val parent = it.key
                            val childList = it.value
                            ProfileExpandableSubtitleGroup(parent.value, onExpandChange = onItemExpandChange).apply {
                                addAll(childList.mapIndexed { index, interest -> ProfileDataInterestItem(interest, index != childList.size - 1) })
                            }
                        })
                    }
                })
            }
        } else null
    }

    private fun initAdditionalInformation(user: User, editable: Boolean): Group? {
        val editClick: OnEditClickListener = { presenter.onEditAdditionalDataClick() }
        val notes = user.user_notes
        val files = user.attached_recomendation_files ?: emptyList()

        val subgroups = mutableListOf<Group>()
        subgroups.add(ProfileExpandableSubtitleGroup(getString(R.string.profile_notes), onExpandChange = onItemExpandChange).apply {
            add(ProfileDataNotesItem(notes.let { if (it.isNullOrEmpty()) "-" else it }))
        })

        subgroups.add(ProfileExpandableSubtitleGroup(getString(R.string.profile_files), onExpandChange = onItemExpandChange).apply {
            if (files.isEmpty() && editable) {
                add(ActionButtonItem(0L, ActionButtonItem.ACTION_ADD_FILE, editClick))
            } else {
                addAll(files.mapIndexed { index, file ->
                    ProfileDataFileItem(
                            (if (file.desc.isNullOrBlank()) file.name else file.desc) ?: "file",
                            index != files.size - 1
                    ) { presenter.onFileClick(file) }
                })
            }
        })

        return if (editable || !notes.isNullOrEmpty() || files.isNotEmpty()) {
            ProfileExpandableTitleGroup(
                    getString(R.string.profile_additional_data),
                    onExpandChange = onItemExpandChange,
                    editClickListener = if (editable) editClick else null
            ).apply {
                titleItem.hideDividerOnExpand = false
                addAll(subgroups)
            }
        } else null
    }

    override fun setActionSubscribe() {
        mainDataSection.notifyItemChanged(0, UserSubscribeButton.Action.FAVORITE)
    }

    override fun setActionUnsubscribe() {
        mainDataSection.notifyItemChanged(0, UserSubscribeButton.Action.UNFAVORITE)
    }

    override fun setActionUnblock() {
        mainDataSection.notifyItemChanged(0, UserSubscribeButton.Action.UNBLOCK)
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
        findNavController().navigate(UserFragmentDirections.userToOrganization(organization.id))
    }

    override fun downloadFile(file: String) {
        val uri = Uri.parse(file)
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (e: ActivityNotFoundException) {
            showRequestErrorMessage()
        }
    }

    override fun showUserMenuButton(show: Boolean) {
        if (show) {
            menuImageView = ToolbarButton(requireContext()).apply {
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

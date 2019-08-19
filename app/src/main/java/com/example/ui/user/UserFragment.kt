package com.example.ui.user

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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

    private val dataSection = Section()
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
        setUser(
                if (profileUserData.editable) initEditableProfileItem(user, avatar)
                else initProfileItem(user, avatar),
                user,
                profileUserData.interests,
                profileUserData.editable
        )
    }

    private fun setUser(headerItem: Item, user: User, interests: Map<Interest, List<Interest>>?, editable: Boolean) {
        dataSection.setHeader(headerItem)
        dataSection.update(
                mutableListOf<Group>()
                        .addPersonalDataItems(user, editable)
                        .addEducation(user, editable)
                        .addWorkExperience(user, editable)
                        .addInterests(interests, editable)
                        .addAdditionalInformation(user, editable)
        )
    }

    private fun initEditableProfileItem(user: User, avatar: Bitmap?): ProfileDataUserEditableItem {
        return ProfileDataUserEditableItem(
                HEADER_ITEM_ID,
                avatar,
                user.fullName,
                user.user_id
        ) { presenter.onEditMainDataClick() }
    }

    override fun setMainDataEditMode(user: User, avatar: Bitmap?, edit: Boolean) {
        if (edit) {
            dataSection.setHeader(ProfileDataUserEditItem(
                    HEADER_ITEM_ID,
                    avatar,
                    user.user_name,
                    user.user_last_name,
                    user.user_middle_name,
                    { presenter.onRemoveAvatarClick() },
                    { presenter.onEditAvatarClick() },
                    { presenter.onEditSave(it) },
                    { presenter.onEditMainDataCancelClick() },
                    { presenter.onDisabledMainInputInfoClick() }
            ))
        } else {
            dataSection.setHeader(initEditableProfileItem(user, avatar))
        }
    }

    override fun changeUserAvatar(avatar: Bitmap?) {
        dataSection.notifyItemChanged(0, avatar)
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

    private fun MutableList<Group>.addPersonalDataItems(user: User, editable: Boolean): MutableList<Group> {
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

    private fun MutableList<Group>.addEducation(user: User, editable: Boolean): MutableList<Group> {
        val education = user.education
        if (!education.isNullOrEmpty()) {
            this += ProfileExpandableTitleGroup(getString(R.string.profile_title_education), onItemExpandChange).apply {
                addAll(education.mapIndexed { index, socialRoles -> ProfileDataEducationItem(socialRoles, index == 0) })
            }
        }
        return this
    }

    private fun MutableList<Group>.addWorkExperience(user: User, editable: Boolean): MutableList<Group> {
        val work = user.work
        if (!work.isNullOrEmpty()) {
            this += ProfileExpandableTitleGroup(getString(R.string.profile_work_experience), onItemExpandChange).apply {
                addAll(work.mapIndexed { index, socialRoles -> ProfileDataWorkExperienceItem(socialRoles, index == 0) })
            }
        }
        return this
    }

    private fun MutableList<Group>.addInterests(interests: Map<Interest, List<Interest>>?, editable: Boolean): MutableList<Group> {
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

    override fun showTakePictureChooser() {
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.photo_alert_title)
                .setPositiveButton(R.string.photo_alert_gallery) { _, _ -> presenter.onTakePhotoFromGalleryRequest() }
                .setNegativeButton(R.string.photo_alert_camera) { _, _ -> presenter.onTakePhotoFromCameraRequest() }
                .show()
    }

    override fun showDisabledMainInputInfo() {
        val message = SpannableString(getString(R.string.profile_edit_name_disabled_message))
        Linkify.addLinks(message, Linkify.EMAIL_ADDRESSES)
        val dialog = AlertDialog.Builder(requireContext())
                .setTitle(R.string.profile_edit_name_disabled_title)
                .setMessage(message)
                .setPositiveButton(R.string.ok) { _, _ -> }
                .show()

        (dialog.findViewById(android.R.id.message) as? TextView)?.movementMethod = LinkMovementMethod.getInstance()
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

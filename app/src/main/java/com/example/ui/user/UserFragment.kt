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
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
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
import com.example.data.models.UserAddress
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
import com.example.util.AuthValidateUtil
import com.google.android.material.textfield.TextInputLayout
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_chat_list.*
import onTextChanged
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

    private val dataSection = Section().apply {
        add(personalDataSection)
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
        val headerItem = if (profileUserData.editable) initEditableProfileItem(user, avatar, profileUserData.isEditMainData)
        else initProfileItem(user, avatar)
        val editable = profileUserData.editable
        val interests = profileUserData.interests

        dataSection.setHeader(headerItem)
        personalDataSection.update(listOfNotNull(initPersonalDataItem(user, editable, profileUserData.isEditPersonalData)))
    }

    private fun initEditableProfileItem(user: User, avatar: Bitmap?, edit: Boolean): Item {
        return if (edit) ProfileDataUserEditItem(
                HEADER_ITEM_ID,
                avatar,
                user.user_name,
                user.user_last_name,
                user.user_middle_name,
                { presenter.onRemoveAvatarClick() },
                { presenter.onEditAvatarClick() },
                { presenter.onEditMainSaveClick(it) },
                { presenter.onEditMainDataCancelClick() },
                { presenter.onDisabledMainInputInfoClick() }
        )
        else ProfileDataUserEditableItem(
                HEADER_ITEM_ID,
                avatar,
                user.fullName,
                user.user_id
        ) { presenter.onEditMainDataClick() }
    }

    override fun setMainDataEditMode(user: User, avatar: Bitmap?, edit: Boolean) {
        dataSection.setHeader(initEditableProfileItem(user, avatar, edit))
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

    private fun initPersonalDataItem(user: User, editable: Boolean, edit: Boolean): Group? {
        return if (editable) {
            ProfileExpandableTitleGroup(
                    getString(R.string.profile_title_general_info),
                    onItemExpandChange,
                    { presenter.onEditPersonalDataClick() }
            ).apply {
                titleItem.editMode = edit
                val item = initPersonalDataContentItem(user, edit)
                add(item)
            }
        } else {
            initProfileDataPersonalItem(user, true)
        }
    }

    private fun initPersonalDataContentItem(user: User, edit: Boolean): Item {
        return if (edit) initProfileDataEditPersonalItem(user)
        else initProfileDataPersonalItem(user, false)
                ?: initProfileDataEditPersonalItem(user)
    }

    override fun setPersonalDataDataEditMode(user: User, edit: Boolean) {
        (personalDataSection.getGroup(0) as? ProfileExpandableTitleGroup)?.apply {
            clear()
            add(initPersonalDataContentItem(user, edit))
            titleItem.editMode = edit
            if (!isExpanded) onToggleExpanded()
        }
    }

    private fun initProfileDataPersonalItem(user: User, withOrganizations: Boolean): ProfileDataPersonalItem? {
        val organizations: List<Organization>? = if (withOrganizations) user.organisations else null
        val email = user.user_email
        val workPhone = user.user_phone_work
        val mobilePhone = user.user_phone
        val gender = user.user_gender
        val birthday = user.user_birthday?.formatToDefaultDate()
        val city = user.user_address_city
        val socialNetworks = user.social_links

        if (!organizations.isNullOrEmpty() || email != null || workPhone != null || mobilePhone != null
                || gender != null || city != null || birthday != null || !socialNetworks.isNullOrEmpty()) {
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

        return null
    }

    private fun initProfileDataEditPersonalItem(user: User): ProfileDataEditPersonalItem {
        return ProfileDataEditPersonalItem(
                requireContext(),
                user.user_email,
                user.user_email_show,
                user.user_phone_work,
                user.user_phone_work_show,
                user.user_phone,
                user.user_phone_show,
                user.user_gender,
                user.user_birthday,
                user.user_birthday_show,
                UserAddress.fromUser(user),
                user.social_links,
                { presenter.onEditMainSaveClick(it) },
                { presenter.onEditMainDataCancelClick() },
                { presenter.onChangeEmailClick() },
                { presenter.onChangePasswordClick() }
        )
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
                .setPositiveButton(R.string.ok, null)
                .show()

        (dialog.findViewById(android.R.id.message) as? TextView)?.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun showChangeEmail() {
        val view = layoutInflater.inflate(R.layout.dialog_change_email, null)
        val til = view.findViewById<TextInputLayout>(R.id.tilEmail)
        val et = view.findViewById<EditText>(R.id.etEmail)
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.profile_email_change)
                .setView(view)
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel, null)
                .create()
                .apply {
                    setOnShowListener {
                        getButton(AlertDialog.BUTTON_POSITIVE).apply {
                            setOnClickListener {
                                val email = et.text.toString()
                                if (AuthValidateUtil.isValidEmail(email)) {
                                    presenter.onChangeEmailConfirm(email)
                                    dismiss()
                                } else til.error = getString(R.string.profile_edit_email_invalid)
                            }
                        }
                    }
                }
                .show()
    }

    override fun showChangeEmailComplete(email: String) {
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.email_change_title)
                .setMessage(String.format(getString(R.string.email_change_msg, email)))
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    override fun showChangePassword() {
        val view = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val tilOldPassword = view.findViewById<TextInputLayout>(R.id.tilOldPassword)
        val etOldPassword = view.findViewById<EditText>(R.id.etOldPassword).apply {
            onTextChanged { tilOldPassword.error = null }
        }
        val tilNewPassword = view.findViewById<TextInputLayout>(R.id.tilNewPassword)
        val etNewPassword = view.findViewById<EditText>(R.id.etNewPassword).apply {
            onTextChanged { tilNewPassword.error = null }
        }
        val tilNewPasswordConfirm = view.findViewById<TextInputLayout>(R.id.tilNewPasswordConfirm)
        val etNewPasswordConfirm = view.findViewById<EditText>(R.id.etNewPasswordConfirm).apply {
            onTextChanged { tilNewPasswordConfirm.error = null }
        }

        val emptyFieldError = getString(R.string.profile_edit_empty_field_error)
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.profile_password_change)
                .setView(view)
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel, null)
                .create()
                .apply {
                    setOnShowListener {
                        getButton(AlertDialog.BUTTON_POSITIVE).apply {
                            setOnClickListener {
                                var hasError = false
                                val oldPassword = etOldPassword.text?.toString()
                                val newPassword = etNewPassword.text?.toString()
                                val newPasswordConfirm = etNewPasswordConfirm.text?.toString()

                                if (oldPassword.isNullOrEmpty()) {
                                    tilOldPassword.error = emptyFieldError
                                    hasError = true
                                }

                                if (newPassword != newPasswordConfirm) {
                                    tilNewPasswordConfirm.error = getString(R.string.passwords_do_not_match)
                                    hasError = true
                                } else {
                                    if (newPassword.isNullOrEmpty()) {
                                        tilNewPassword.error = emptyFieldError
                                        hasError = true
                                    }
                                    if (newPasswordConfirm.isNullOrEmpty()) {
                                        tilNewPasswordConfirm.error = emptyFieldError
                                        hasError = true
                                    }
                                }

                                if (!hasError && oldPassword != null && newPassword != null && newPasswordConfirm != null) {
                                    presenter.onChangePasswordClickConfirm(oldPassword, newPassword, newPasswordConfirm)
                                    dismiss()
                                }
                            }
                        }
                    }
                }
                .show()
    }

    override fun showPasswordChangeComplete() {
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.profile_password_change)
                .setMessage(R.string.profile_password_change_complete)
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    override fun showUpdateError(message: String?) {
        val title = getString(R.string.profile_edit_request_error)
        Toast.makeText(requireContext(), message?.let { "$title: $it" }
                ?: title, Toast.LENGTH_SHORT).show()
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

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
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.*
import com.example.data.models.user.User
import com.example.extensions.formatToDefaultDate
import com.example.extensions.showChangePasswordDialog
import com.example.extensions.showPasswordChangeCompleteDialog
import com.example.holders.*
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.image.ImageViewActivityArgs
import com.example.ui.views.UserSubscribeButton
import com.example.ui.views.toolbar.ToolbarButton
import com.example.ui.views.toolbar.ToolbarContentActionBar
import com.example.util.PHONE_PERSONAL
import com.example.util.PHONE_WORK
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_user.*
import javax.inject.Inject
import javax.inject.Provider

class UserFragment : BaseFragment(), UserContract.View, ToolbarFragment {

    override val title: String? = null

    @InjectPresenter
    lateinit var presenter: UserPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserPresenter>

    @ProvidePresenter
    fun providePresenter(): UserPresenter = presenterProvider.get().apply {
        val args = UserFragmentArgs.fromBundle(requireArguments())
        userId = args.userId
    }

    private val onOrganizationClickListener: (/*Organization*/OrganizationNew) -> Unit = {
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

    private val editText by lazy {
        getString(R.string.edit)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = this@UserFragment.adapter
        }
        swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
    }

    override fun setUser(profileUserData: ProfileUserData) {
        val user = profileUserData.user
        val avatar = profileUserData.avatar
        val interests = profileUserData.interests
        val editable = profileUserData.editable
        val headerItem = if (editable) initEditableProfileItem(user, avatar)
        else initProfileItem(user, avatar)
        mainDataSection.update(listOf(headerItem))

        val personalData = mutableListOf<Group>()
        if (editable) {
            personalData.add(ProfileButtonItem(getString(R.string.profile_password_change)) { presenter.onChangePasswordClick() })
        }
        initPersonalDataItem(user, editable)?.let { personalData.add(it) }
        personalDataSection.update(personalData)
        educationDataSection.update(listOfNotNull(initEducationDataItem(user, editable)))
        workDataSection.update(listOfNotNull(initWorkExperience(user, editable)))
        interestsDataSection.update(listOfNotNull(initInterests(interests, editable)))
        additionalDataSection.update(listOfNotNull(initAdditionalInformation(user, editable)))
        swipeToRefresh.isRefreshing = false
    }

    private fun initEditableProfileItem(user: /*User*/UserDetail, avatar: Bitmap?): Item {
        return ProfileDataUserEditableItem(
                HEADER_ITEM_ID,
                user.image?.uri,
                avatar,
                user.fullName,
                user.id,
                { presenter.onEditMainDataClick() },
                { imageView ->
                    val url = user.image?.uri ?: return@ProfileDataUserEditableItem
                    onAvatarClick(imageView, url)
                }
        )
    }

    private fun initProfileItem(user: /*User*/UserDetail, avatar: Bitmap?): ProfileDataUserItem {
        return ProfileDataUserItem(
                HEADER_ITEM_ID,
                user.image?.uri,
                avatar,
                user.fullName,
                user.id,
                user.getUserSubscribeAction() ?: UserSubscribeButton.Action.FAVORITE,
                user.binds?.userFavorite != null,
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
                    val url = user.image?.uri ?: return@ProfileDataUserItem
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

    private fun initPersonalDataItem(user: /*User*/UserDetail, editable: Boolean): Group? {
        return ProfileExpandableTitleGroup(
                getString(R.string.profile_title_general_info),
                onExpandChange = onItemExpandChange
        ).apply {
            add(initProfileDataPersonalItem(user, editable))
            if (editable) add(ProfileButtonEditItem(editText, false) { presenter.onEditPersonalDataClick() })
        }
    }

    private fun initProfileDataPersonalItem(user: /*User*/UserDetail, editable: Boolean): ProfileDataPersonalItem {
        val organizations: List</*Organization*/OrganizationNew>? = if (!editable) user.binds?.organization else null
        val email = user.email?.value
        val workPhone = user.phone?.firstOrNull { it.type == PHONE_WORK }?.value
        val mobilePhone = user.phone?.firstOrNull { it.type == PHONE_PERSONAL }?.value
        val gender = user.gender
        val birthday = user.birthday?.value?.formatToDefaultDate()
        val city = user.address?.shortAddres ?: user.address?.city
        val socialNetworks = user.contactInformation.socialLinks?.values

        return ProfileDataPersonalItem(
                organizations,
                email,
                workPhone,
                mobilePhone,
                editable && user.phone?.firstOrNull { it.type == PHONE_PERSONAL }?.isConfirmed?: false,
                gender,
                birthday,
                city,
                socialNetworks,
                user.phone?.firstOrNull { it.type == PHONE_WORK }?.value,
                onOrganizationClickListener)
    }

    private fun initEducationDataItem(user: /*User*/UserDetail, editable: Boolean): Group? {
        val educationLevel = user.educationLevelList?.firstOrNull { it.id == user.educationLevel }?.name
        val academicDegrees = user.binds?.academicDegree ?: emptyList()
        val education = user.binds?.education ?: emptyList()
        return if (editable || education.isNotEmpty() || !educationLevel.isNullOrEmpty() || !academicDegrees.isNullOrEmpty()) {
            ProfileExpandableTitleGroup(
                    getString(R.string.profile_title_education),
                    onExpandChange = onItemExpandChange
            ).apply {
                add(Section().apply {
                    if (!educationLevel.isNullOrEmpty()) setHeader(ProfileDataEducationLevelItem(educationLevel, academicDegrees,
                            user.academicDegrees?: emptyList(), user.speciality?: emptyList()))
                    addAll(education.map { ProfileDataEducationItem(it) })
                    if (editable) add(ProfileButtonEditItem(editText, false) { presenter.onEditEducationClick() })
                })
            }
        } else null
    }

    private fun initWorkExperience(user: /*User*/UserDetail, editable: Boolean): Group? {
        val work = user.binds?.workExperience?.models ?: emptyList()
        return if (editable || work.isNotEmpty()) {
            ProfileExpandableTitleGroup(
                    getString(R.string.profile_work_experience),
                    onExpandChange = onItemExpandChange
            ).apply {
                add(Section().apply {
                    if (work.isNullOrEmpty()) add(ProfileNoWorkExperienceItem(resources.getString(R.string.no_experience)))
                    addAll(work.mapIndexed { index, socialRoles ->
                        ProfileDataWorkExperienceItem(socialRoles, index == 0) })
                    if (editable) add(ProfileButtonEditItem(editText, false) { presenter.onEditWorkClick() })
                })
            }
        } else null
    }

    private fun initInterests(interests: Map</*Interest*/InterestNew, List</*Interest*/InterestNew>>?, editable: Boolean): Group? {
        val nonNullInterests = interests ?: emptyMap()
        return if (editable || nonNullInterests.isNotEmpty()) {
            ProfileExpandableTitleGroup(
                    getString(R.string.profile_interests),
                    onExpandChange = onItemExpandChange
            ).apply {
                add(Section().apply {
                    addAll(nonNullInterests.map {
                        val parent = it.key
                        val childList = it.value
                        ProfileExpandableSubtitleGroup(parent.name?: "", onExpandChange = onItemExpandChange).apply {
                            addAll(childList.map { interest -> ProfileDataInterestItem(interest) })
                        }
                    })
                    if (editable) add(ProfileButtonEditItem(editText, false) { presenter.onEditInterestsClick() })
                })
            }
        } else null
    }

    private fun initAdditionalInformation(user: /*User*/UserDetail, editable: Boolean): Group? {
        val notes = user.notes
        val files = user.binds?.recommendationFile ?: emptyList()

        val subgroups = mutableListOf<Group>()
        subgroups.add(ProfileExpandableSubtitleGroup(getString(R.string.profile_notes), onExpandChange = onItemExpandChange).apply {
            add(ProfileDataNotesItem(notes.let { if (it.isNullOrEmpty()) "-" else it }))
            if (editable) add(ProfileButtonEditItem(editText, false) { presenter.onEditAdditionalNotesDataClick() })
        })

        subgroups.add(ProfileExpandableSubtitleGroup(getString(R.string.profile_files), onExpandChange = onItemExpandChange).apply {
            if (files.isNotEmpty()) {
                addAll(files.map { file ->
                    ProfileDataFileItem(
                            (/*if (file.desc.isNullOrBlank())*/ file.name /*else file.desc*/) ?: "file"
                    ) { presenter.onFileClick(file) }
                })
            }
            if (editable) add(ProfileButtonEditItem(editText, false) { presenter.onEditAdditionalFilesDataClick() })
        })

        return if (editable || !notes.isNullOrEmpty() || files.isNotEmpty()) {
            ProfileExpandableTitleGroup(
                    getString(R.string.profile_additional_data),
                    onExpandChange = onItemExpandChange
            ).apply {
                titleItem.hideDividerOnExpand = false
                addAll(subgroups)
            }
        } else null
    }

    override fun showChangePassword() = showChangePasswordDialog(presenter::onChangePasswordClickConfirm)

    override fun showPasswordChangeComplete() = showPasswordChangeCompleteDialog()

    override fun setSubscribeAction(action: UserSubscribeButton.Action?) {
        mainDataSection.notifyItemChanged(0, action)

        toolbarContentActionBar.removeAllRightViews()
        toolbarContentActionBar.addRightView(ToolbarButton(requireContext()).apply {
            setImageResource(if (action == UserSubscribeButton.Action.UNBLOCK) R.drawable.ic_revert else R.drawable.ic_block)
            setOnClickListener {
                if (action == UserSubscribeButton.Action.UNBLOCK) presenter.onUnblockClick()
                else presenter.onBlockClick()
            }
        })
    }

    override fun setNoTitle() {
        toolbarContentActionBar.title = null
    }

    override fun setProfileTitle() {
        toolbarContentActionBar.title = getString(R.string.profile_current_user_label)
    }

    override fun openChat(userName: String, userAvatar: String?, chatId: String) {
        findNavController().navigate(UserFragmentDirections.userToChat(userName, chatId).apply {
            setUserAvatar(userAvatar)
        })
    }

    override fun showStatus() {

    }

    override fun showOrganization(organization: /*Organization*/OrganizationNew) {
        findNavController().navigate(UserFragmentDirections.userToOrganization(organization.id.toString()))
    }

    override fun downloadFile(file: String) {
        val uri = Uri.parse(file)
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (e: ActivityNotFoundException) {
            showRequestErrorMessage()
        }
    }

    override fun showBlockConfirmation() {
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.user_ban_confirmation_title)
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

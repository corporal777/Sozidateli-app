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
import androidx.core.view.isVisible
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.BuildConfig
import com.example.R
import com.example.data.models.*
import com.example.databinding.FragmentUserBinding
import com.example.extensions.findItemBy
import com.example.extensions.formatToDefaultDate
import com.example.extensions.showChangePasswordDialog
import com.example.extensions.showPasswordChangeCompleteDialog
import com.example.holders.*
import com.example.ui.base.BaseFragmentNew
import com.example.ui.image.ImageViewActivityArgs
import com.example.ui.user.items.ProfileDataDividerItem
import com.example.ui.user.items.UserProfileActionsItem
import com.example.ui.views.UserSubscribeButton
import com.example.ui.views.dialogs_new.MessageDialogWithBrownButton
import com.example.ui.views.toolbar.ToolbarContentActionBar
import com.example.util.PHONE_PERSONAL
import com.example.util.PHONE_WORK
import com.example.util.firstLetterToUppercase
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import onScrolled
import javax.inject.Inject
import javax.inject.Provider

class UserFragment : BaseFragmentNew<FragmentUserBinding>(), UserContract.View {

    @InjectPresenter
    lateinit var presenter: UserPresenter

    private var mDy: Int = 0

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
            val mSmoothScroller: RecyclerView.SmoothScroller =
                object : LinearSmoothScroller(requireContext()) {
                    override fun getVerticalSnapPreference(): Int {
                        return SNAP_TO_START
                    }
                }
            val mLayoutManager = mBinding.contentList.layoutManager as LinearLayoutManager
            mSmoothScroller.targetPosition = position
            mLayoutManager.startSmoothScroll(mSmoothScroller)
        }
    }

    private val mainDataSection = Section()
    private val personalDataSection = Section()
    private val educationDataSection = Section()
    private val workDataSection = Section()
    private val interestsDataSection = Section()
    private val additionalDataSection = Section()
    private val actionsDataSection = Section()

    private val adapter = GroupAdapter<GroupieViewHolder>().apply {
        add(mainDataSection)
        add(personalDataSection)
        add(educationDataSection)
        add(workDataSection)
        add(interestsDataSection)
        add(additionalDataSection)
        add(actionsDataSection)
    }

    private lateinit var toolbarContentActionBar: ToolbarContentActionBar

    private val editText by lazy {
        getString(R.string.edit)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.contentList.apply {
            adapter = this@UserFragment.adapter
            onScrolled { dx, dy ->
                mDy = this.computeVerticalScrollOffset()
                if (this.computeVerticalScrollOffset() <= 10) {
                    mBinding.appBar.elevation = mDy.toFloat()
                } else {
                    mBinding.appBar.elevation = 10f
                }
            }
        }
        mBinding.swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        mBinding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        mBinding.ivShare.setOnClickListener {
            if (!presenter.userId.isNullOrEmpty()) {
                showShare(presenter.userId)
            }
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

        if (profileUserData.user.state?.isRegistered == true) {
            if (editable) {
                actionsDataSection.update(emptyList())
            } else actionsDataSection.update(
                listOf(
                    ProfileDataDividerItem(),
                    initActions(profileUserData.user.getUserSubscribeAction())
                )
            )
        }

        mBinding.swipeToRefresh.isRefreshing = false
    }

    private fun initEditableProfileItem(user: /*User*/UserDetail, avatar: Bitmap?): Item {
        return ProfileDataUserEditableItem(
            HEADER_ITEM_ID,
            user.image?.uri,
            avatar,
            user.nameLastName,
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
            user.nameLastName,
            user.id,
            user.getUserSubscribeAction() ?: UserSubscribeButton.Action.FAVORITE,
            { presenter.onWriteMessageClick() },
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
            ImageViewActivityArgs.Builder(url, null, null, imageView.transitionName).build()
                .toBundle(),
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
            if (editable) add(
                ProfileButtonEditItem(
                    editText,
                    false
                ) { presenter.onEditPersonalDataClick() })
        }
    }

    private fun initProfileDataPersonalItem(
        user: /*User*/UserDetail,
        editable: Boolean
    ): ProfileDataPersonalItem {
        val organizations: List</*Organization*/OrganizationNew>? =
            if (!editable) user.binds?.organization else null
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
            editable && user.phone?.firstOrNull { it.type == PHONE_PERSONAL }?.isConfirmed ?: false,
            gender?.value,
            birthday,
            city,
            socialNetworks,
            user.phone?.firstOrNull { it.type == PHONE_WORK }?.value,
            onOrganizationClickListener
        )
    }

    private fun initEducationDataItem(user: /*User*/UserDetail, editable: Boolean): Group? {
        val educationLevel =
            user.educationLevelList?.firstOrNull { it.id == user.educationLevel?.value }?.name
        val academicDegrees = user.binds?.academicDegree ?: emptyList()
        val education = user.binds?.education ?: emptyList()
        return if (editable || education.isNotEmpty() || !educationLevel.isNullOrEmpty() || !academicDegrees.isNullOrEmpty()) {
            ProfileExpandableTitleGroup(
                getString(R.string.profile_title_education),
                onExpandChange = onItemExpandChange
            ).apply {
                add(Section().apply {
                    if (!educationLevel.isNullOrEmpty()) setHeader(
                        ProfileDataEducationLevelItem(
                            educationLevel, academicDegrees,
                            user.academicDegrees ?: emptyList(), user.speciality ?: emptyList()
                        )
                    )
                    addAll(education.map { ProfileDataEducationItem(it) })
                    if (editable) add(
                        ProfileButtonEditItem(
                            editText,
                            false
                        ) { presenter.onEditEducationClick() })
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
                        ProfileDataWorkExperienceItem(socialRoles, index == 0)
                    })
                    if (editable) add(
                        ProfileButtonEditItem(
                            editText,
                            false
                        ) { presenter.onEditWorkClick() })
                })
            }
        } else null
    }

    private fun initInterests(
        interests: Map</*Interest*/InterestNew, List</*Interest*/InterestNew>>?,
        editable: Boolean
    ): Group? {
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
                        ProfileExpandableSubtitleGroup(
                            parent.name ?: "",
                            onExpandChange = onItemExpandChange
                        ).apply {
                            addAll(childList.map { interest -> ProfileDataInterestItem(interest) })
                        }
                    })
                    if (editable) add(
                        ProfileButtonEditItem(
                            editText,
                            false
                        ) { presenter.onEditInterestsClick() })
                })
            }
        } else null
    }

    private fun initActions(action: UserSubscribeButton.Action?): Group? {
        return ProfileExpandableTitleGroup(
            getString(R.string.yet_btn_text).firstLetterToUppercase(),
            onExpandChange = onItemExpandChange
        ).apply {
            add(Section().apply {
                add(UserProfileActionsItem(getString(R.string.complain_about_user_label)) {

                })
                add(UserProfileActionsItem(action) {
                    if (action == UserSubscribeButton.Action.UNBLOCK) presenter.onUnblockClick()
                    else presenter.onBlockClick()
                })
            })
        }
    }

    private fun initAdditionalInformation(user: /*User*/UserDetail, editable: Boolean): Group? {
        val notes = user.notes
        val files = user.binds?.recommendationFile ?: emptyList()

        val subgroups = mutableListOf<Group>()
        subgroups.add(
            ProfileExpandableSubtitleGroup(
                getString(R.string.profile_notes),
                onExpandChange = onItemExpandChange
            ).apply {
                add(ProfileDataNotesItem(notes?.value.let { if (it.isNullOrEmpty()) "-" else it }))
                if (editable) add(
                    ProfileButtonEditItem(
                        editText,
                        false
                    ) { presenter.onEditAdditionalNotesDataClick() })
            })

        subgroups.add(
            ProfileExpandableSubtitleGroup(
                getString(R.string.profile_files),
                onExpandChange = onItemExpandChange
            ).apply {
                if (files.isNotEmpty()) {
                    addAll(files.map { file ->
                        ProfileDataFileItem(
                            (/*if (file.desc.isNullOrBlank())*/ file.name /*else file.desc*/)
                                ?: "file"
                        ) { presenter.onFileClick(file) }
                    })
                }
                if (editable) add(
                    ProfileButtonEditItem(
                        editText,
                        false
                    ) { presenter.onEditAdditionalFilesDataClick() })
            })

        return if (editable || !notes?.value.isNullOrEmpty() || files.isNotEmpty()) {
            ProfileExpandableTitleGroup(
                getString(R.string.profile_additional_data),
                onExpandChange = onItemExpandChange
            ).apply {
                addAll(subgroups)
            }
        } else null
    }

    override fun showChangePassword() =
        showChangePasswordDialog(presenter::onChangePasswordClickConfirm)

    override fun showPasswordChangeComplete() = showPasswordChangeCompleteDialog()

    override fun setSubscribeBlockAction(action: UserSubscribeButton.Action?) {
        mainDataSection.notifyItemChanged(0, action)
        val item = actionsDataSection.findItemBy<UserProfileActionsItem> { x -> x.isAction  }
        if (item != null) {
            item.notifyChanged(action)
            item.action = {
                if (action == UserSubscribeButton.Action.UNBLOCK) presenter.onUnblockClick()
                else presenter.onBlockClick()
            }
        }
    }

    override fun setEnableAddToFavoriteButton(enabled: Boolean) {
        mBinding.ivAddToFavorite.apply {
            isEnabled = enabled
            alpha = if (enabled) 1f
            else 0.6f
        }
    }

    override fun setSubscribeFavoriteAction(action: UserSubscribeButton.Action?) {
        mBinding.apply {
            ivAddToFavorite.isVisible = true
            if (action != null) {
                if (action == UserSubscribeButton.Action.UNFAVORITE) {
                    ivAddToFavorite.apply {
                        setImageResource(R.drawable.ic_star_filled)
                        setOnClickListener {
                            presenter.onUnsubscribeClick()
                        }
                    }
                } else {
                    ivAddToFavorite.apply {
                        setImageResource(R.drawable.ic_star)
                        setOnClickListener {
                            presenter.onSubscribeClick()
                        }
                    }
                }
            }
        }
    }


    override fun showUserHiddenDialog() {
        val message = "Данный профиль недоступен"
        MessageDialogWithBrownButton(requireContext(), message).setSelectCallback {
            findNavController().navigateUp()
        }
    }

    override fun openChat(userName: String, userAvatar: String?, chatId: String) {
        findNavController().navigate(UserFragmentDirections.userToChat(userName, chatId).apply {
            setUserAvatar(userAvatar)
        })
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

    private fun showShare(userId: String) {
        val link = BuildConfig.SHARE_URL + "portal/user/" + userId
        try {
            val shareApp = Intent(Intent.ACTION_SEND)
            shareApp.type = "text/plain"
            shareApp.putExtra(Intent.EXTRA_TEXT, link)
            startActivity(Intent.createChooser(shareApp, "Choose one of the:"))
        } catch (e: Exception) {
            showRequestErrorMessage()
        }
    }

    override fun layout() = R.layout.fragment_user

    companion object {
        private const val HEADER_ITEM_ID = 100L
    }
}

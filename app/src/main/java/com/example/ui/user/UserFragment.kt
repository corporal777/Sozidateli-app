package com.example.ui.user

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.extensions.dp
import com.example.extensions.findItemBy
import com.example.extensions.firstLetterToUppercase
import com.example.extensions.updateGroup
import com.example.extensions.updateItem
import com.example.app.BuildConfig
import com.example.app.R
import com.example.data.models.InterestNew
import com.example.data.models.OrganizationNew
import com.example.data.models.ProfileUserData
import com.example.data.models.UserDetail
import com.example.app.databinding.FragmentUserBinding
import com.example.holders.*
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.image.ImageViewActivityArgs
import com.example.ui.user.items.ProfileDataDividerItem
import com.example.ui.user.items.UserProfileActionsItem
import com.example.ui.views.UserSubscribeButton
import com.example.ui.views.UserSubscribeImageView
import com.example.ui.views.dialogs.DefaultAlertDialog
import com.example.ui.views.toolbar.ToolbarContent
import com.example.ui.views.toolbar.ToolbarIconView
import com.example.util.PHONE_PERSONAL
import com.example.util.PHONE_WORK
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class UserFragment : BaseFragment<FragmentUserBinding>(), UserContract.View, ToolbarFragment {

    private val shareProfileButton by lazy {
        ToolbarIconView(requireContext(), 32).apply {
            setImageAsIcon(R.drawable.ic_share_white)
            setIconTint(R.color.main_brown_color_new)
            initPadding(top = 0, bottom = 0, left = 7.dp, right = 7.dp)
            setOnClickListener { if (!presenter.userId.isNullOrEmpty()) showShare(presenter.userId) }
        }
    }

    private val addToFavoriteButton by lazy {
        UserSubscribeImageView(requireContext()).apply {
            isVisible = false
            setButtonMargins(0, 0, 0, 10.dp)
        }
    }


    @InjectPresenter
    lateinit var presenter: UserPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserPresenter>

    @ProvidePresenter
    fun providePresenter(): UserPresenter = presenterProvider.get().apply {
        val args = UserFragmentArgs.fromBundle(requireArguments())
        userId = args.userId
    }

    private val onOrganizationClickListener: (OrganizationNew) -> Unit = {
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

    private val mainDataSection = Section().apply {
        setPlaceholder(PlaceholderItem(PlaceholderItem.Type.USER_PROFILE))
    }
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            contentList.apply {
                adapter = this@UserFragment.adapter
            }
            mBinding.swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }
    }


    override fun setUser(profileUserData: ProfileUserData) {
        val user = profileUserData.user
        val interests = profileUserData.interests

        mainDataSection.updateItem(initProfileItem(user))
        personalDataSection.updateGroup(initPersonalDataItem(user))
        educationDataSection.updateGroup(initEducationDataItem(user))
        workDataSection.updateGroup(initWorkExperience(user))
        interestsDataSection.updateGroup(initInterests(interests))
        additionalDataSection.updateGroup(initAdditionalInformation(user))

        if (profileUserData.user.state?.isRegistered == true) {
            actionsDataSection.update(
                listOf(
                    ProfileDataDividerItem(),
                    initActions(profileUserData.user.getUserSubscribeAction())
                )
            )
        }

        mBinding.swipeToRefresh.isRefreshing = false
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

    private fun initProfileItem(user: UserDetail): ProfileDataUserItem {
        return ProfileDataUserItem(
            HEADER_ITEM_ID,
            user.loadUserImage(),
            user.nameLastName,
            user.id,
            user.getUserSubscribeAction() ?: UserSubscribeButton.Action.FAVORITE,
            { presenter.onWriteMessageClick() },
            { imageView ->
                val url = user.loadUserImage() ?: return@ProfileDataUserItem
                onAvatarClick(imageView, url)
            }
        )
    }

    private fun initPersonalDataItem(user: UserDetail): Group {
        return ProfileExpandableTitleGroup(
            getString(R.string.profile_title_general_info),
            onExpandChange = onItemExpandChange
        ).apply {
            add(
                ProfileDataPersonalItem(
                    user.binds?.organization,
                    user.email,
                    user.contactInformation.emails,
                    user.phone?.firstOrNull { it.type == PHONE_PERSONAL },
                    user.phone?.firstOrNull { it.type == PHONE_WORK },
                    user.phone?.firstOrNull { it.type == PHONE_PERSONAL }?.isConfirmed ?: false,
                    user.gender,
                    user.birthday,
                    user.address?.shortAddres ?: user.address?.city,
                    user.contactInformation.socialLinks,
                    user.contactInformation.site,
                    user.phone?.firstOrNull { it.type == PHONE_WORK }?.additional,
                    onOrganizationClickListener
                )
            )
        }
    }


    private fun initEducationDataItem(user: UserDetail): Group? {
        val educationLevel =
            presenter.getEducationLevels().firstOrNull { it.id == user.educationLevel?.value }?.name
        val degrees = user.binds?.academicDegree?.filter { it.showInProfile == true } ?: emptyList()
        val education = user.binds?.education?.filter { it.showInProfile == true } ?: emptyList()

        return if (education.isNotEmpty() || !educationLevel.isNullOrEmpty() || degrees.isNotEmpty()) {
            ProfileExpandableTitleGroup(
                getString(R.string.profile_title_education),
                onExpandChange = onItemExpandChange
            ).apply {
                add(Section().apply {
                    if (!educationLevel.isNullOrEmpty() || degrees.isNotEmpty()) setHeader(
                        ProfileDataEducationLevelItem(
                            educationLevel, degrees,
                            presenter.getAcademicDegrees(),
                            presenter.getSpecialities()
                        )
                    )
                    addAll(education.map { ProfileDataEducationItem(it) })
                })
            }
        } else null
    }

    private fun initWorkExperience(user: UserDetail): Group? {
        val work = user.binds?.workExperience?.models?.filter { it.showInProfile == true } ?: emptyList()
        return if (work.isNotEmpty()) {
            ProfileExpandableTitleGroup(
                getString(R.string.profile_work_experience),
                onExpandChange = onItemExpandChange
            ).apply {
                add(Section().apply {
                    if (work.isEmpty()) add(ProfileNoWorkExperienceItem(resources.getString(R.string.no_experience)))
                    addAll(work.map { ProfileDataWorkExperienceItem(it) })
                })
            }
        } else null
    }

    private fun initInterests(interests: Map<InterestNew, List<InterestNew>>?): Group? {
        val nonNullInterests = interests ?: emptyMap()
        return if (nonNullInterests.isNotEmpty()) {
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
                })
            }
        } else null
    }

    private fun initActions(action: UserSubscribeButton.Action?): Group {
        return ProfileExpandableTitleGroup(
            getString(R.string.yet_btn_text).firstLetterToUppercase(),
            onExpandChange = onItemExpandChange
        ).apply {
            add(Section().apply {
//                add(UserProfileActionsItem(getString(R.string.complain_about_user_label)) {
//
//                })
                add(UserProfileActionsItem(action) {
                    if (action == UserSubscribeButton.Action.UNBLOCK) presenter.onUnblockClick()
                    else presenter.onBlockClick()
                })
            })
        }
    }

    private fun initAdditionalInformation(user: UserDetail): Group? {
        val notes = if (user.notes?.showInProfile == true) user.notes?.value else ""
        val files =
            user.binds?.recommendationFile?.filter { it.showInProfile == true } ?: emptyList()

        val subgroups = mutableListOf<Group>().apply {
            if (!notes.isNullOrEmpty()) {
                add(
                    ProfileExpandableSubtitleGroup(
                        getString(R.string.profile_notes),
                        onExpandChange = onItemExpandChange
                    ).apply { add(ProfileDataNotesItem(notes)) })
            }
            if (!files.isNullOrEmpty()) {
                add(
                    ProfileExpandableSubtitleGroup(
                        getString(R.string.profile_files),
                        onExpandChange = onItemExpandChange
                    ).apply {
                        addAll(files.map { file ->
                            ProfileDataFileItem(file.name ?: "file") { presenter.onFileClick(file) }
                        })
                    })
            }

        }

        return if (!notes.isNullOrEmpty() || files.isNotEmpty()) {
            ProfileExpandableTitleGroup(
                getString(R.string.profile_additional_data),
                onExpandChange = onItemExpandChange
            ).apply { addAll(subgroups) }
        } else null
    }

    override fun setSubscribeBlockAction(action: UserSubscribeButton.Action?) {
        mainDataSection.notifyItemChanged(0, action)
        val item = actionsDataSection.findItemBy<UserProfileActionsItem> { x -> x.isAction }
        if (item != null) {
            item.notifyChanged(action)
            item.action = {
                if (action == UserSubscribeButton.Action.UNBLOCK) presenter.onUnblockClick()
                else presenter.onBlockClick()
            }
        }
    }

    override fun setEnableAddToFavoriteButton(enabled: Boolean) {
        addToFavoriteButton.setAlphaVision(enabled)
    }

    override fun setSubscribeFavoriteAction(action: UserSubscribeButton.Action?) {
        addToFavoriteButton.apply {
            if (action != null) {
                isVisible = true
                setAction(action)
                setOnClickListener {
                    if (action == UserSubscribeButton.Action.UNFAVORITE) {
                        presenter.onUnsubscribeClick()
                    } else presenter.onSubscribeClick()
                }
            }
        }
    }


    override fun showUserHiddenDialog() {
        val message = "Данный профиль недоступен"
        DefaultAlertDialog(requireContext(), null, message, withCancel = false)
            .setSelectCallback { findNavController().navigateUp() }
    }

    override fun openChat(userName: String, userAvatar: String?, chatId: String) {
        if (!findNavController().popBackStack(R.id.chat_fragment, false)) {
            findNavController().navigate(UserFragmentDirections.userToChat(userName, chatId).apply {
                setUserAvatar(userAvatar)
            })
        }
    }

    override fun showOrganization(organization: OrganizationNew) {
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
        DefaultAlertDialog(
            context = requireContext(),
            title = null,
            message = getString(R.string.user_ban_confirmation_title),
            positiveText = getString(R.string.yes),
            negativeText = getString(R.string.cancel),
        ).setSelectCallback { presenter.onBlockConfirm() }
    }

    override fun showUpdateError(message: String?) {
        val title = getString(R.string.profile_edit_request_error)
        Toast.makeText(requireContext(), message?.let { "$title: $it" }
            ?: title, Toast.LENGTH_SHORT).show()
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


    companion object {
        private const val HEADER_ITEM_ID = 100L
    }

    override fun layout() = R.layout.fragment_user
    override val title: CharSequence by lazy { getString(R.string.profile_current_user_label) }
    override fun actionIconContainer(view: ViewGroup) {
        view.apply {
            removeAllViews()
            addView(addToFavoriteButton, 0)
            addView(shareProfileButton, 1)
        }
    }

    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}

package com.example.ui.userprofile

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import coil.transform.RoundedCornersTransformation
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserDetail
import com.example.data.models.UserEditDataType
import com.example.databinding.FragmentUserProfileBinding
import com.example.extensions.dp
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragmentNew
import com.example.ui.userprofile.read.interests.UserProfileInterestsFragmentDirections
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.setImage
import setOnClickListener
import javax.inject.Inject
import javax.inject.Provider

class UserProfileFragment : BaseFragmentNew<FragmentUserProfileBinding>(true),
    UserProfileContract.View, ToolbarFragment {


    override fun layout() = R.layout.fragment_user_profile

    @InjectPresenter
    lateinit var presenter: UserProfilePresenter

    @Inject
    lateinit var presenterProvider: Provider<UserProfilePresenter>

    @ProvidePresenter
    fun providePresenter(): UserProfilePresenter = presenterProvider.get()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            btnEdit.setOnClickListener(presenter::onEditAvatarClick)
            btnMainInfo.setOnClickListener(presenter::onMainDataClick)
            btnContacts.setOnClickListener(presenter::onContactsClick)
            btnInterests.setOnClickListener(presenter::onInterestsClick)
            btnEducation.setOnClickListener(presenter::onEducationClick)
            btnExperience.setOnClickListener(presenter::onExperienceClick)
        }
    }

    override fun onUserUpdated(user: UserDetail?, state: String) {
        startPostponedEnterTransition()
        user ?: return
        mBinding.ivAvatar.apply {
            val avatarUrl = user.image?.uri?.takeIf { it.isNotBlank() }
            transitionName = avatarUrl
            setImage(
                avatarUrl,
                error = R.drawable.avatar_placeholder_rectangle,
                transformations = listOf(RoundedCornersTransformation(10f.dp))
            )
        }
    }

    override fun showTakePictureChooser(canRemove: Boolean, isBase: Boolean, isMax: Boolean) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.photo_alert_title)
            .apply {
                if (canRemove) {
                    setNeutralButton(R.string.photo_alert_remove) { _, _ ->
                        showEditWarning(isBase, isMax, false, true) {
                            presenter.onRemovePhotoClick()
                        }
                    }
                }
            }
            .setPositiveButton(R.string.photo_alert_gallery) { _, _ -> presenter.onTakePhotoFromGalleryClick() }
            .setNegativeButton(R.string.photo_alert_camera) { _, _ -> presenter.onTakePhotoFromCameraClick() }
            .show()
    }

    override fun showMainData() {
        findNavController().navigate(UserProfileFragmentDirections.profileToMainData())
    }

    override fun showContacts() {
        findNavController().navigate(UserProfileFragmentDirections.profileToContacts())
    }

    override fun showInterests() {
        findNavController().navigate(UserProfileFragmentDirections.profileToInterests())
    }

    override fun showEducation() {
        findNavController().navigate(UserProfileFragmentDirections.profileToEducation())
    }

    override fun showExperience() {
        findNavController().navigate(UserProfileFragmentDirections.profileToExperience())
    }

    override fun showEdit() {
        findNavController().navigate(UserProfileInterestsFragmentDirections.toEdit(UserEditDataType.INTERESTS))

    }

    override fun showNextScreen() {
        findNavController().navigate(UserProfileInterestsFragmentDirections.toEdit(UserEditDataType.INTERESTS))

    }

    override val title: CharSequence by lazy { getString(R.string.user_profile_label) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: (value: Int) -> Unit) { scroll.invoke(0) }
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}

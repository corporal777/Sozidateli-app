package com.example.ui.userprofile

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import coil.transform.RoundedCornersTransformation
import com.example.R
import com.example.data.models.UserDetail
import com.example.data.models.UserEditDataType
import com.example.databinding.FragmentUserProfileBinding
import com.example.extensions.dp
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.gallery.GalleryBottomSheet
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.setImage
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import setOnClickListener
import javax.inject.Inject
import javax.inject.Provider

class UserProfileFragment : BaseFragment<FragmentUserProfileBinding>(), UserProfileContract.View,
    ToolbarFragment {

    override fun animationType(): AnimType = AnimType.AXIS
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

    override fun setUserData(user: UserDetail, state: String) {
        startPostponedEnterTransition()
        mBinding.apply {
            ivAvatar.setImage(user.loadUserImage(), user.avatarIsDefault ?: true)
            btnEdit.text =
                if (user.loadUserImage().isNullOrEmpty() || user.avatarIsDefault == true)
                    getString(R.string.user_profile_add)
                else getString(R.string.user_profile_edit)
        }

    }

    override fun showTakePictureChooser() {
        GalleryBottomSheet().show(childFragmentManager)
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
        findNavController().navigate(UserProfileFragmentDirections.profileToInterestsEdit())
    }

    override val title: CharSequence by lazy { getString(R.string.user_profile_label) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}

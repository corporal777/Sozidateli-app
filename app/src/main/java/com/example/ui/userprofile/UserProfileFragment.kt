package com.example.ui.userprofile

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.app.R
import com.example.data.models.UserDetail
import com.example.app.databinding.FragmentUserProfileBinding
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.gallery.GalleryBottomSheet
import com.example.ui.views.toolbar.ToolbarContent
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import com.example.extensions.setOnClickListener
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
        findNavController().navigate(R.id.user_profile_main_data_fragment)
    }

    override fun showContacts() {
        findNavController().navigate(R.id.user_profile_contacts_fragment)
    }

    override fun showInterests() {
        findNavController().navigate(R.id.user_profile_interests_fragment)
    }

    override fun showEducation() {
        findNavController().navigate(R.id.user_profile_education_fragment)
    }

    override fun showExperience() {
        findNavController().navigate(R.id.user_profile_experience_fragment)
    }

    override fun showEdit() {
        findNavController().navigate(R.id.editInterestsFragment)
    }

    override val title: CharSequence by lazy { getString(R.string.user_profile_label) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}

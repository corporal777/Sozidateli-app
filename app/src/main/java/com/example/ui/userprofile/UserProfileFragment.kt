package com.example.ui.userprofile

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.user.User
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_user_profile.*
import kotlinx.android.synthetic.main.item_profile_data_current_user.btnEdit
import kotlinx.android.synthetic.main.item_profile_data_current_user.ivAvatar
import setOnClickListener
import javax.inject.Inject
import javax.inject.Provider

class UserProfileFragment : BaseFragment(), UserProfileContract.View, ToolbarFragment {

    override val title: String?
        get() = getString(R.string.user_profile_label)

    override fun layout() = R.layout.fragment_user_profile

    @InjectPresenter
    lateinit var presenter: UserProfilePresenter

    @Inject
    lateinit var presenterProvider: Provider<UserProfilePresenter>

    @ProvidePresenter
    fun providePresenter(): UserProfilePresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnEdit.setOnClickListener(presenter::onEditAvatarClick)
        btnMainInfo.setOnClickListener(presenter::onMainDataClick)
        btnContacts.setOnClickListener(presenter::onContactsClick)
        btnInterests.setOnClickListener(presenter::onInterestsClick)
        btnEducation.setOnClickListener(presenter::onEducationClick)
        btnExperience.setOnClickListener(presenter::onExperienceClick)
    }

    override fun onUserUpdated(user: User?) {
        user ?: return
        ivAvatar.apply {
            val avatarUrl = user.user_avatar?.takeIf { it.isNotBlank() }
            clipToOutline = true
            transitionName = avatarUrl
            Picasso.get()
                    .load(avatarUrl)
                    .placeholder(R.drawable.avatar_placeholder_rectangle)
                    .error(R.drawable.avatar_placeholder_rectangle)
                    .into(this)
        }
    }

    override fun showTakePictureChooser(canRemove: Boolean) {
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.photo_alert_title)
                .apply {
                    if (canRemove) {
                        setNeutralButton(R.string.photo_alert_remove) { _, _ -> presenter.onRemovePhotoClick() }
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
        TODO("Not yet implemented")
    }

    override fun showInterests() {
        findNavController().navigate(UserProfileFragmentDirections.profileToInterests())
    }

    override fun showEducation() {
        findNavController().navigate(UserProfileFragmentDirections.profileToEducation())
    }

    override fun showExperience() {
        TODO("Not yet implemented")
    }
}

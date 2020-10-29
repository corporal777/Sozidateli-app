package com.example.ui.userprofile

import android.os.Bundle
import android.view.View
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

    override fun setUser(user: User?) {
        user ?: return
        ivAvatar.apply {
            val avatarUrl = user.user_avatar
            clipToOutline = true
            transitionName = avatarUrl
            Picasso.get()
                    .load(avatarUrl)
                    .placeholder(R.drawable.avatar_placeholder_rectangle)
                    .into(this)
        }
    }
}

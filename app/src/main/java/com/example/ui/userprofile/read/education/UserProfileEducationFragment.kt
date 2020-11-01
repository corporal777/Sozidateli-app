package com.example.ui.userprofile.read.education

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserEditDataType
import com.example.data.models.user.User
import com.example.holders.ProfileDataEducationItem
import com.example.holders.ProfileDataEducationLevelItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import kotlinx.android.synthetic.main.fragment_user_profile_interests.*
import kotlinx.android.synthetic.main.fragment_user_profile_main_data.btnEdit
import setOnClickListener
import javax.inject.Inject
import javax.inject.Provider

class UserProfileEducationFragment : BaseFragment(), UserProfileEducationContract.View, ToolbarFragment {

    override val title: String?
        get() = getString(R.string.user_profile_education)

    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun layout() = R.layout.fragment_user_profile_interests

    @InjectPresenter
    lateinit var presenter: UserProfileEducationPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserProfileEducationPresenter>

    @ProvidePresenter
    fun providePresenter(): UserProfileEducationPresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvInterests.adapter = adapter
        btnEdit.setOnClickListener(presenter::onEditClick)
    }

    override fun onUserUpdated(user: User?) {
        user ?: return

        val educationLevel = user.user_education
        val academicDegrees = user.academic_degree ?: emptyList()
        val education = user.education ?: emptyList()

        val educationGroup = Section().apply {
            if (!educationLevel.isNullOrEmpty()) setHeader(ProfileDataEducationLevelItem(educationLevel, academicDegrees))
            addAll(education.map { ProfileDataEducationItem(it) })
        }

        adapter.update(listOf(educationGroup))
    }

    override fun showEdit() {
        findNavController().navigate(UserProfileEducationFragmentDirections.toEdit(UserEditDataType.EDUCATION))
    }
}

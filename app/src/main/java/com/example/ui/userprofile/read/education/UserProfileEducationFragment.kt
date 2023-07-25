package com.example.ui.userprofile.read.education

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserDetail
import com.example.databinding.FragmentUserProfileInterestsBinding
import com.example.extensions.updateItem
import com.example.holders.EmptyItem
import com.example.holders.ProfileDataEducationItem
import com.example.holders.ProfileDataEducationLevelItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.toolbar.ToolbarContent
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import setOnClickListener
import javax.inject.Inject
import javax.inject.Provider

class UserProfileEducationFragment : BaseFragment<FragmentUserProfileInterestsBinding>(),
    UserProfileEducationContract.View, ToolbarFragment {

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
        mBinding.apply {
            rvInterests.adapter = adapter
            btnEdit.setOnClickListener(presenter::onEditClick)
        }
    }

    override fun onUserUpdated(user: UserDetail?, state: String) {
        user ?: return
        val educationLevel =
            user.educationLevelList?.firstOrNull { it.id == user.educationLevel?.value }?.name
        val academicDegrees = user.binds?.academicDegree ?: emptyList()
        val education = user.binds?.education ?: emptyList()

        val educationGroup = Section().apply {
            if (educationLevel == null && education.isNullOrEmpty()) {
                updateItem(EmptyItem(getString(R.string.no_education)))
            } else {
                if (educationLevel != null) setHeader(
                    ProfileDataEducationLevelItem(
                        educationLevel, academicDegrees,
                        user.academicDegrees ?: emptyList(), user.speciality ?: emptyList()
                    )
                )
                addAll(education.map { ProfileDataEducationItem(it) })
            }
        }

        adapter.update(listOf(educationGroup))
    }

    override fun showEdit() {
        findNavController().navigate(R.id.editEducationFragment)
    }

    override val title: CharSequence by lazy { getString(R.string.user_profile_education) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: (value: Int) -> Unit) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}

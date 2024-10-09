package com.example.ui.userprofile.read.education

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.app.R
import com.example.data.models.AcademicDegreeModel
import com.example.data.models.EducationModel
import com.example.app.databinding.FragmentUserProfileInterestsBinding
import com.example.extensions.updateGroup
import com.example.extensions.updateItem
import com.example.extensions.updateItems
import com.example.holders.EmptyItem
import com.example.holders.ProfileDataEducationItem
import com.example.holders.ProfileDataEducationLevelItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.toolbar.ToolbarContent
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import com.example.extensions.setOnClickListener
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


    override fun setUserEducation(
        userEducationLevel: String,
        userAcademicDegrees: List<AcademicDegreeModel>,
        userEducation: List<EducationModel>
    ) {

        val educationGroup = Section().apply {
            if (userEducationLevel.isNullOrEmpty() && userEducation.isNullOrEmpty())
                updateItem(EmptyItem(getString(R.string.no_education)))
            else updateItems(
                if (!userEducationLevel.isNullOrEmpty())
                    ProfileDataEducationLevelItem(
                        userEducationLevel,
                        userAcademicDegrees,
                        presenter.getAcademicDegrees(),
                        presenter.getSpecialities()
                    )
                else null,
                userEducation.map { ProfileDataEducationItem(it) }
            )
        }
        adapter.updateGroup(educationGroup)
    }

    override fun showEdit() {
        findNavController().navigate(R.id.editEducationFragment)
    }

    override val title: CharSequence by lazy { getString(R.string.user_profile_education) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}

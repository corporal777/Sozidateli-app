package com.example.ui.userprofile.read.experience

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.app.R
import com.example.data.models.WorkExperience
import com.example.app.databinding.FragmentUserProfileInterestsBinding
import com.example.extensions.updateItem
import com.example.holders.EmptyItem
import com.example.holders.ProfileDataWorkExperienceItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.toolbar.ToolbarContent
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import com.example.extensions.setOnClickListener
import javax.inject.Inject
import javax.inject.Provider

class UserProfileExperienceFragment : BaseFragment<FragmentUserProfileInterestsBinding>(),
    UserProfileExperienceContract.View, ToolbarFragment {

    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun layout() = R.layout.fragment_user_profile_interests

    @InjectPresenter
    lateinit var presenter: UserProfileExperiencePresenter

    @Inject
    lateinit var presenterProvider: Provider<UserProfileExperiencePresenter>

    @ProvidePresenter
    fun providePresenter(): UserProfileExperiencePresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            rvInterests.adapter = adapter
            btnEdit.setOnClickListener(presenter::onEditClick)
        }
    }


    override fun setUserExperience(workExperience: List<WorkExperience>) {
        if (workExperience.isEmpty()) {
            adapter.updateItem(EmptyItem(getString(R.string.no_experience)))
        } else {
            adapter.update(workExperience.map {
                ProfileDataWorkExperienceItem(it)
            })
        }
    }

    override fun showEdit() {
        findNavController().navigate(R.id.editWorksFragment)
    }

    override val title: CharSequence by lazy { getString(R.string.user_profile_experience) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}

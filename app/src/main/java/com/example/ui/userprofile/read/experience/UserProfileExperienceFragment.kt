package com.example.ui.userprofile.read.experience

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserDetail
import com.example.databinding.FragmentUserProfileInterestsBinding
import com.example.holders.EmptyItem
import com.example.holders.ProfileDataWorkExperienceItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragmentNew
import com.example.ui.views.toolbar.ToolbarContent
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import onScrolled
import setOnClickListener
import javax.inject.Inject
import javax.inject.Provider

class UserProfileExperienceFragment : BaseFragmentNew<FragmentUserProfileInterestsBinding>(),
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

    override fun onUserUpdated(user: UserDetail?, state: String) {
        user ?: return

        val work = user.binds?.workExperience?.models ?: emptyList()
        if (work.isEmpty()) {
            adapter.update(
                arrayListOf(
                    EmptyItem(
                        context?.resources?.getString(R.string.no_experience) ?: ""
                    )
                )
            )
        } else {
            adapter.update(work.mapIndexed { index, socialRoles ->
                ProfileDataWorkExperienceItem(socialRoles, index == 0)
            })
        }
    }

    override fun showEdit() {
        findNavController().navigate(R.id.editWorksFragment)
        //findNavController().navigate(UserProfileExperienceFragmentDirections.toEdit(UserEditDataType.WORK))
    }

    override val title: CharSequence by lazy { getString(R.string.user_profile_experience) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: (value: Int) -> Unit) {
        mBinding.rvInterests.apply {
            scroll.invoke(this.computeVerticalScrollOffset())
            onScrolled { _, _ -> scroll.invoke(this.computeVerticalScrollOffset()) }
        }
    }
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}

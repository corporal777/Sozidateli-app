package com.example.ui.userprofile.read.experience

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserEditDataType
import com.example.data.models.user.User
import com.example.holders.EmptyItem
import com.example.holders.ProfileDataWorkExperienceItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_user_profile_interests.*
import kotlinx.android.synthetic.main.fragment_user_profile_main_data.btnEdit
import setOnClickListener
import javax.inject.Inject
import javax.inject.Provider

class UserProfileExperienceFragment : BaseFragment(), UserProfileExperienceContract.View, ToolbarFragment {

    override val title: String?
        get() = getString(R.string.user_profile_experience)

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
        rvInterests.adapter = adapter
        btnEdit.setOnClickListener(presenter::onEditClick)
    }

    override fun onUserUpdated(user: User?) {
        user ?: return

        val work = user.work ?: emptyList()
        if (work.isEmpty()) {
            adapter.update(arrayListOf(EmptyItem(context?.resources?.getString(R.string.no_experience)?: "")))
        } else {
            adapter.update(work.mapIndexed { index, socialRoles ->
                ProfileDataWorkExperienceItem(socialRoles, index == 0)
            })
        }
    }

    override fun showEdit() {
        findNavController().navigate(UserProfileExperienceFragmentDirections.toEdit(UserEditDataType.WORK))
    }
}

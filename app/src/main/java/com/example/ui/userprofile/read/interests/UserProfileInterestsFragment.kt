package com.example.ui.userprofile.read.interests

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Interest
import com.example.data.models.InterestNew
import com.example.data.models.UserEditDataType
import com.example.holders.OnExpandChange
import com.example.holders.ProfileDataInterestItem
import com.example.holders.ProfileExpandableSubtitleGroup
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_user_profile_interests.*
import kotlinx.android.synthetic.main.fragment_user_profile_main_data.btnEdit
import setOnClickListener
import javax.inject.Inject
import javax.inject.Provider

class UserProfileInterestsFragment : BaseFragment(), UserProfileInterestsContract.View, ToolbarFragment {

    override val title: String?
        get() = getString(R.string.user_profile_interests)

    private val adapter = GroupAdapter<GroupieViewHolder>()

    private val onItemExpandChange: OnExpandChange<*> = {
        if (it.isExpanded) {
            val position = adapter.getAdapterPosition(it.titleItem)
            (rvInterests.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, 0)
        }
    }

    override fun layout() = R.layout.fragment_user_profile_interests

    @InjectPresenter
    lateinit var presenter: UserProfileInterestsPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserProfileInterestsPresenter>

    @ProvidePresenter
    fun providePresenter(): UserProfileInterestsPresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvInterests.adapter = adapter
        btnEdit.setOnClickListener(presenter::onEditClick)
    }

    override fun onInterestsUpdated(interests: Map<InterestNew, List<InterestNew>>) {
        val items = interests.map {
            val parent = it.key
            val childList = it.value
            ProfileExpandableSubtitleGroup(parent.name?: "", onExpandChange = onItemExpandChange).apply {
                addAll(childList.map { interest -> ProfileDataInterestItem(interest) })
            }
        }

        adapter.update(items)
    }

    override fun showNextScreen() {
        findNavController().navigate(UserProfileInterestsFragmentDirections.toEdit(UserEditDataType.INTERESTS))
    }

    override fun showEdit() {
        findNavController().navigate(UserProfileInterestsFragmentDirections.toEdit(UserEditDataType.INTERESTS))
    }
}

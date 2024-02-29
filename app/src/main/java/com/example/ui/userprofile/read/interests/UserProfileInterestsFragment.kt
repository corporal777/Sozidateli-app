package com.example.ui.userprofile.read.interests

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.R
import com.example.data.models.InterestNew
import com.example.data.models.UserEditDataType
import com.example.databinding.FragmentUserProfileInterestsBinding
import com.example.holders.OnExpandChange
import com.example.holders.PlaceholderItem
import com.example.holders.ProfileDataInterestItem
import com.example.holders.ProfileExpandableSubtitleGroup
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.toolbar.ToolbarContent
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import setOnClickListener
import javax.inject.Inject
import javax.inject.Provider

class UserProfileInterestsFragment : BaseFragment<FragmentUserProfileInterestsBinding>(),
    UserProfileInterestsContract.View, ToolbarFragment {

    override fun layout() = R.layout.fragment_user_profile_interests

    @InjectPresenter
    lateinit var presenter: UserProfileInterestsPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserProfileInterestsPresenter>

    @ProvidePresenter
    fun providePresenter(): UserProfileInterestsPresenter = presenterProvider.get()

    private val adapter = GroupAdapter<GroupieViewHolder>()

    private val onItemExpandChange: OnExpandChange<*> = {
        if (it.isExpanded) {
            val position = adapter.getAdapterPosition(it.titleItem)
            (mBinding.rvInterests.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                position,
                0
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            rvInterests.adapter = adapter
            btnEdit.setOnClickListener(presenter::onEditClick)
        }
    }

    override fun showInterestsPlaceholder() {
        adapter.update(List(7) { PlaceholderItem(PlaceholderItem.Type.INTERESTS) })
    }

    override fun onInterestsUpdated(interests: Map<InterestNew, List<InterestNew>>) {
        if (interests.isNullOrEmpty()) findNavController().navigateUp()
        else {
            val items = interests.map {
                val parent = it.key
                val childList = it.value
                ProfileExpandableSubtitleGroup(
                    parent.name ?: "",
                    onExpandChange = onItemExpandChange
                ).apply {
                    addAll(childList.map { interest -> ProfileDataInterestItem(interest) })
                }
            }
            adapter.update(items)
        }
    }

    override fun showEdit() {
        findNavController().navigate(R.id.editInterestsFragment)
    }

    override val title: CharSequence by lazy { getString(R.string.user_profile_interests) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}

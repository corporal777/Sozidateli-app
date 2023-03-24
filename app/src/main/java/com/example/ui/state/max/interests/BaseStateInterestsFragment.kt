package com.example.ui.state.max.interests

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.InterestNew
import com.example.data.models.UserInterest
import com.example.databinding.FragmentBaseStateInterestsBinding
import com.example.holders.OnExpandChange
import com.example.holders.ProfileDataInterestEditItem
import com.example.holders.ProfileExpandableSubtitleGroup
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragmentNew
import com.example.ui.state.base.MainInfoFragmentArgs
import com.example.ui.state.max.MaxStateScreenType
import com.example.ui.views.dialogs_new.MessageDialogWithBrownButton
import com.example.ui.views.toolbar.ToolbarContent
import com.example.ui.views.toolbar.ToolbarIconView
import com.example.util.Utils
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import onScrolled
import javax.inject.Inject
import javax.inject.Provider

class BaseStateInterestsFragment : BaseFragmentNew<FragmentBaseStateInterestsBinding>(),
    BaseStateInterestsContract.View, ToolbarFragment {

    override fun layout(): Int = R.layout.fragment_base_state_interests

    @InjectPresenter
    lateinit var presenter: BaseStateInterestsPresenter

    @Inject
    lateinit var presenterProvider: Provider<BaseStateInterestsPresenter>

    @ProvidePresenter
    fun providePresenter(): BaseStateInterestsPresenter = presenterProvider.get().apply {
        screen = MainInfoFragmentArgs.fromBundle(requireArguments()).screen
    }

    private var onSaveClick: (() -> Unit)? = null

    private val onItemExpandChange: OnExpandChange<*> = {
        if (it.isExpanded) {
            val position = adapter.getAdapterPosition(it.titleItem)
            (mBinding.recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                position,
                0
            )
        }
    }

    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.recyclerView.apply {
            adapter = this@BaseStateInterestsFragment.adapter
        }

        mBinding.btnSave.setOnClickListener { onSaveClick?.invoke() }
    }

    override fun setInterestsData(interests: Map<InterestNew, List<UserInterest>>) {
        val findUserInterests: () -> List<InterestNew> = {
            interests.values.flatten().filter { item -> item.isUserInterest }
                .map { item -> item.interest }
        }

        var userInterests = findUserInterests()
        mBinding.btnSave.isEnabled = userInterests.isNotEmpty()
        adapter.update(interests.map {
            val parent = it.key
            val childList = it.value
            ProfileExpandableSubtitleGroup(
                parent.name ?: "",
                onExpandChange = onItemExpandChange
            ).apply {
                titleItem.badgeCount = childList.count { child -> child.isUserInterest }
                val interestsItems = childList.mapIndexed { index, interest ->
                    ProfileDataInterestEditItem(interest, index != childList.size - 1) {
                        userInterests = findUserInterests()
                        val count = childList.count { child -> child.isUserInterest }
                        titleItem.apply {
                            badgeCount = count
                            notifyChanged(count)
                            mBinding.btnSave.isEnabled = userInterests.isNotEmpty()
                        }
                    }
                }
                addAll(interestsItems)
            }
        })

        onSaveClick = { presenter.onSaveInterestsClick(userInterests) }
    }

    override fun goToNext() {
        //findNavController().navigate(BaseStateInterestsFragmentDirections.actionBaseStateInterestsFragmentToMaxStateWorkFragment().setScreen(presenter.screen))
        when (Utils.maxStateScreen(presenter.getUserData())) {
            MaxStateScreenType.EDUCATION ->
                findNavController().navigate(
                    BaseStateInterestsFragmentDirections.actionBaseStateInterestsFragmentToMaxStateEducationFragment()
                        .setScreen(presenter.screen)
                )
            MaxStateScreenType.DONE -> MessageDialogWithBrownButton(
                requireContext(),
                getString(R.string.you_got_max_state)
            )
                .setSelectCallback {
                    when (presenter.screen) {
                        1 -> findNavController().popBackStack(R.id.profile_fragment, false)
                        2 -> findNavController().popBackStack(R.id.userStateFragment, false)
                    }
                }
            else ->
                findNavController().navigate(
                    BaseStateInterestsFragmentDirections.actionBaseStateInterestsFragmentToMaxStateWorkFragment()
                        .setScreen(presenter.screen)
                )
        }
    }

    override fun showUpdateError(message: String?) {
        val title = getString(R.string.profile_edit_request_error)
        Toast.makeText(requireContext(), message?.let { "$title: $it" }
            ?: title, Toast.LENGTH_SHORT).show()
    }

    override fun setClickClose(type: Int) {
        when (presenter.screen) {
            1 -> findNavController().popBackStack(R.id.profile_fragment, false)
            2 -> findNavController().popBackStack(R.id.userStateFragment, false)
            else -> navigateUp()
        }
    }


    override val title: CharSequence by lazy { getString(R.string.profile_interests) }
    override fun actionIconContainer(view: ViewGroup) {
        view.apply {
            addView(ToolbarIconView(context, 40).apply {
                setImageAsIcon(R.drawable.ic_close_new)
                setOnClickListener { presenter.onClickClose() }
            })
        }
    }

    override fun scrollValue(scroll: (value: Int) -> Unit) {
        mBinding.recyclerView.apply {
            scroll.invoke(this.computeVerticalScrollOffset())
            onScrolled { _, _ ->
                scroll.invoke(this.computeVerticalScrollOffset())
            }
        }
    }

    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}
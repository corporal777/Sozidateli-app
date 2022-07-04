package com.example.ui.state.max.interests

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.InterestNew
import com.example.data.models.UserInterest
import com.example.holders.OnExpandChange
import com.example.holders.ProfileDataInterestEditItem
import com.example.holders.ProfileExpandableSubtitleGroup
import com.example.ui.base.BaseFragment
import com.example.ui.state.base.MainInfoFragmentArgs
import com.example.ui.state.max.MaxStateScreenType
import com.example.ui.views.dialogs_new.MessageDialogWithBrownButton
import com.example.util.Utils
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_register_email.*
import kotlinx.android.synthetic.main.fragment_user_edit.*
import javax.inject.Inject
import javax.inject.Provider

class BaseStateInterestsFragment: BaseFragment(), BaseStateInterestsContract.View {

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
            (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, 0)
        }
    }

    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                hideKeyboard()
                navigateUp()
            }
        })
        ivClose.setOnClickListener {
            when (presenter.screen) {
                1 -> findNavController().popBackStack(R.id.profile_fragment, false)
                2 -> findNavController().popBackStack(R.id.userStateFragment, false)
            }
        }
        recyclerView.apply {
            adapter = this@BaseStateInterestsFragment.adapter
        }

        btnSave.setOnClickListener { onSaveClick?.invoke() }
    }

    override fun setInterestsData(interests: Map<InterestNew, List<UserInterest>>) {
        val findUserInterests: () -> List<InterestNew> = {
            interests.values.flatten().filter { item -> item.isUserInterest }
                    .map { item -> item.interest }
        }

        var userInterests = findUserInterests()
        btnSave.isEnabled = userInterests.isNotEmpty()
        adapter.update(interests.map {
            val parent = it.key
            val childList = it.value
            ProfileExpandableSubtitleGroup(parent.name?: "", onExpandChange = onItemExpandChange).apply {
                titleItem.badgeCount = childList.count { child -> child.isUserInterest }
                val interestsItems = childList.mapIndexed { index, interest ->
                    ProfileDataInterestEditItem(interest, index != childList.size - 1) {
                        userInterests = findUserInterests()
                        val count = childList.count { child -> child.isUserInterest }
                        titleItem.apply {
                            badgeCount = count
                            notifyChanged(count)
                            btnSave.isEnabled = userInterests.isNotEmpty()
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
                findNavController().navigate(BaseStateInterestsFragmentDirections.actionBaseStateInterestsFragmentToMaxStateEducationFragment().setScreen(presenter.screen))
            MaxStateScreenType.DONE -> MessageDialogWithBrownButton(requireContext(), getString(R.string.you_got_max_state))
                    .setSelectCallback {
                        when (presenter.screen) {
                            1 -> findNavController().popBackStack(R.id.profile_fragment, false)
                            2 -> findNavController().popBackStack(R.id.userStateFragment, false)
                        }
                    }
            else ->
                findNavController().navigate(BaseStateInterestsFragmentDirections.actionBaseStateInterestsFragmentToMaxStateWorkFragment().setScreen(presenter.screen))
        }
    }

    override fun showUpdateError(message: String?) {
        val title = getString(R.string.profile_edit_request_error)
        Toast.makeText(requireContext(), message?.let { "$title: $it" }
                ?: title, Toast.LENGTH_SHORT).show()
    }
}
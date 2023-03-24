package com.example.ui.state.max.work

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserDetail
import com.example.databinding.FragmentMaxStateWorkBinding
import com.example.holders.ProfileDataWorkEditGroup
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragmentNew
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

class MaxStateWorkFragment : BaseFragmentNew<FragmentMaxStateWorkBinding>(),
    MaxStateWorkContract.View, ToolbarFragment {

    override fun layout(): Int = R.layout.fragment_max_state_work

    @InjectPresenter
    lateinit var presenter: MaxStateWorkPresenter

    @Inject
    lateinit var presenterProvider: Provider<MaxStateWorkPresenter>

    @ProvidePresenter
    fun providePresenter(): MaxStateWorkPresenter = presenterProvider.get().apply {
        screen = MaxStateWorkFragmentArgs.fromBundle(requireArguments()).screen
    }

    private val adapter = GroupAdapter<GroupieViewHolder>()

    private var onSaveClick: (() -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.recyclerView.apply {
            adapter = this@MaxStateWorkFragment.adapter
        }

        mBinding.btnSave.setOnClickListener { onSaveClick?.invoke() }
        mBinding.btnSave.isEnabled = false
    }

    override fun setWorkData(user: UserDetail) {
        val work = user.binds?.workExperience
        val dataItem = ProfileDataWorkEditGroup(
            requireContext(),
            user.birthday,
            work,
            { /*presenter.onSaveWorkClick(mutableMapOf(User.FIELD_USER_HAS_WORK_EXPERIENCE to it))*/ },
            { isEnable -> mBinding.btnSave.isEnabled = isEnable })
        adapter.update(listOf(dataItem))
        onSaveClick = {
            if (dataItem.checkDataValid()) {
                presenter.onSaveWorkClick(dataItem.getDataToSave())
            }
        }
    }

    override fun goToNext() {
        //findNavController().navigate(MaxStateWorkFragmentDirections.actionMaxStateWorkFragmentToMaxStateEducationFragment().setScreen(presenter.screen))
        when (Utils.maxStateScreen(presenter.getUserData())) {
            MaxStateScreenType.DONE -> MessageDialogWithBrownButton(
                requireContext(),
                getString(R.string.you_got_max_state)
            ).setSelectCallback {
                when (presenter.screen) {
                    1 -> findNavController().popBackStack(R.id.profile_fragment, false)
                    2 -> findNavController().popBackStack(R.id.userStateFragment, false)
                }
            }
            else ->
                findNavController().navigate(
                    MaxStateWorkFragmentDirections.actionMaxStateWorkFragmentToMaxStateEducationFragment()
                        .setScreen(presenter.screen)
                )
        }
    }


    override fun setClickClose(type: Int) {
        when (presenter.screen) {
            1 -> findNavController().popBackStack(R.id.profile_fragment, false)
            2 -> findNavController().popBackStack(R.id.userStateFragment, false)
            else -> navigateUp()
        }
    }

    override fun showUpdateError(message: String?) {
        val title = getString(R.string.profile_edit_request_error)
        Toast.makeText(requireContext(), message?.let { "$title: $it" }
            ?: title, Toast.LENGTH_SHORT).show()
    }

    override val title: CharSequence by lazy { getString(R.string.profile_work_experience) }
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
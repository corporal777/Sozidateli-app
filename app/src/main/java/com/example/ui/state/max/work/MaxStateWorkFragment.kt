package com.example.ui.state.max.work

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserDetail
import com.example.holders.ProfileDataWorkEditGroup
import com.example.ui.base.BaseFragment
import com.example.ui.state.max.MaxStateScreenType
import com.example.ui.views.dialogs_new.MessageDialogWithBrownButton
import com.example.util.Utils
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_register_email.*
import kotlinx.android.synthetic.main.fragment_user_edit.*
import javax.inject.Inject
import javax.inject.Provider

class MaxStateWorkFragment: BaseFragment(), MaxStateWorkContract.View {

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
            adapter = this@MaxStateWorkFragment.adapter
        }

        btnSave.setOnClickListener { onSaveClick?.invoke() }
        btnSave.isEnabled = false
    }

    override fun setWorkData(user: UserDetail) {
        val work = user.binds?.workExperience
        val dataItem = ProfileDataWorkEditGroup(
                requireContext(),
                user.birthday,
                work,
        { /*presenter.onSaveWorkClick(mutableMapOf(User.FIELD_USER_HAS_WORK_EXPERIENCE to it))*/ },
                { isEnable -> btnSave.isEnabled = isEnable })
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
            MaxStateScreenType.DONE ->MessageDialogWithBrownButton(requireContext(), getString(R.string.you_got_max_state)).setSelectCallback {
                when (presenter.screen) {
                    1 -> findNavController().popBackStack(R.id.profile_fragment, false)
                    2 -> findNavController().popBackStack(R.id.userStateFragment, false)
                }
            }
            else ->
                findNavController().navigate(MaxStateWorkFragmentDirections.actionMaxStateWorkFragmentToMaxStateEducationFragment().setScreen(presenter.screen))
        }
    }

    override fun showUpdateError(message: String?) {
        val title = getString(R.string.profile_edit_request_error)
        Toast.makeText(requireContext(), message?.let { "$title: $it" }
                ?: title, Toast.LENGTH_SHORT).show()
    }
}
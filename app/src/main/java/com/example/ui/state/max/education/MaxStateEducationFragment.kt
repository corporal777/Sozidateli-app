package com.example.ui.state.max.education

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserDetail
import com.example.extensions.showChangeEmailCompleteDialog
import com.example.holders.ProfileDataEducationEditGroupNew
import com.example.ui.base.BaseFragment
import com.example.ui.state.max.work.MaxStateWorkFragmentArgs
import com.example.ui.views.AddPhoneEmailDialog
import com.example.ui.views.BaseStateDialog
import com.example.ui.views.RegisterDataType
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_register_email.*
import kotlinx.android.synthetic.main.fragment_user_edit.*
import javax.inject.Inject
import javax.inject.Provider

class MaxStateEducationFragment: BaseFragment(), MaxStateEducationContract.View {

    override fun layout(): Int = R.layout.fragment_max_state_education
    private lateinit var dialog: AddPhoneEmailDialog

    @InjectPresenter
    lateinit var presenter: MaxStateEducationPresenter

    @Inject
    lateinit var presenterProvider: Provider<MaxStateEducationPresenter>

    @ProvidePresenter
    fun providePresenter(): MaxStateEducationPresenter = presenterProvider.get().apply {
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
            adapter = this@MaxStateEducationFragment.adapter
        }

        btnSave.setOnClickListener { onSaveClick?.invoke() }
        btnSave.isEnabled = false
    }

    override fun setEducationData(user: UserDetail) {
        val academicDegree = if (user.binds?.academicDegree?.size == 1 && user.binds?.academicDegree?.get(0)?.degree == null)
            null else user.binds?.academicDegree
        val dataItem = ProfileDataEducationEditGroupNew(
                requireContext(),
                user.birthday,
                user.educationLevel,
                user.educationLevelList ?: emptyList(),
                user.academicDegrees ?: emptyList(),
                user.speciality ?: emptyList(),
                user.binds?.education ?: emptyList(),
                academicDegree ?: emptyList()
        ) { isEnable -> btnSave.isEnabled = isEnable }
        adapter.update(listOf(dataItem))

        onSaveClick = {
            if (dataItem.checkDataValid()) {
                presenter.onSaveEducationClick(
                        dataItem.getEducationLevelToSave(),
                        dataItem.getEducationsToSave(),
                        dataItem.getDegreeToSave())
            }
        }
    }

    override fun goToNext() {
        if (presenter.getEmail()?.value != null && presenter.getEmail()?.isConfirmed != null) {
            maxActionWithSuccess()
        } else {
            dialog = AddPhoneEmailDialog(requireActivity(), RegisterDataType.EMAIL)
                    .setSelectCallback {
                        presenter.sendEmail(it.value)
                    }.setNegativeClickCallback { maxActions() }
        }
    }

    private fun maxActionWithSuccess() {
        BaseStateDialog(resources.getString(R.string.you_got_max_state), requireActivity())
                .setSelectCallback {
                    maxActions()
                }
    }

    private fun maxActions() {
        /*BaseStateDialog(resources.getString(R.string.you_got_max_state), requireActivity())
                .setSelectCallback {*/
                    when (presenter.screen) {
                        1 -> findNavController().popBackStack(R.id.profile_fragment, false)
                        2 -> findNavController().popBackStack(R.id.userStateFragment, false)
                    }
                //}
    }

    override fun showChangeEmailComplete(email: String) {
        dialog.hideDialog()
        showChangeEmailCompleteDialog(email)
        maxActions()
    }

    override fun showUpdateError(message: String?) {
        val title = getString(R.string.profile_edit_request_error)
        Toast.makeText(requireContext(), message?.let { "$title: $it" }
                ?: title, Toast.LENGTH_SHORT).show()
    }
}
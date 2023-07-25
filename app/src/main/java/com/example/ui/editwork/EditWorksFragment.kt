package com.example.ui.editwork

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserDetail
import com.example.databinding.FragmentEditWorkFragmentBinding
import com.example.extensions.findGroupBy
import com.example.extensions.updateGroup
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.editwork.items.UserWorksGroup
import com.example.ui.views.toolbar.ToolbarContent
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import javax.inject.Inject
import javax.inject.Provider

class EditWorksFragment : BaseFragment<FragmentEditWorkFragmentBinding>(),
    EditWorksContract.View, ToolbarFragment {


    override fun layout(): Int = R.layout.fragment_edit_work_fragment

    @InjectPresenter
    lateinit var presenter: EditWorksPresenter

    @Inject
    lateinit var presenterProvider: Provider<EditWorksPresenter>

    @ProvidePresenter
    fun providePresenter(): EditWorksPresenter = presenterProvider.get()

    private val contentSection = Section()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>().apply {
        add(contentSection)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            rvInterests.apply {
                adapter = this@EditWorksFragment.groupAdapter
            }
            btnEdit.setOnClickListener {
                saveData()
            }
        }
    }

    override fun setWorkData(user: UserDetail) {
        val work = user.binds?.workExperience
        val dataItem = UserWorksGroup(
            requireContext(),
            user.birthday,
            work
        ) { isEnable -> buttonSaveEnabled(isEnable) }

        contentSection.updateGroup(dataItem)
    }

    private fun saveData() {
        val group = contentSection.findGroupBy<UserWorksGroup> { true }
        if (group != null && group.checkDataValid()) {
            presenter.onSaveWorkClick(group.getDataToSave())
        }
    }

    override fun buttonSaveEnabled(enable: Boolean) {
        mBinding.btnEdit.isEnabled = enable
    }


    override fun showUpdateError(message: String?) {
        val title = getString(R.string.profile_edit_request_error)
        Toast.makeText(requireContext(), message?.let { "$title: $it" }
            ?: title, Toast.LENGTH_SHORT).show()
    }

    override val title: CharSequence by lazy { getString(R.string.profile_work_experience) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: (value: Int) -> Unit) {}

    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}
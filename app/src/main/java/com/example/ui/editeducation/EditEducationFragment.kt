package com.example.ui.editeducation

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.example.R
import com.example.data.models.UserDetail
import com.example.databinding.FragmentEditEducationFragmentBinding
import com.example.extensions.findGroupBy
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.editeducation.items.UserEducationGroup
import com.example.ui.editeducation.items.UserEducationLevelGroup
import com.example.ui.views.toolbar.ToolbarContent
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class EditEducationFragment : BaseFragment<FragmentEditEducationFragmentBinding>(),
    EditEducationContract.View,
    ToolbarFragment {

    @InjectPresenter
    lateinit var presenter: EditEducationPresenter

    @Inject
    lateinit var presenterProvider: Provider<EditEducationPresenter>

    @ProvidePresenter
    fun providePresenter(): EditEducationPresenter = presenterProvider.get()


    private val contentSection = Section()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>().apply {
        add(contentSection)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            rvInterests.apply {
                adapter = groupAdapter
            }
            btnSave.setOnClickListener { saveData() }
        }

    }

    override fun setEducationData(user: UserDetail) {
        val academicDegree =
            if (user.binds?.academicDegree?.size == 1 && user.binds?.academicDegree?.get(0)?.degree == null)
                null else user.binds?.academicDegree


        val levelItem = UserEducationLevelGroup(
            requireContext(),
            user.educationLevel,
            user.educationLevelList ?: emptyList(),
            user.academicDegrees ?: emptyList(),
            user.speciality ?: emptyList(),
            academicDegree ?: emptyList()
        )

        val education = UserEducationGroup(
            requireContext(),
            user.birthday,
            user.binds?.education ?: emptyList()
        ).apply { setSelectedLevel(levelItem.getSelectedLevel()) }

        levelItem.selectedLevel = { education.setSelectedLevel(it) }
        levelItem.enableNextButton = { buttonSaveEnabled(it && education.isDataValid()) }
        education.enableNextButton = { buttonSaveEnabled(levelItem.isDataValid() && it) }

        contentSection.update(listOf(levelItem, education))
    }

    private fun saveData() {
        val levelItem = contentSection.findGroupBy<UserEducationLevelGroup> { true }
        val educationItem = contentSection.findGroupBy<UserEducationGroup> { true }
        if (levelItem != null && educationItem != null) {
            if (levelItem.checkDataValid() && educationItem.checkDataValid()){
                presenter.onSaveEducationClick(
                    levelItem.getEducationLevelToSave(),
                    educationItem.getEducationsToSave(),
                    levelItem.getDegreeToSave()
                )
            }
        }
    }

    override fun buttonSaveEnabled(enable: Boolean) {
        mBinding.btnSave.isEnabled = enable
    }

    override fun showCustomLoading() {
        mBinding.apply { btnSave.showProgressLoading(true) }
    }

    override fun hideCustomLoading(){
        mBinding.apply { btnSave.showProgressLoading(false) }
    }

    override fun layout(): Int = R.layout.fragment_edit_education_fragment
    override val title: CharSequence by lazy { getString(R.string.profile_title_education) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: (value: Int) -> Unit) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}
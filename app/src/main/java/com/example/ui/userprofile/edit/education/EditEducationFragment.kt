package com.example.ui.userprofile.edit.education

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.example.app.R
import com.example.data.models.UserDetail
import com.example.app.databinding.FragmentEditEducationFragmentBinding
import com.example.extensions.findGroupBy
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.userprofile.base.BaseUserProfileEditFragment
import com.example.ui.userprofile.edit.education.items.UserEducationGroup
import com.example.ui.userprofile.edit.education.items.UserEducationLevelGroup
import com.example.ui.views.toolbar.ToolbarContent
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class EditEducationFragment : BaseUserProfileEditFragment(), EditEducationContract.View {

    @InjectPresenter
    lateinit var presenter: EditEducationPresenter

    @Inject
    lateinit var presenterProvider: Provider<EditEducationPresenter>

    @ProvidePresenter
    fun providePresenter(): EditEducationPresenter = presenterProvider.get()


    override fun setEducationData(user: UserDetail) {
        val academicDegree =
            if (user.binds?.academicDegree?.size == 1 && user.binds?.academicDegree?.get(0)?.degree == null)
                null else user.binds?.academicDegree

        val levelItem = UserEducationLevelGroup(
            requireContext(),
            user.educationLevel,
            presenter.getEducationLevels(),
            presenter.getAcademicDegrees(),
            presenter.getSpecialities(),
            academicDegree ?: emptyList()
        )

        val educationItem = UserEducationGroup(
            requireContext(),
            user.birthday,
            user.binds?.education ?: emptyList()
        ).apply { setSelectedLevel(levelItem.getSelectedLevel()) }

        levelItem.selectedLevel = { educationItem.setSelectedLevel(it) }
        levelItem.enableNextButton = { buttonSaveEnabled(it && educationItem.isDataValid()) }
        educationItem.enableNextButton = { buttonSaveEnabled(levelItem.isDataValid() && it) }

        onSaveClick = {
            if (levelItem.checkDataValid() && educationItem.checkDataValid()){
                presenter.onSaveEducationClick(
                    levelItem.getEducationLevelToSave(),
                    educationItem.getEducationsToSave(),
                    levelItem.getDegreeToSave()
                )
            }
        }

        groupAdapter.update(listOf(levelItem, educationItem))
    }


    override val title: CharSequence by lazy { getString(R.string.profile_title_education) }
}
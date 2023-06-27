package com.example.ui.state.maxNew.education

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserDetail
import com.example.ui.editeducation.items.UserEducationGroup
import com.example.ui.editeducation.items.UserEducationLevelGroup
import com.example.ui.state.maxNew.base.BaseMaxStateFragment
import com.example.ui.state.maxNew.work.MaxStatusWorkFragmentArgs
import javax.inject.Inject
import javax.inject.Provider

class MaxStatusEducationFragment : BaseMaxStateFragment<MaxStatusEducationPresenter>(),
    MaxStatusEducationContract.View {

    override val title: CharSequence by lazy { getString(R.string.profile_title_education) }

    @InjectPresenter
    override lateinit var presenter: MaxStatusEducationPresenter

    @Inject
    lateinit var presenterProvider: Provider<MaxStatusEducationPresenter>

    @ProvidePresenter
    fun providePresenter(): MaxStatusEducationPresenter = presenterProvider.get().apply {
        screen = MaxStatusWorkFragmentArgs.fromBundle(requireArguments()).screen
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

        val educationItem = UserEducationGroup(
            requireContext(),
            user.birthday,
            user.binds?.education ?: emptyList()
        ).apply { setSelectedLevel(levelItem.getSelectedLevel()) }

        levelItem.selectedLevel = { educationItem.setSelectedLevel(it) }
        levelItem.enableNextButton = { buttonNextEnabled(it && educationItem.isDataValid()) }
        educationItem.enableNextButton = { buttonNextEnabled(levelItem.isDataValid() && it) }

        contentSection.update(listOf(levelItem, educationItem))

        onSaveClick = {
            if (levelItem.checkDataValid() && educationItem.checkDataValid()){
                presenter.onSaveEducationClick(
                    levelItem.getEducationLevelToSave(),
                    educationItem.getEducationsToSave(),
                    levelItem.getDegreeToSave()
                )
            }
        }
    }
}
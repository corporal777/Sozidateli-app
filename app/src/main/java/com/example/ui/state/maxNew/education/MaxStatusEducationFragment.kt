package com.example.ui.state.maxNew.education

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserDetail
import com.example.extensions.updateGroup
import com.example.holders.ProfileDataEducationEditGroupNew
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

        val dataItem = ProfileDataEducationEditGroupNew(
            requireContext(),
            user.birthday,
            user.educationLevel,
            user.educationLevelList ?: emptyList(),
            user.academicDegrees ?: emptyList(),
            user.speciality ?: emptyList(),
            user.binds?.education ?: emptyList(),
            academicDegree ?: emptyList()
        ) { isEnable -> buttonNextEnabled(isEnable) }

        contentSection.updateGroup(dataItem)

        onSaveClick = {
            if (dataItem.checkDataValid()) {
                presenter.onSaveEducationClick(
                    dataItem.getEducationLevelToSave(),
                    dataItem.getEducationsToSave(),
                    dataItem.getDegreeToSave()
                )
            }
        }
    }
}
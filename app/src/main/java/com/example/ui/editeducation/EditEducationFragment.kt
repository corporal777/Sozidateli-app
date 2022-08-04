package com.example.ui.editeducation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.AcademicDegreeModel
import com.example.data.models.EducationModel
import com.example.data.models.ToggleIntModel
import com.example.data.models.UserDetail
import com.example.databinding.FragmentEditEducationFragmentBinding
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.parseToDate
import com.example.ui.base.BaseFragmentNew
import com.example.ui.editeducation.EditEducationModel.Companion.ADD_EDUCATION
import com.example.ui.editeducation.EditEducationModel.Companion.ADD_HIGHT_LEVEL
import com.example.ui.editeducation.EditEducationModel.Companion.EDUCATION_ITEM
import com.example.ui.views.toolbar.SimpleTitleToolbar
import onScrolled
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class EditEducationFragment : BaseFragmentNew<FragmentEditEducationFragmentBinding>(),
    EditEducationContract.View, SimpleTitleToolbar {

    private lateinit var adapter: EditEducationAdapter
    private var birthday: Date? = null
    private var academicDegrees: MutableList<EducationLevelNew>? = mutableListOf()
    private var speciality: MutableList<EducationLevelNew>? = mutableListOf()
    private var hiddenAcademicDegrees: List<EditEducationModel>? = null

    override fun layout(): Int = R.layout.fragment_edit_education_fragment

    @InjectPresenter
    lateinit var presenter: EditEducationPresenter

    @Inject
    lateinit var presenterProvider: Provider<EditEducationPresenter>

    @ProvidePresenter
    fun providePresenter(): EditEducationPresenter = presenterProvider.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = EditEducationAdapter({ addDegree() }, { addEducation() }, {
            hideAcademicDegrees(it)
        }, { deleteDegree(it) }, { }, { deleteEducation(it) })
    }

    private fun addEducation() {
        val acDegreeSpinner =
            adapter.currentList.filter { it.type == EditEducationModel.EDUCATION_LEVEL }
                .toMutableList()
        val acDegreeList =
            adapter.currentList.filter { it.type == EditEducationModel.HIGHT_LEVEL_ITEM }
                .toMutableList()
        val edList = adapter.currentList.filter { it.type == EDUCATION_ITEM }.toMutableList()
        val newList = mutableListOf<EditEducationModel>()
        newList.addAll(acDegreeSpinner)
        newList.addAll(acDegreeList)
        if (acDegreeSpinner[0].selectedDegree == "Более одного высшего" || acDegreeSpinner[0].selectedDegree == "Высшее") {
            newList.add(
                EditEducationModel(
                    -2, ADD_HIGHT_LEVEL, null, null,
                    null, null, null, null, null,
                    false, true, false, false, null, academicDegrees != null
                )
            )
        }
        newList.addAll(edList)
        if (edList.firstOrNull { !it.isDataValid } == null) {
            newList.add(
                EditEducationModel(
                    newList[newList.size - 1].id + 1,
                    EDUCATION_ITEM,
                    birthday,
                    EducationModelNew(null, null, null, null, null, null),
                    null,
                    null,
                    null,
                    null,
                    null,
                    true,
                    false,
                    false,
                    true,
                    null,
                    academicDegrees != null
                )
            )
        } else {
            newList.forEach {
                if (!it.isDataValid) it.showErrors = true
            }
        }
        newList.add(
            EditEducationModel(
                -1, ADD_EDUCATION, null, null,
                null, null, null, null, null,
                false, true, false, false, null, academicDegrees != null
            )
        )
        adapter.submitList(newList)
        adapter.notifyDataSetChanged()
    }

    private fun deleteEducation(position: Int) {
        val currentData = adapter.currentList.toMutableList()
        val edList = adapter.currentList.filter { it.type == EDUCATION_ITEM }.toMutableList()
        currentData.removeAt(position)
        currentData.forEach {
            if (it.type == EDUCATION_ITEM) {
                it.isDeleteVisible = (edList.size - 1) != 1
            }
        }
        adapter.submitList(currentData)
        adapter.notifyDataSetChanged()
    }

    private fun addDegree() {
        val acDegreeSpinner =
            adapter.currentList.filter { it.type == EditEducationModel.EDUCATION_LEVEL }
                .toMutableList()
        val acDegreeList =
            adapter.currentList.filter { it.type == EditEducationModel.HIGHT_LEVEL_ITEM }
                .toMutableList()
        val edList = adapter.currentList.filter { it.type == EDUCATION_ITEM }.toMutableList()
        val newList = mutableListOf<EditEducationModel>()
        newList.addAll(acDegreeSpinner)
        acDegreeList.forEach { it.isDeleteVisible = true }
        newList.addAll(acDegreeList)
        newList.add(
            EditEducationModel(
                acDegreeList[acDegreeList.size - 1].id + 1,
                EditEducationModel.HIGHT_LEVEL_ITEM,
                birthday,
                null,
                AcademicDegreeModelNew(null, null, academicDegrees?.first()?.id, false),
                null,
                academicDegrees,
                speciality,
                null,
                true,
                true,
                false,
                false,
                null,
                academicDegrees != null
            )
        )
        newList.add(
            EditEducationModel(
                -2, ADD_HIGHT_LEVEL, null, null,
                null, null, null, null, null,
                false, true, false, false, null, academicDegrees != null
            )
        )
        newList.addAll(edList)
        newList.add(
            EditEducationModel(
                -1, ADD_EDUCATION, null, null,
                null, null, null, null, null,
                false, true, false, false, null, academicDegrees != null
            )
        )
        adapter.submitList(newList)
        adapter.notifyDataSetChanged()
    }

    private fun deleteDegree(position: Int) {
        val currentData = adapter.currentList.toMutableList()
        val acDegreeList =
            currentData.filter { it.type == EditEducationModel.HIGHT_LEVEL_ITEM }.toMutableList()
        currentData.removeAt(position)
        currentData.forEach {
            if (it.type == EditEducationModel.HIGHT_LEVEL_ITEM) {
                it.isDeleteVisible = (acDegreeList.size - 1) != 1
            }
        }
        adapter.submitList(currentData)
        adapter.notifyDataSetChanged()
    }

    private fun hideAcademicDegrees(isTrigger: Boolean) {
        val acDegreeSpinner =
            adapter.currentList.filter { it.type == EditEducationModel.EDUCATION_LEVEL }
                .toMutableList()
        val acDegreeList =
            adapter.currentList.filter { it.type == EditEducationModel.HIGHT_LEVEL_ITEM }
                .toMutableList()
        val edList = adapter.currentList.filter { it.type == EDUCATION_ITEM }.toMutableList()
        val newList = mutableListOf<EditEducationModel>()
        if (isTrigger) {
            newList.addAll(acDegreeSpinner)
            if (hiddenAcademicDegrees != null) {
                hiddenAcademicDegrees?.forEach {
                    newList.add(it)
                }
                hiddenAcademicDegrees = null
            } else {
                newList.add(
                    EditEducationModel(
                        1,
                        EditEducationModel.HIGHT_LEVEL_ITEM,
                        birthday,
                        null,
                        AcademicDegreeModelNew(null, null, academicDegrees?.first()?.id, false),
                        null,
                        academicDegrees,
                        speciality,
                        null,
                        false,
                        true,
                        false,
                        false,
                        null,
                        academicDegrees != null
                    )
                )
                hiddenAcademicDegrees = null
            }
            newList.add(
                EditEducationModel(
                    -2, ADD_HIGHT_LEVEL, null, null,
                    null, null, null, null, null,
                    false, true, false, false, null, academicDegrees != null
                )
            )
            newList.addAll(edList)
            newList.add(
                EditEducationModel(
                    -1, ADD_EDUCATION, null, null,
                    null, null, null, null, null,
                    false, true, false, false, null, academicDegrees != null
                )
            )
        } else {
            if (acDegreeList.isNotEmpty() && hiddenAcademicDegrees == null) {
                hiddenAcademicDegrees = acDegreeList
            }
            newList.addAll(acDegreeSpinner)
            newList.addAll(edList)
            newList.add(
                EditEducationModel(
                    -1, ADD_EDUCATION, null, null,
                    null, null, null, null, null,
                    false, true, false, false, null, academicDegrees != null
                )
            )
        }
        adapter.submitList(newList)
        adapter.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarTitle(getString(R.string.profile_title_education))
        mBinding.apply {
            rvInterests.apply {
                adapter = this@EditEducationFragment.adapter
                onScrolled { _, dy ->
                    presenter.changeAppBarElevation(dy)
                }
            }
            btnEdit.setOnClickListener {
                val currentList = adapter.currentList.toMutableList()
                val acDegreeSpinner =
                    adapter.currentList.filter { it.type == EditEducationModel.EDUCATION_LEVEL }
                        .toMutableList()
                val acDegreeList =
                    adapter.currentList.filter { it.type == EditEducationModel.HIGHT_LEVEL_ITEM }
                        .toMutableList()
                val edList = adapter.currentList.filter { it.type == EDUCATION_ITEM }.toMutableList()
                if (edList.firstOrNull { !it.isDataValid } == null) {
                    showEditWarning(
                        presenter.getBaseUserState(),
                        presenter.getMaxUserState(), false, false
                    ) {
                        presenter.onSaveEducationClick(
                            ToggleIntModel(
                                acDegreeSpinner[0].availableEducations?.firstOrNull { aEd -> aEd.name == acDegreeSpinner[0].selectedDegree }?.id/*acDegreeSpinner[0].educationLevel?.value*/,
                                acDegreeSpinner[0].educationLevel?.showInProfile
                            ),
                            edList.map {
                                EducationModel(
                                    it.education?.id,
                                    it.education?.begin,
                                    it.education?.end,
                                    it.education?.organization,
                                    it.education?.speciality,
                                    it.education?.showInProfile
                                )
                            },
                            acDegreeList.map {
                                AcademicDegreeModel(
                                    it.academicDegrees?.id, it.academicDegrees?.speciality,
                                    it.academicDegrees?.degree, it.academicDegrees?.showInProfile
                                )
                            })
                    }
                } else {
                    currentList.forEach {
                        if (!it.isDataValid) it.showErrors = true
                    }
                    adapter.submitList(currentList)
                    adapter.notifyDataSetChanged()
                }
            }
        }

    }

    override fun setEducationData(user: UserDetail) {
        birthday = user.birthday?.value?.parseToDate(defaultServerDateFormatter)
        val academicDegree =
            if (user.binds?.academicDegree?.size == 1 && user.binds?.academicDegree?.get(0)?.degree == null)
                null else user.binds?.academicDegree
        academicDegrees =
            user.academicDegrees?.map { ad -> EducationLevelNew(ad.id, ad.name, ad.order) }
                ?.toMutableList()
        speciality = user.speciality?.map { ad -> EducationLevelNew(ad.id, ad.name, ad.order) }
            ?.toMutableList()

        val screenData = mutableListOf<EditEducationModel>()
        screenData.add(
            EditEducationModel(
                -3,
                EditEducationModel.EDUCATION_LEVEL,
                birthday,
                null,
                if (academicDegree.isNullOrEmpty()) null else AcademicDegreeModelNew(
                    academicDegree[0].id, academicDegree[0].speciality,
                    academicDegree[0].degree, academicDegree[0].showInProfile
                ),
                user.educationLevelList?.map { EducationLevelNew(it.id, it.name, it.order) },
                null,
                null,
                ToggleIntModelNew(user.educationLevel?.value, user.educationLevel?.showInProfile),
                false,
                true,
                false,
                false,
                user.educationLevelList?.firstOrNull { it.id == user.educationLevel?.value }?.name,
                /*academicDegrees != null*/
                !academicDegree.isNullOrEmpty()
            )
        )

        if (!academicDegree.isNullOrEmpty()) {
            val isDeleteVisible = (academicDegree.size) > 1
            academicDegree.forEach {
                screenData.add(
                    EditEducationModel(
                        it.id ?: 0,
                        EditEducationModel.HIGHT_LEVEL_ITEM,
                        birthday,
                        null,
                        AcademicDegreeModelNew(it.id, it.speciality, it.degree, it.showInProfile),
                        null,
                        academicDegrees,
                        speciality,
                        null,
                        isDeleteVisible,
                        true,
                        false,
                        false,
                        null,
                        academicDegrees != null
                    )
                )
            }
            screenData.add(
                EditEducationModel(
                    -2, ADD_HIGHT_LEVEL, null, null,
                    null, null, null, null, null,
                    false, true, false, false, null, academicDegrees != null
                )
            )
        }
        val isDeleteVisibleEducation = (user.binds?.education?.size ?: 0) > 1
        if (user.binds?.education?.size != 0) {
            user.binds?.education?.forEach {
                screenData.add(
                    EditEducationModel(
                        (it.id
                            ?: 1) * 10,
                        EDUCATION_ITEM,
                        birthday,
                        EducationModelNew(
                            it.id, it.begin, it.end,
                            it.organization, it.speciality, it.showInProfile
                        ),
                        null,
                        null,
                        null,
                        null,
                        null,
                        isDeleteVisibleEducation,
                        true,
                        false,
                        it.end == null,
                        null,
                        academicDegrees != null
                    )
                )
            }
        } else {
            screenData.add(
                EditEducationModel(
                    1,
                    EDUCATION_ITEM,
                    birthday,
                    EducationModelNew(null, null, null, null, null, null),
                    null,
                    null,
                    null,
                    null,
                    null,
                    true,
                    false,
                    false,
                    true,
                    null,
                    academicDegrees != null
                )
            )
        }
        screenData.add(
            EditEducationModel(
                -1, ADD_EDUCATION, null, null,
                null, null, null, null, null,
                false, true, false, false, null, academicDegrees != null
            )
        )
        adapter.submitList(screenData)
    }

    override fun showUpdateError(message: String?) {
        val title = getString(R.string.profile_edit_request_error)
        Toast.makeText(requireContext(), message?.let { "$title: $it" }
            ?: title, Toast.LENGTH_SHORT).show()
    }

}
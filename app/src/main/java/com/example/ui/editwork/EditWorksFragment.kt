package com.example.ui.editwork

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserDetail
import com.example.data.models.WorkExperience
import com.example.data.models.WorkExperienceModel
import com.example.data.models.WorkExperienceServerModel
import com.example.databinding.ItemProfileDataEditNoWorkNewBinding
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.editwork.EditWorksModel.Companion.ADD_WORK
import com.example.ui.editwork.EditWorksModel.Companion.HAS_WORK
import com.example.ui.editwork.EditWorksModel.Companion.WORK_ITEM
import com.example.ui.views.NoWorkDialog
import kotlinx.android.synthetic.main.fragment_edit_work_fragment.*
import javax.inject.Inject
import javax.inject.Provider

class EditWorksFragment: BaseFragment(), EditWorksContract.View, ToolbarFragment {

    private lateinit var adapter: EditWorksAdapter
    private var birthday: String? = ""

    private var currentAddedWorks: WorkExperienceModel? = null

    override fun layout(): Int = R.layout.fragment_edit_work_fragment

    @InjectPresenter
    lateinit var presenter: EditWorksPresenter

    @Inject
    lateinit var presenterProvider: Provider<EditWorksPresenter>

    @ProvidePresenter
    fun providePresenter(): EditWorksPresenter = presenterProvider.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = EditWorksAdapter ({ hasWork, holder ->
            noWorkClick(hasWork, holder)
        },{
            deleteWork(it)
        },{
            addMoreWork()
        },{

        })
    }

    private fun deleteWork(position: Int) {
        val currentList = adapter.currentList.toMutableList()
        val noWorkOnly = currentList.filter { it.type == HAS_WORK }
        val buttonOnly = currentList.filter { it.type == ADD_WORK }
        val onlyWorks = currentList.filter { it.type == WORK_ITEM }
        if (onlyWorks.size == 1) {
            val newList = mutableListOf<EditWorksModel>()
            newList.addAll(noWorkOnly)
            newList.add(EditWorksModel(1, WORK_ITEM, WorkExperienceNew(null, null, null, null, null, null, null),
                    false, false, birthday, false, false, false, true))
            newList.addAll(buttonOnly)
            adapter.submitList(newList)
        } else {
            currentList.removeAt(position)
            if (onlyWorks.size == 2) {
                currentList.forEach { it.isDeleteVisible = false }
            }
            adapter.submitList(currentList)
        }
    }

    private fun addMoreWork() {
        val currentList = adapter.currentList.toMutableList()
        val onlyWorks = currentList.filter { it.type == WORK_ITEM }
        val noWorkOnly = currentList.filter { it.type == HAS_WORK }
        val buttonOnly = currentList.filter { it.type == ADD_WORK }

        if (onlyWorks.isNotEmpty()) {
            if (onlyWorks[onlyWorks.size - 1].isDataValid) {
                val newList = mutableListOf<EditWorksModel>()
                newList.addAll(noWorkOnly)
                onlyWorks.forEach { it.isDeleteVisible = true }
                newList.addAll(onlyWorks)
                newList.add(EditWorksModel(onlyWorks[onlyWorks.size - 1].id + 1, WORK_ITEM, WorkExperienceNew(null, null, null, null, null, null, null),
                        false, true, birthday, false, false, false, true))
                newList.addAll(buttonOnly)
                adapter.submitList(newList)
                adapter.notifyItemRangeChanged(1, newList.size - 1)
            } else {
                currentList[currentList.size - 2].showErrors = true
                adapter.submitList(currentList)
                adapter.notifyItemChanged(currentList.size - 2)
            }
        } else {
            val newList = mutableListOf<EditWorksModel>()
            newList.addAll(noWorkOnly)
            newList.add(EditWorksModel(1, WORK_ITEM, WorkExperienceNew(null, null, null, null, null, null, null),
                    false, false, birthday, false, false, false, true))
            newList.addAll(buttonOnly)
            adapter.submitList(newList)
            adapter.notifyItemRangeChanged(1, newList.size - 1)
        }
    }

    private fun noWorkClick(hasWork: Boolean, holder: ItemProfileDataEditNoWorkNewBinding) {
        val onlyWorks = adapter.currentList.toMutableList().filter { it.type == WORK_ITEM }
        if (hasWork && onlyWorks.isNotEmpty() && !onlyWorks[0].works?.organization.isNullOrEmpty()) {
            NoWorkDialog(requireContext())
                    .setSelectCallback { isDelete ->
                        if (isDelete)
                            updateListHasWork(hasWork, holder)
                        else updateListHasWork(!hasWork, holder)
                    }
        } else updateListHasWork(hasWork, holder)
    }

    private fun updateListHasWork(hasWork: Boolean, holder: ItemProfileDataEditNoWorkNewBinding) {
        val currentList = adapter.currentList.toMutableList()
        currentList.forEach {
            it.hasWork = hasWork
        }
        adapter.submitList(currentList)
        adapter.notifyItemRangeChanged(1, currentList.size - 1)
        holder.scNoExperience.isChecked = hasWork
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvInterests.adapter = adapter
        btnEdit.setOnClickListener {
            val currentList = adapter.currentList.toMutableList()
            val onlyWorks = currentList.filter { it.type == WORK_ITEM }
            if (onlyWorks[onlyWorks.size-1].isDataValid) {
                showEditWarning(presenter.getBaseUserState(),
                        presenter.getMaxUserState(), false, false) {
                    val works = mutableListOf<WorkExperience>()
                    if (!onlyWorks[0].hasWork) {
                        onlyWorks.forEach {
                            val oldSame = currentAddedWorks?.models?.firstOrNull { old -> old.id == it.id }
                            works.add(WorkExperience(if (oldSame == null) null else it.id, it.works?.begin,
                                    it.works?.end, it.works?.organization, it.works?.position, it.works?.description,
                                    it.works?.showInProfile))
                        }
                    }
                    val result = WorkExperienceServerModel(onlyWorks[0].hasWork, works)
                    presenter.onSaveWorkClick(result)
                }
            } else {
                currentList[currentList.size-2].showErrors = true
                adapter.submitList(currentList)
                adapter.notifyItemChanged(currentList.size-2)
            }
        }
    }

    override fun setWorkData(user: UserDetail) {
        currentAddedWorks = user.binds?.workExperience
        birthday = user.birthday?.value
        val isDeleteVisible = (currentAddedWorks?.models?.size?: 0) > 1
        val screenData = mutableListOf<EditWorksModel>()
        screenData.add(EditWorksModel(-2, HAS_WORK, null, currentAddedWorks?.absent?: false, false,
                null, true, true, false, true))
        user.binds?.workExperience?.models?.forEach {
            screenData.add(EditWorksModel(it.id?: 1, WORK_ITEM,
                    WorkExperienceNew(it.id, it.begin, it.end, it.organization, it.position, it.description, it.showInProfile),
                    user.binds?.workExperience?.absent?: false, isDeleteVisible, birthday, true, true, false, it.end == null))
        }
        screenData.add(EditWorksModel(-1, ADD_WORK, null, currentAddedWorks?.absent?: false, false, null, true, true, false, true))
        adapter.submitList(screenData)
    }

    override fun showUpdateError(message: String?) {
        val title = getString(R.string.profile_edit_request_error)
        Toast.makeText(requireContext(), message?.let { "$title: $it" }
                ?: title, Toast.LENGTH_SHORT).show()
    }

    override val title: CharSequence?
        get() = getString(R.string.profile_work_experience)
}
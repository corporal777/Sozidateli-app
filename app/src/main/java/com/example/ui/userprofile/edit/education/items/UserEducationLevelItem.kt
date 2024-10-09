package com.example.ui.userprofile.edit.education.items

import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.view.isVisible
import com.example.app.R
import com.example.adapters.NoFilterArrayAdapter
import com.example.data.models.EducationLevel
import com.example.data.models.ToggleIntModel
import com.example.app.databinding.ItemUserEducationLevelBinding
import com.example.ui.views.dialogs.DefaultAlertDialog
import com.example.util.initSwitch
import com.xwray.groupie.databinding.BindableItem

class UserEducationLevelItem(
    private val educationLevel: Int?,
    private val availableEducations: List<EducationLevel>,
    private val isAcademicDegree: Boolean,
    private val showInProfile: Boolean?,
    private val onHasAcademicDegree: (hasAcademic: Boolean) -> Unit,
    private val onSelectedLevel: (level: String?) -> Unit
) : BindableItem<ItemUserEducationLevelBinding>(-1001L) {

    private var mSelectedLevel = availableEducations.find { x -> x.id == educationLevel }?.name
    private var mShowInProfile = showInProfile ?: true
    private var mHasAcademicDegree = isAcademicDegree

    var onDataValid: (isValid: Boolean) -> Unit = {}

    private lateinit var mBinding: ItemUserEducationLevelBinding
    override fun bind(viewBinding: ItemUserEducationLevelBinding, position: Int) {
        mBinding = viewBinding
        onDataValid.invoke(!mSelectedLevel.isNullOrBlank())
        onSelectedLevel.invoke(mSelectedLevel)
        viewBinding.apply {
            tvEducationLevel.apply {
                initEducationLevels(this){
                    tilEducationLevel.showError(null)
                    mSelectedLevel = it
                    validateCheckbox(mSelectedLevel, scAcadDegry)
                    onDataValid.invoke(!mSelectedLevel.isNullOrBlank())
                    onSelectedLevel.invoke(mSelectedLevel)
                }
            }
            scAcadDegry.apply {
                validateCheckbox(mSelectedLevel, this)
                isChecked = mHasAcademicDegree
                setOnCheckedChangeListener { _, b ->
                    mHasAcademicDegree = b
                    onHasAcademicDegree.invoke(b)
                }
            }
            scEducation.initSwitch(mShowInProfile) { mShowInProfile = it }

        }
    }


    private fun initEducationLevels(
        tvLevel: AutoCompleteTextView,
        onLevelChange: (level: String?) -> Unit
    ) {
        tvLevel.apply {
            keyListener = null
            setAdapter(
                NoFilterArrayAdapter(
                    context,
                    R.layout.item_dropdown,
                    R.id.tvText,
                    availableEducations.map { it.name }.toMutableList()
                )
            )
            setText(mSelectedLevel)

            onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                val level = availableEducations[position].name
                if (!isTrigger(level)){
                    DefaultAlertDialog(
                        mBinding.root.context,
                        null,
                        mBinding.root.context.getString(R.string.change_degree_text),
                        mBinding.root.context.getString(R.string.ok),
                        mBinding.root.context.getString(R.string.revoke),
                        false
                    )
                        .setSelectCallback { onLevelChange.invoke(level) }
                        .setCancelCallback {
                            setText(mSelectedLevel)
                            onLevelChange.invoke(mSelectedLevel)
                        }
                } else onLevelChange.invoke(level)
            }
            isCursorVisible = false
            isFocusableInTouchMode = false
        }
    }

    private fun validateCheckbox(text: String?, cb: AppCompatCheckBox) {
        if (isTrigger(text)) {
            cb.isVisible = true
        } else {
            cb.isVisible = false
            cb.isChecked = false
        }

    }

    fun isDataValid(): Boolean {
        mBinding.apply {
            if (mSelectedLevel.isNullOrBlank()) tilEducationLevel.apply {
                showError(context.getString(R.string.required_field))
            }
        }
        return !mSelectedLevel.isNullOrBlank()
    }

    fun getDataToSave(): ToggleIntModel {
        return ToggleIntModel(
            availableEducations.firstOrNull { it.name == mSelectedLevel }?.id,
            mShowInProfile
        )
    }

    fun getLevel() = mSelectedLevel

    private fun isTrigger(text: String?) = text == "Более одного высшего" || text == "Высшее"

    override fun getLayout() = R.layout.item_user_education_level
}
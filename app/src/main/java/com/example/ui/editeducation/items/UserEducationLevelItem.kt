package com.example.ui.editeducation.items

import android.content.Context
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.view.isVisible
import com.example.R
import com.example.adapters.NoFilterArrayAdapter
import com.example.data.models.EducationLevel
import com.example.data.models.InterestNew
import com.example.data.models.ToggleIntModel
import com.example.databinding.ItemProfileDataEditEducationLevelBinding
import com.example.databinding.ItemUserEducationLevelBinding
import com.example.ui.views.ClearDegreeDialog
import com.example.ui.views.dialogs_new.TitleMessageDialog
import com.example.util.initSwitch
import com.google.android.material.textfield.TextInputLayout
import com.xwray.groupie.databinding.BindableItem
import initDropDownView
import kotlinx.android.synthetic.main.item_profile_data_edit_education_level.*
import onTextChanged

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
                    ClearDegreeDialog(mBinding.root.context).setSelectCallback {
                        if (it) onLevelChange.invoke(level)
                        else {
                            setText(mSelectedLevel)
                            onLevelChange.invoke(mSelectedLevel)
                        }
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
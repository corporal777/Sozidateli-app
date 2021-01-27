package com.example.holders

import android.view.View
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.AppCompatCheckBox
import com.example.R
import com.example.adapters.NoFilterArrayAdapter
import com.google.android.material.textfield.TextInputLayout
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_profile_data_edit_education_level.*

class ProfileDataEducationLevelEditItem(
        educationLevel: String?,
        private val availableEducations: List<String>,
        private val isAcademicDegree: Boolean,
        private val onEducationLevelSelected: (isLast: Boolean) -> Unit,
        private val hasAcademicDegree: (hasAcademic: Boolean) -> Unit
) : Item() {

    var mEducationLevel = educationLevel
        private set

    var mHasAcademicDegree = isAcademicDegree
        private set

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            setupDropDown(tvEducationLevel, scAcadDegry, tilEducationLevel, availableEducations, mEducationLevel) {
                mEducationLevel = it
                checkLastEducationSelected()
            }
            scAcadDegry.isChecked = mHasAcademicDegree
            scAcadDegry.setOnCheckedChangeListener { _, b ->
                mHasAcademicDegree = b
                hasAcademicDegree(b)
            }
        }
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) super.bind(viewHolder, position, payloads)
        else {
            if (!isDataValid()) {
                viewHolder.tilEducationLevel.apply {
                    error = resources.getString(R.string.profile_edit_empty_field_error)
                }
            }
        }
    }

    private fun setupDropDown(textView: AutoCompleteTextView, cb: AppCompatCheckBox, textInputLayout: TextInputLayout, variants: List<String>, initialVariant: String?, onSelect: (String?) -> Unit) {
        textView.apply {
            setAdapter(NoFilterArrayAdapter(context, android.R.layout.simple_list_item_1, variants.toTypedArray()))
            setText(initialVariant)
            validateCheckbox(initialVariant, cb)
            onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                textInputLayout.error = null
                validateCheckbox(variants.getOrNull(position), cb)
                if (variants.getOrNull(position) != "Более одного высшего" || variants.getOrNull(position) != "Высшее") {
                    mHasAcademicDegree = false
                    cb.isChecked = mHasAcademicDegree
                    hasAcademicDegree(false)
                }
                onSelect(variants.getOrNull(position))
            }
        }
    }

    private fun validateCheckbox(text: String?, cb: AppCompatCheckBox) {
        cb.apply {
            visibility = if (text == "Более одного высшего" || text == "Высшее")
                View.VISIBLE
            else
                View.GONE
        }
    }

    private fun checkLastEducationSelected() {
        onEducationLevelSelected(mEducationLevel == availableEducations.lastOrNull())
    }

    fun isDataValid() = mEducationLevel != null

    override fun getLayout() = R.layout.item_profile_data_edit_education_level
}
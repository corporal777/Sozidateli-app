package com.example.holders

import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import com.example.R
import com.example.adapters.NoFilterArrayAdapter
import com.google.android.material.textfield.TextInputLayout
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_profile_data_edit_education_level.*

class ProfileDataEducationLevelEditItem(
        educationLevel: String?,
        private val availableEducations: List<String>,
        private val onEducationLevelSelected: (isLast: Boolean) -> Unit
) : Item() {

    var mEducationLevel = educationLevel
        private set

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            setupDropDown(tvEducationLevel, tilEducationLevel, availableEducations, mEducationLevel) {
                mEducationLevel = it
                checkLastEducationSelected()
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

    private fun setupDropDown(textView: AutoCompleteTextView, textInputLayout: TextInputLayout, variants: List<String>, initialVariant: String?, onSelect: (String?) -> Unit) {
        textView.apply {
            setAdapter(NoFilterArrayAdapter(context, android.R.layout.simple_list_item_1, variants.toTypedArray()))
            setText(initialVariant)
            onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                textInputLayout.error = null
                onSelect(variants.getOrNull(position))
            }
        }
    }

    private fun checkLastEducationSelected() {
        onEducationLevelSelected(mEducationLevel == availableEducations.lastOrNull())
    }

    fun isDataValid() = mEducationLevel != null

    override fun getLayout() = R.layout.item_profile_data_edit_education_level
}
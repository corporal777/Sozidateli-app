package com.example.holders

import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import com.example.R
import com.example.adapters.NoFilterArrayAdapter
import com.google.android.material.textfield.TextInputLayout
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_profile_data_edit_academic_degree.*

class ProfileDataAcademicDegreeEditItem(
        degreesLevel: String?,
        sciencesLevel: String?,
        private val availableDegrees: List<String>,
        private val availableSciences: List<String>,
        private val onRemoveClickListener: (ProfileDataAcademicDegreeEditItem) -> Unit
) : Item() {

    var mDegreesLevel = degreesLevel
        private set

    var mSciencesLevel = sciencesLevel
        private set

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            setupDropDown(tvDegreesLevel, tilDegreesLevel, availableDegrees, mDegreesLevel) {
                mDegreesLevel = it
            }
            setupDropDown(tvSciencesLevel, tilSciencesLevel, availableSciences, mSciencesLevel) {
                mSciencesLevel = it
            }

            btnRemove.setOnClickListener { onRemoveClickListener(this@ProfileDataAcademicDegreeEditItem) }
        }
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) super.bind(viewHolder, position, payloads)
        else {
            if (!isDegreeValid()) {
                viewHolder.tilDegreesLevel.apply {
                    error = resources.getString(R.string.profile_edit_empty_field_error)
                }
            }
            if (!isSciencesValid()) {
                viewHolder.tilSciencesLevel.apply {
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

    private fun isDegreeValid(): Boolean = !mDegreesLevel.isNullOrEmpty()
    private fun isSciencesValid(): Boolean = !mSciencesLevel.isNullOrEmpty()
    fun isDataValid() = isDegreeValid() && isSciencesValid()

    override fun getLayout() = R.layout.item_profile_data_edit_academic_degree
}
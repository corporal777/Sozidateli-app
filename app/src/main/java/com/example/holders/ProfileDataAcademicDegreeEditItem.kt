package com.example.holders

import android.view.View
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import com.example.R
import com.example.adapters.NoFilterArrayAdapter
import com.example.data.models.AcademicDegreeModel
import com.example.data.models.EducationLevel
import com.example.databinding.ItemProfileDataEditAcademicDegreeBinding
import com.example.util.initSwitch
import com.google.android.material.textfield.TextInputLayout
import com.xwray.groupie.databinding.BindableItem

class ProfileDataAcademicDegreeEditItem(
    id: Int?,
    private val degreesLevel: String?,
    private val sciencesLevel: String?,
    private val availableDegrees: List<EducationLevel>,
    private val availableSciences: List<EducationLevel>,
    private val showInProfile: Boolean?,
    private val onRemoveClickListener: (ProfileDataAcademicDegreeEditItem) -> Unit
) : BindableItem<ItemProfileDataEditAcademicDegreeBinding>(id?.toLong() ?: 0) {

    var isDeleteVisible = true

    var mId = id
        private set

    var mDegreesLevel = degreesLevel
        private set

    var mSciencesLevel = sciencesLevel
        private set

    var mShowInProfile = showInProfile ?: false
        private set

    override fun bind(viewBinding: ItemProfileDataEditAcademicDegreeBinding, position: Int) {
        viewBinding.apply {
            setupDropDown(tvDegreesLevel, tilDegreesLevel, availableDegrees, mDegreesLevel) {
                mDegreesLevel = it?.name
            }
            setupDropDown(tvSciencesLevel, tilSciencesLevel, availableSciences, mSciencesLevel) {
                mSciencesLevel = it?.name
            }

            btnRemove.setOnClickListener { onRemoveClickListener(this@ProfileDataAcademicDegreeEditItem) }

            scEducation.initSwitch(mShowInProfile) { mShowInProfile = it }

            if (isDeleteVisible) btnRemove.visibility = View.VISIBLE
            else btnRemove.visibility = View.GONE
        }
    }

    override fun bind(viewBinding: ItemProfileDataEditAcademicDegreeBinding, position: Int, payloads: MutableList<Any>?) {
        if (payloads.isNullOrEmpty()) super.bind(viewBinding, position, payloads)
        else {
            if (!isDegreeValid()) {
                viewBinding.tilDegreesLevel.apply {
                    error = resources.getString(R.string.profile_edit_empty_field_error)
                }
            }
            if (!isSciencesValid()) {
                viewBinding.tilSciencesLevel.apply {
                    error = resources.getString(R.string.profile_edit_empty_field_error)
                }
            }
        }
    }


    private fun setupDropDown(
        textView: AutoCompleteTextView,
        baseTextInputLayout: TextInputLayout,
        variants: List<EducationLevel>,
        initialVariant: String?,
        onSelect: (EducationLevel?) -> Unit
    ) {
        textView.apply {
            setAdapter(
                NoFilterArrayAdapter(
                    context,
                    android.R.layout.simple_list_item_1,
                    variants.map { item -> item.name }.toTypedArray()
                )
            )
            setText(initialVariant)
            onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                baseTextInputLayout.error = null
                onSelect(variants.getOrNull(position))
            }
        }
    }

    private fun isDegreeValid(): Boolean = !mDegreesLevel.isNullOrEmpty()
    private fun isSciencesValid(): Boolean = !mSciencesLevel.isNullOrEmpty()
    fun isDataValid() = isDegreeValid() && isSciencesValid()

    fun getDataToSave(): AcademicDegreeModel {
        return AcademicDegreeModel(
            //id = mId,
            id = null,
            speciality = availableSciences.firstOrNull { degree -> degree.name == mSciencesLevel }?.id,
            degree = availableDegrees.firstOrNull { degree -> degree.name == mDegreesLevel }?.id,
            showInProfile = mShowInProfile
        )
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is ProfileDataAcademicDegreeEditItem) return false
        if (degreesLevel != other.degreesLevel) return false
        if (sciencesLevel != other.sciencesLevel) return false
        if (availableDegrees != other.availableDegrees) return false
        if (availableSciences != other.availableSciences) return false
        if (showInProfile != other.showInProfile) return false
        return true
    }

    override fun getLayout() = R.layout.item_profile_data_edit_academic_degree
}
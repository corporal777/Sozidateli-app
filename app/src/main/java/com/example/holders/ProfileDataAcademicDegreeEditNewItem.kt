package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_profile_data_edit_academic_degree_new.*

class ProfileDataAcademicDegreeEditNewItem(
        degreesLevel: String?,
        sciencesLevel: String?,
        private val onRemoveClickListener: (ProfileDataAcademicDegreeEditNewItem) -> Unit,
        private val onEditClickListener: (ProfileDataAcademicDegreeEditNewItem, position: Int) -> Unit
): Item() {

    var mDegreesLevel = degreesLevel
        private set

    var mSciencesLevel = sciencesLevel
        private set

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvDegreesLevel.setText(mDegreesLevel)
            tvSciencesLevel.setText(mSciencesLevel)
            btnRemove.setOnClickListener {
                onRemoveClickListener(this@ProfileDataAcademicDegreeEditNewItem)
            }
            btnEdit.setOnClickListener {
                onEditClickListener(this@ProfileDataAcademicDegreeEditNewItem, adapterPosition - 1)
            }
        }
    }

    fun updateData(degreesLevel: String?, sciencesLevel: String?) {
        mDegreesLevel = degreesLevel
        mSciencesLevel = sciencesLevel
        notifyChanged()
    }

    override fun getLayout(): Int = R.layout.item_profile_data_edit_academic_degree_new

}
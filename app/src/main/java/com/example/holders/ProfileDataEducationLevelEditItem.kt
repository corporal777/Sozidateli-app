package com.example.holders

import com.example.R
import com.example.adapters.NoFilterArrayAdapter
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_profile_data_edit_education_level.*
import onTextChanged

class ProfileDataEducationLevelEditItem(
        educationLevel: String?
) : Item() {

    var mEducationLevel = educationLevel
        private set

    override fun bind(viewHolder:GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvEducationLevel.apply {
                val educationLevels = context.resources.getStringArray(R.array.profile_edit_education_levels)
                setAdapter(NoFilterArrayAdapter<String>(context, android.R.layout.simple_list_item_1, educationLevels))

                setText(mEducationLevel)
                keyListener = null
                onTextChanged { mEducationLevel = it.toString() }
            }
        }
    }

    override fun getLayout() = R.layout.item_profile_data_edit_education_level
}
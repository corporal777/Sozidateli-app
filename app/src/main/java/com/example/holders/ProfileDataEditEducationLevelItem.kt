package com.example.holders

import com.example.R
import com.example.adapters.NoFilterArrayAdapter
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_profile_data_edit_education_level.*
import onTextChanged

class ProfileDataEditEducationLevelItem(
        private val educationLevel: String?
) : Item() {

    private var mEducationLevel = educationLevel

    override fun bind(viewHolder: ViewHolder, position: Int) {
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
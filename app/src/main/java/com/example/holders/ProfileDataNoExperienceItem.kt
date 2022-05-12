package com.example.holders

import com.example.R
import com.example.util.initSwitch
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_profile_data_edit_no_work.*

class ProfileDataNoExperienceItem: Item {

    private var hasWork: Boolean = true
    private val noWorkListener:(hasWork: Boolean) -> Unit

    constructor(noWorkListener: (hasWork: Boolean) -> Unit) : super() {
        this.noWorkListener = noWorkListener
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.scNoExperience.isChecked = hasWork
        viewHolder.scNoExperience.initSwitch(hasWork) {
            noWorkListener(it)
        }
    }

    fun hasWork(hasWork: Boolean) {
        this.hasWork = hasWork
    }

    override fun getLayout(): Int = R.layout.item_profile_data_edit_no_work
}
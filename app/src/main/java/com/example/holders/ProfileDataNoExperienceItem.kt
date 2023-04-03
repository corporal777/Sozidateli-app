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

    private lateinit var mViewHolder: GroupieViewHolder
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        mViewHolder = viewHolder
        viewHolder.scNoExperience.isChecked = hasWork
        viewHolder.scNoExperience.initSwitch(hasWork) {
            noWorkListener(it)
        }
    }

    fun hasWork(hasWork: Boolean) {
        this.hasWork = hasWork
    }

    fun setNoExperience(hasWork: Boolean){
        if (this::mViewHolder.isInitialized){
            this.hasWork = hasWork
            mViewHolder.scNoExperience.isChecked = hasWork
        }
    }

    override fun getLayout(): Int = R.layout.item_profile_data_edit_no_work
}
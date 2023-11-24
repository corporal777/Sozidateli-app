package com.example.holders

import com.example.R
import com.example.databinding.ItemProfileDataEditNoWorkBinding
import com.example.util.initSwitch
import com.xwray.groupie.databinding.BindableItem

class ProfileDataNoExperienceItem(
    private val noWorkListener: (hasWork: Boolean) -> Unit
) : BindableItem<ItemProfileDataEditNoWorkBinding>() {

    private var hasWork: Boolean = true

    private lateinit var mViewHolder: ItemProfileDataEditNoWorkBinding
    override fun bind(viewBinding: ItemProfileDataEditNoWorkBinding, position: Int) {
        mViewHolder = viewBinding
        viewBinding.scNoExperience.isChecked = hasWork
        viewBinding.scNoExperience.initSwitch(hasWork) {
            noWorkListener(it)
        }
    }

    fun hasWork(hasWork: Boolean) {
        this.hasWork = hasWork
    }

    fun setNoExperience(hasWork: Boolean) {
        if (this::mViewHolder.isInitialized) {
            this.hasWork = hasWork
            mViewHolder.scNoExperience.isChecked = hasWork
        }
    }


    override fun getLayout(): Int = R.layout.item_profile_data_edit_no_work
}
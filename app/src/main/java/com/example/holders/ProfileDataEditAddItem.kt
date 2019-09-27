package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_profile_data_edit_add.*

class ProfileDataEditAddItem(
        private val action: Int,
        private val addClickListener: () -> Unit
) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.btnAdd.apply {
            setOnClickListener { addClickListener() }
            val actionText = when (action) {
                ACTION_ADD_RECORD -> R.string.add_record
                ACTION_ADD_FILE -> R.string.add_file
                else -> R.string.add_record
            }
            text = resources.getString(actionText)
        }
    }

    override fun getLayout() = R.layout.item_profile_data_edit_add

    companion object {
        const val ACTION_ADD_RECORD = 0
        const val ACTION_ADD_FILE = 1
    }
}
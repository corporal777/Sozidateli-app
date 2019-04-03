package com.example.holders.profile

import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import com.example.R
import com.example.data.models.ProfileField
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_switch.view.*

class ProfileSwitchItem(private val profileField: ProfileField, private val onCheckedChangeListener: CompoundButton.OnCheckedChangeListener? = null) : ProfileBaseFieldItem(profileField) {


    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        viewHolder.itemView.apply {
            switchView.text = profileField.label ?: context.getString(R.string.profile_show_in_profile)
            var isChecked = false
            if (profileField.data != null) {
                isChecked = profileField.data.toString().toBoolean()
            }

            profileField.data = isChecked

            switchView.isChecked = isChecked

            switchView.setOnCheckedChangeListener { compoundButton, b ->
                profileField.data = b
                onCheckedChangeListener?.onCheckedChanged(compoundButton, b)
            }
        }
    }

    override fun getLayout() = R.layout.item_switch
}
package com.example.holders

import android.support.transition.AutoTransition
import android.support.transition.Transition
import android.support.transition.TransitionListenerAdapter
import android.support.transition.TransitionManager
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import com.example.R
import com.example.data.models.ProfileField
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.brown_button.view.*
import kotlinx.android.synthetic.main.field_expand_profile.view.*

class BrownButtonItem(private val text:String,private val clickListener: View.OnClickListener) : Item() {


    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            btn.text = text
            btn.setOnClickListener(clickListener)
        }
    }

    override fun getLayout() = R.layout.brown_button
}
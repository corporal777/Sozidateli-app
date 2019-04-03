package com.example.holders.profile

import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import com.example.R
import com.example.data.models.ProfileField
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder

class TrashItem(private val clickListener: View.OnClickListener) : Item() {


    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.setIsRecyclable(false)
        viewHolder.itemView.apply {
            setOnClickListener(clickListener)
        }
    }

    override fun getLayout() = R.layout.item_trash
}
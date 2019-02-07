package com.example.holders

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
import kotlinx.android.synthetic.main.item_info_profile_expand.view.*

class InfoProfileExpandFieldItem(private val title:String,private val data: HashMap<String, String?>,private val isFirst:Boolean = false) : Item() {


    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            expand.setName(title)
            expand.setDataInfo(data)
            expand.setisFirstExpand(isFirst)
        }
    }

    override fun getLayout() = R.layout.item_info_profile_expand
}
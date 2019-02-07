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
import com.example.util.CropCircleTransformation
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_profile_header.view.*

class ProfileHeaderItem(private val name:String, private val image:String?,private val id:Int) : Item() {


    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            Picasso.get().setLoggingEnabled(true)
            if (!image.isNullOrEmpty()) Picasso.get().load(image).transform(CropCircleTransformation()).into(ivAvatar)

            tvName.text = name
            tvId.text = "id${this@ProfileHeaderItem.id}"
        }
    }

    override fun getLayout() = R.layout.item_profile_header
}
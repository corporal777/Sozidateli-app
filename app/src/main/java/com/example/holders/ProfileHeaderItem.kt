package com.example.holders

import android.net.Uri
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
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_profile_header.view.*

class ProfileHeaderItem(private val name: String, private val image: String?, private val id: Int, private val isShowEdit: Boolean = false, private val onAvatarClickListener: View.OnClickListener? = null,private val avatarUri: Uri?=null) : Item() {

    override fun bind(viewHolder:GroupieViewHolder, position: Int) {
        viewHolder.itemView.apply {
            Picasso.get().setLoggingEnabled(true)
            if(avatarUri!=null){
                Picasso.get().load(avatarUri).placeholder(R.drawable.avatar_placeholder).transform(CropCircleTransformation()).into(ivAvatar)
            } else {
                Picasso.get().load(image.let { if(it.isNullOrEmpty()) null else it }).placeholder(R.drawable.avatar_placeholder).transform(CropCircleTransformation()).into(ivAvatar)
            }

            tvName.text = name
            tvId.text = "id${this@ProfileHeaderItem.id}"

            ivEdit.visibility = if (isShowEdit) View.VISIBLE else View.GONE
            ivAvatar.setOnClickListener(onAvatarClickListener)
            ivEdit.setOnClickListener(onAvatarClickListener)
        }
    }

    override fun getLayout() = R.layout.item_profile_header
}
package com.example.holders

import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.R
import com.example.data.models.user.SocialRoles
import com.example.data.models.user.Value
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_profile_info.view.*
import kotlinx.android.synthetic.main.item_profile_info_textview.view.*

class InfoProfileFieldItem(private val title: String, private val data: ArrayList<*>) : Item() {


    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            tvLabel.text = title
            if(llInfoFields.childCount==0) {
                for (item in data) {
                    createFields(llInfoFields, item)
                }
            }
        }
    }

    private fun createFields(linearLayout:LinearLayout, item:Any){
        val view = LayoutInflater.from(linearLayout.context).inflate(R.layout.item_profile_info_textview,linearLayout,false)
        var text = ""

        when(item){
            is Value ->{
                text = item.value
            }
            is String ->{
                text = item
            }
        }
        view.tvText.text = text

        view.tvText.linksClickable = true
        view.tvText.autoLinkMask = Linkify.WEB_URLS
        view.tvText.movementMethod = LinkMovementMethod.getInstance()

        linearLayout.addView(view)
    }

    override fun getLayout() = R.layout.item_profile_info
}
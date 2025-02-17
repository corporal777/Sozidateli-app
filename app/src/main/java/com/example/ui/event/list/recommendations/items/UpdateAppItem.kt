package com.example.ui.event.list.recommendations.items

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import com.example.app.R
import com.example.app.databinding.ItemUpdateAppBinding
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem

class UpdateAppItem(val itemId: Long = 1000L) : BindableItem<ItemUpdateAppBinding>(itemId) {


    override fun bind(viewBinding: ItemUpdateAppBinding, position: Int) {
        viewBinding.apply {
            btnAddToTimetable.setOnClickListener {
                openPlayMarket(root.context)
            }
        }
    }

    override fun hasSameContentAs(other: Item<*>): Boolean {
        if (other !is UpdateAppItem) return false
        if (itemId != other.itemId) return false
        return true
    }

    private fun openPlayMarket(context: Context) {
        val appPackageName = context.packageName
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (e: android.content.ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }

    override fun initializeViewBinding(view: View) = ItemUpdateAppBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_update_app
}
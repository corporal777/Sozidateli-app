package com.example.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.app.R
import com.example.app.databinding.ItemEventNewPlaceholderBinding
import com.example.app.databinding.ItemUpdateAppBinding

class AppUpdateAdapter : CustomLoadStateAdapter<AppUpdateAdapter.AppUpdateViewHolder>() {

    var isNeedShowUpdate = false

    override fun getViewHolder(view: ViewGroup): AppUpdateViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(view.context)
        return AppUpdateViewHolder(layoutInflater.inflate(R.layout.item_update_app, view, false))
    }

    override fun getItemsCount(): Int = 1


    override fun onBindViewHolder(holder: AppUpdateViewHolder, position: Int) {
        holder.bind()
    }

    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return loadState is LoadState.NotLoading && isNeedShowUpdate
    }

    inner class AppUpdateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val viewBinding by viewBinding(ItemUpdateAppBinding::bind)

        fun bind() {
            with(viewBinding) {
                btnAddToTimetable.setOnClickListener {
                    openPlayMarket(root.context)
                }
            }
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
    }
}
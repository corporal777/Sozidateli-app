package com.example.adapters.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adapters.CustomLoadStateAdapter
import com.example.app.R
import com.example.app.databinding.ItemUserPlaceholderBinding
import dev.androidbroadcast.vbpd.viewBinding

class UserPlaceholderAdapter(val count : Int) : CustomLoadStateAdapter<UserPlaceholderAdapter.UserPlaceholderViewHolder>() {


    override fun getViewHolder(view: ViewGroup): UserPlaceholderViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(view.context)
        return UserPlaceholderViewHolder(layoutInflater.inflate(R.layout.item_user_placeholder, view, false))
    }

    override fun getItemsCount(): Int = count


    override fun onBindViewHolder(holder: UserPlaceholderViewHolder, position: Int) {
        holder.bind()
    }

    inner class UserPlaceholderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val viewBinding by viewBinding(ItemUserPlaceholderBinding::bind)

        fun bind() {
            with(viewBinding) {}
        }
    }
}
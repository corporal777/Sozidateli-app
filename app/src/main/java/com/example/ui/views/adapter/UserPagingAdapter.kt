package com.example.ui.views.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.app.databinding.ItemUserBinding
import com.example.data.models.UserDetail
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.app.R
import com.example.util.setCircleAvatar
import kotlin.math.truncate

class UserPagingAdapter : PagingDataAdapter<UserDetail, UserPagingAdapter.UserViewHolder>(AsyncDiffCallback) {

    private var isFirstLaunch = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        return UserViewHolder(layoutInflater.inflate(R.layout.item_user, parent, false))
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
            isFirstLaunch = false
        }
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val viewBinding by viewBinding(ItemUserBinding::bind)

        fun bind(user: UserDetail) {
            with(viewBinding) {
                tvUserName.text = user.nameLastName
                tvDescription.apply {
                    isVisible = false
                    text = null
                }
                ivUserAvatar.setCircleAvatar(user.loadUserImage())
                root.setOnClickListener {  }
                btnAction.apply {
                    val action = user.getUserSubscribeAction()
                    visibility = if (action != null) {
                        setAction(action)
                        setOnClickListener {  }
                        View.VISIBLE
                    } else View.GONE
                }
            }
        }
    }

    private object AsyncDiffCallback : DiffUtil.ItemCallback<UserDetail>() {
        override fun areItemsTheSame(oldItem: UserDetail, newItem: UserDetail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserDetail, newItem: UserDetail): Boolean {
            return oldItem == newItem
        }
    }

    companion object{
        fun UserPagingAdapter.withLoadStateAdapters(
            refresh: CustomLoadStateAdapter<*>,
            footer: CustomLoadStateAdapter<*>
        ): ConcatAdapter {
            addLoadStateListener { loadStates ->
                refresh.loadState = loadStates.refresh
                footer.loadState = loadStates.append
            }
            return ConcatAdapter(refresh, this, footer)
        }
    }
}
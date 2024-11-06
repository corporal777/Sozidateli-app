package com.example.ui.views.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.app.databinding.ItemUserBinding
import com.example.data.models.UserDetail
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.app.R
import com.example.exceptions.EmptyDataException
import com.example.extensions.dp
import com.example.util.setCircleAvatar

class UserPagingAdapter(
    private val onUserClick: (user: UserDetail) -> Unit,
    private val onActionClick: (user: UserDetail) -> Unit
) : PagingDataAdapter<UserDetail, UserPagingAdapter.UserViewHolder>(AsyncDiffCallback) {

    private var isFirstLaunch = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return UserViewHolder(layoutInflater.inflate(R.layout.item_user, parent, false))
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
            isFirstLaunch = false
        }
    }

    fun updateUserFavorite(user: UserDetail) {
        snapshot().items.find { x -> x.id == user.id }.let { local ->
            if (local != null) {
                local.binds?.userFavorite = user.binds?.userFavorite
                val position = snapshot().items.indexOf(local)
                notifyItemChanged(position)
            }
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

                btnAction.apply {
                    val action = user.getUserSubscribeAction()
                    visibility = if (action != null) {
                        setAction(action)
                        setOnClickListener {
                            showProgress(true)
                            onActionClick.invoke(user)
                        }
                        View.VISIBLE
                    } else View.INVISIBLE
                }

                btnProgress.apply {
                    isVisible = false
                    setProgressColor(ContextCompat.getColor(context, R.color.main_brown_color_new))
                    setSize(19.dp)
                    setStroke(8f)
                }

                root.setOnClickListener { onUserClick.invoke(user) }
            }
        }

        private fun showProgress(show: Boolean) {
            viewBinding.btnProgress.isVisible = show
            viewBinding.btnAction.isInvisible = show
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

    companion object {
        fun UserPagingAdapter.withLoadStateAdapters(
            refresh: CustomLoadStateAdapter<*>,
            footer: CustomLoadStateAdapter<*>,
            onEmpty: (show: Boolean) -> Unit
        ): ConcatAdapter {
            addLoadStateListener { loadState ->
                refresh.loadState = loadState.refresh
                footer.loadState = loadState.append


                if (loadState.refresh is LoadState.Error)
                    if ((loadState.refresh as LoadState.Error).error is EmptyDataException)
                        if (this.snapshot().isEmpty()) onEmpty.invoke(true)
                        else onEmpty.invoke(false)
                    else onEmpty.invoke(false)
                else onEmpty.invoke(false)
            }
            return ConcatAdapter(refresh, this, footer)
        }
    }
}
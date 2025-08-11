package com.example.adapters.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.examle.data.models.UserDetail
import com.example.adapters.CustomLoadStateAdapter
import com.example.app.R
import com.example.app.databinding.ItemUserBinding
import com.example.extensions.executePlaceholderLoadState
import com.example.extensions.setCircleAvatar
import dev.androidbroadcast.vbpd.viewBinding

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
                local.setUserFavorite(user.binds?.userFavorite)
                //local.binds?.userFavorite = user.binds?.userFavorite
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
                    isVisible = !user.address?.shortAddres.isNullOrEmpty()
                    text = user.address?.shortAddres
                }
                ivUserAvatar.setCircleAvatar(user.loadUserImage(),300)

                btnAction.apply {
//                    val action = user.getUserFavoriteState()
//                    if (action == null) isInvisible = true
//                    else {
//                        isInvisible = false
//                        if (action == UserSubscribeButton.Action.UNFAVORITE){
//                            setActionFavorite()
//                        } else setActionUnfavorite()
//
//                        setOnClickListener {
//                            showProgress(true)
//                            onActionClick.invoke(user)
//                        }
//                    }
                }

                btnProgress.isVisible = false

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
            header: CustomLoadStateAdapter<*>,
            footer: CustomLoadStateAdapter<*>,
            onEmpty: (show: Boolean) -> Unit
        ): ConcatAdapter {
            addLoadStateListener { loadState ->
                header.loadState = if (itemCount > 0) header.notRefresh else loadState.refresh
                footer.loadState = loadState.append
                executePlaceholderLoadState(loadState){ onEmpty.invoke(it) }
            }
            return ConcatAdapter(header, this, footer)
        }
    }
}
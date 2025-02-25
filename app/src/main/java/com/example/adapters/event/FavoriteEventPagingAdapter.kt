package com.example.adapters.event

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.adapters.CustomLoadStateAdapter
import com.example.app.R
import com.example.app.databinding.ItemEventFavoriteBinding
import com.example.data.models.EventNew
import com.example.extensions.executePlaceholderLoadState
import com.example.ui.views.UserSubscribeButton
import com.example.util.getColorStateList
import com.example.util.setImage
import dev.androidbroadcast.vbpd.viewBinding

class FavoriteEventPagingAdapter(
    val onEventClick: (event: EventNew) -> Unit,
    val onEventActionClick: (event: EventNew) -> Unit
) : PagingDataAdapter<EventNew, FavoriteEventPagingAdapter.FavoriteEventVH>(AsyncDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteEventVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        return FavoriteEventVH(layoutInflater.inflate(R.layout.item_event_favorite, parent, false))
    }

    override fun onBindViewHolder(holder: FavoriteEventVH, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    fun updateEventAction(event: EventNew) {
        snapshot().items.find { x -> x.id == event.id }.let { local ->
            if (local != null) {

                local.binds?.userFavorite = event.binds?.userFavorite

                val position = snapshot().items.indexOf(local)
                notifyItemChanged(position)
            }
        }
    }

    inner class FavoriteEventVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val viewBinding by viewBinding(ItemEventFavoriteBinding::bind)

        fun bind(event: EventNew) {
            with(viewBinding) {
                tvEventName.text = event.name
                ivLogo.apply {
                    clipToOutline = true
                    setImage(event.image?.uri.let { if (it.isNullOrBlank()) null else it })
                }

                tvImageName.apply {
                    text = event.name
                    clipToOutline = true
                    backgroundTintList = if (!event.backgroundColor?.value.isNullOrEmpty())
                        getColorStateList(event.backgroundColor?.value!!)
                    else getColorStateList(R.color.colorAccent)
                }

//                btnSubevents.apply {
//                    isVisible = false
//                    isVisible = !event.binds?.userFavoriteActivities.isNullOrEmpty()
//                    setOnClickListener()
//                }
                userSubscribeButton.apply {
                    val action = if (event.binds?.userFavorite != null)
                        UserSubscribeButton.Action.UNFAVORITE
                    else UserSubscribeButton.Action.FAVORITE
                    setAction(action)
                    setOnClickListener {
                        showProgress(true)
                        onEventActionClick.invoke(event)
                    }
                }
                btnProgress.isVisible = false
                root.setOnClickListener { onEventClick.invoke(event) }

            }
        }

        private fun showProgress(show: Boolean) {
            viewBinding.btnProgress.isVisible = show
            viewBinding.userSubscribeButton.isInvisible = show
        }
    }

    companion object {
        fun FavoriteEventPagingAdapter.withLoadStateAdapters(
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


    private object AsyncDiffCallback : DiffUtil.ItemCallback<EventNew>() {
        override fun areItemsTheSame(oldItem: EventNew, newItem: EventNew): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EventNew, newItem: EventNew): Boolean {
            return oldItem == newItem
        }
    }
}
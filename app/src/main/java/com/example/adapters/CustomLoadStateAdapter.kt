package com.example.adapters

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.example.exceptions.EmptyDataException

abstract class CustomLoadStateAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    val notRefresh = LoadState.NotLoading(endOfPaginationReached = false)
    var loadState: LoadState = LoadState.NotLoading(endOfPaginationReached = false)
        set(loadState) {
            if (field != loadState) {
                val oldItem = displayLoadStateAsItem(field)
                val newItem = displayLoadStateAsItem(loadState)

                if (oldItem && !newItem) {
                    notifyItemRangeRemoved(0, getItemsCount())
                } else if (newItem && !oldItem) {
                    notifyItemRangeInserted(0, getItemsCount())
                } else if (oldItem && newItem) {
                    notifyItemRangeChanged(0, getItemsCount())
                }
                field = loadState
            }
        }



    abstract fun getViewHolder(view: ViewGroup): VH
    abstract fun getItemsCount(): Int

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return getViewHolder(parent)
    }


    final override fun getItemViewType(position: Int): Int = getStateViewType(loadState)

    final override fun getItemCount(): Int =
        if (displayLoadStateAsItem(loadState)) getItemsCount() else 0


    open fun getStateViewType(loadState: LoadState): Int = 0

    open fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return if (loadState is LoadState.Loading) true
        //else if (loadState is LoadState.Error && loadState.error !is EmptyDataException) true
        else false
    }
}
package com.example.ui.state

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.app.databinding.FragmentBaseStateBinding
import com.example.app.databinding.FragmentMaxStateBinding

class UserStateAdapter(val onGetStateClick:(type: UserState) -> Unit): ListAdapter<StateItemModel, ViewHolder<*>>(UserStateDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<*> {
        return when (viewType) {
            1 ->  ViewHolder(
                FragmentMaxStateBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent, false
                )
            )
            else -> ViewHolder(
                    FragmentBaseStateBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent, false
                    )
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder<*>, position: Int) {
        when (getItemViewType(position)) {
            1 -> {
                val holderMax = holder as ViewHolder<FragmentMaxStateBinding>
                holderMax.binding.btnGetState.isVisible = !getItem(position).isDone
                holderMax.binding.tvPercentage.setTypeface(null, Typeface.BOLD)
                holderMax.binding.btnGetState.setOnClickListener {
                    onGetStateClick(UserState.MAX)
                }
                holderMax.binding.executePendingBindings()
            }
            2 -> {
                val holderBase = holder as ViewHolder<FragmentBaseStateBinding>
                holderBase.binding.btnGetState.isInvisible = getItem(position).isDone
                holderBase.binding.tvPercentage.setTypeface(null, Typeface.BOLD)
                holderBase.binding.btnGetState.setOnClickListener {
                    onGetStateClick(UserState.BASE)
                }
                holderBase.binding.executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).state) {
            UserState.BASE -> 2
            else -> 1
        }
    }
}

class UserStateDiffCallback: DiffUtil.ItemCallback<StateItemModel>() {

    override fun areItemsTheSame(oldItem: StateItemModel, newItem: StateItemModel): Boolean =
            oldItem.state == newItem.state

    override fun areContentsTheSame(oldItem: StateItemModel, newItem: StateItemModel): Boolean =
            oldItem == newItem
}

data class StateItemModel(val state: UserState, val isDone: Boolean)

enum class UserState {BASE, MAX}
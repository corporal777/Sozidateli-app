package com.example.ui.state

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class ViewHolder<T : ViewDataBinding>(val binding : T) : RecyclerView.ViewHolder(binding.root)
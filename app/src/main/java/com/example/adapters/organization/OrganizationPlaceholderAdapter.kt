package com.example.adapters.organization

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.adapters.CustomLoadStateAdapter
import com.example.adapters.UserPlaceholderAdapter
import com.example.app.R
import com.example.app.databinding.ItemOrganizationPlaceholderBinding
import com.example.app.databinding.ItemUserPlaceholderBinding

class OrganizationPlaceholderAdapter(val count: Int) :
    CustomLoadStateAdapter<OrganizationPlaceholderAdapter.OrgPlaceholderViewHolder>() {


    override fun getViewHolder(view: ViewGroup): OrgPlaceholderViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(view.context)
        return OrgPlaceholderViewHolder(
            layoutInflater.inflate(
                R.layout.item_organization_placeholder,
                view,
                false
            )
        )
    }

    override fun getItemsCount(): Int = count


    override fun onBindViewHolder(holder: OrgPlaceholderViewHolder, position: Int) {
        holder.bind()
    }

    inner class OrgPlaceholderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val viewBinding by viewBinding(ItemOrganizationPlaceholderBinding::bind)

        fun bind() {
            with(viewBinding) {}
        }
    }
}
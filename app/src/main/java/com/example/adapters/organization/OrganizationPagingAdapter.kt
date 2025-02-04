package com.example.adapters.organization

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.adapters.CustomLoadStateAdapter
import com.example.adapters.EventPagingAdapter
import com.example.app.R
import com.example.app.databinding.ItemOrganizationBinding
import com.example.data.models.Organization
import com.example.data.models.OrganizationNew
import com.example.exceptions.EmptyDataException
import com.example.ui.views.UserSubscribeButton
import com.example.util.getColorStateList
import com.example.util.setImage

class OrganizationPagingAdapter(
    private val onOrganizationClick: (org : OrganizationNew) -> Unit,
    private val onSubscribeClick: (org : OrganizationNew) -> Unit
) : PagingDataAdapter<OrganizationNew, OrganizationPagingAdapter.OrgViewHolder>(AsyncDiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrgViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return OrgViewHolder(inflater.inflate(R.layout.item_organization, parent, false))
    }

    override fun onBindViewHolder(holder: OrgViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    fun updateOrganization(organization: OrganizationNew) {
        snapshot().items.find { x -> x.id == organization.id }.let { local ->
            if (local != null) {
                local.binds?.userFavorite = organization.binds?.userFavorite
                val position = snapshot().items.indexOf(local)
                notifyItemChanged(position)
            }
        }
    }

    inner class OrgViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val viewBinding by viewBinding(ItemOrganizationBinding::bind)

        fun bind(organization: OrganizationNew) {
            viewBinding.apply {
                tvOrganizationName.text = organization.getOrganizationName()
                tvOrganizationImageName.apply {
                    text = organization.legalInformation?.name?.short
                    clipToOutline = true
                    backgroundTintList = if (!organization.backgroundColor?.value.isNullOrEmpty())
                        ColorStateList.valueOf(Color.parseColor(organization.backgroundColor?.value))
                    else getColorStateList(R.color.colorAccent)
                }

                btnAction.apply {
                    setAction(
                        if (organization.binds?.userFavorite != null)
                            UserSubscribeButton.Action.UNFAVORITE
                        else UserSubscribeButton.Action.FAVORITE
                    )
                    setOnClickListener { onSubscribeClick.invoke(organization) }
                }

                ivOrganizationImage.apply {
                    val image = organization.logo.let { it?.uri }
                    clipToOutline = true
                    setImage(image, 300)
                }
                root.setOnClickListener { onOrganizationClick.invoke(organization) }
            }
        }
    }

    private object AsyncDiffCallback : DiffUtil.ItemCallback<OrganizationNew>() {
        override fun areItemsTheSame(oldItem: OrganizationNew, newItem: OrganizationNew): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: OrganizationNew,
            newItem: OrganizationNew
        ): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        fun OrganizationPagingAdapter.withLoadStateAdapters(
            header: CustomLoadStateAdapter<*>,
            footer: CustomLoadStateAdapter<*>,
            onEmpty: (show: Boolean) -> Unit
        ): ConcatAdapter {
            addOnPagesUpdatedListener {}
            addLoadStateListener { loadState ->

                if (itemCount > 0) header.loadState = header.notRefresh
                else header.loadState = loadState.refresh

                //refresh.loadState = if (isRefresh) refresh.notRefresh else loadState.refresh
                //refresh.loadState = loadState.refresh
                footer.loadState = loadState.append

                if (loadState.refresh is LoadState.Error)
                    if ((loadState.refresh as LoadState.Error).error is EmptyDataException)
                        if (this.snapshot().isEmpty()) onEmpty.invoke(true)
                        else onEmpty.invoke(false)
                    else onEmpty.invoke(false)
                else onEmpty.invoke(false)
            }
            return ConcatAdapter(header, this, footer)
        }
    }
}
package com.example.adapters.organization

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.examle.data.models.OrganizationNew
import com.example.adapters.CustomLoadStateAdapter
import com.example.app.R
import com.example.app.databinding.ItemOrganizationBinding
import com.example.extensions.executePlaceholderLoadState
import com.example.extensions.getColorStateList
import com.example.extensions.setImage
import com.example.ui.views.UserSubscribeButton
import dev.androidbroadcast.vbpd.viewBinding

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
                ivOrganizationImage.apply {
                    clipToOutline = true
                    setImage(organization.logo.let { it?.uri }, 300)
                }
                btnAction.apply {
                    setAction(
                        if (organization.binds?.userFavorite != null)
                            UserSubscribeButton.Action.UNFAVORITE
                        else UserSubscribeButton.Action.FAVORITE
                    )
                    setOnClickListener {
                        showProgress(true)
                        onSubscribeClick.invoke(organization)
                    }
                }
                btnProgress.isVisible = false

                root.setOnClickListener { onOrganizationClick.invoke(organization) }
            }
        }

        private fun showProgress(show: Boolean) {
            viewBinding.btnProgress.isVisible = show
            viewBinding.btnAction.isInvisible = show
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
            addLoadStateListener { loadState ->
                header.loadState = if (itemCount > 0) header.notRefresh else loadState.refresh
                footer.loadState = loadState.append
                executePlaceholderLoadState(loadState){ onEmpty.invoke(it) }
            }
            return ConcatAdapter(header, this, footer)
        }
    }
}
package com.example.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.app.R
import com.example.app.databinding.ItemEventNewBinding
import com.example.app.databinding.ItemOrganizationBinding
import com.example.app.databinding.ItemOrganizationInfoBinding
import com.example.data.models.OrganizationNew
import com.example.data.models.UserDetail
import com.example.ui.views.UserSubscribeButton
import com.example.util.setImage
import com.squareup.picasso.Picasso

class OrganizationPagingAdapter(
    private val onOrganizationClick: () -> Unit,
    private val onSubscribeClick: () -> Unit
) {

    inner class OrgViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val viewBinding by viewBinding(ItemOrganizationBinding::bind)

        fun bind(organization: OrganizationNew) {
            viewBinding.apply {
                tvOrganizationName.text = organization.getOrganizationName()
                tvOrganizationImageName.apply {
                    text = organization.legalInformation?.name?.short
                    clipToOutline = true
                    if (!organization.backgroundColor?.value.isNullOrEmpty())
                        ViewCompat.setBackgroundTintList(
                            this,
                            ColorStateList.valueOf(Color.parseColor(organization.backgroundColor?.value))
                        )
                    else
                        ViewCompat.setBackgroundTintList(
                            this, ColorStateList.valueOf(
                                ResourcesCompat.getColor(resources, R.color.colorAccent, null)
                            )
                        )
                }

                btnAction.apply {
                    setAction(
                        if (organization.binds?.userFavorite != null)
                            UserSubscribeButton.Action.UNFAVORITE
                        else UserSubscribeButton.Action.FAVORITE
                    )
                    setOnClickListener { onSubscribeClick.invoke() }
                }

                ivOrganizationImage.apply {
                    clipToOutline = true
                    setImage(
                        organization.logo.let { if (it?.uri.isNullOrBlank()) null else it?.uri },
                        300
                    )
                }
                root.setOnClickListener { onOrganizationClick.invoke() }
            }
        }
    }
    private object AsyncDiffCallback : DiffUtil.ItemCallback<OrganizationNew>() {
        override fun areItemsTheSame(oldItem: OrganizationNew, newItem: OrganizationNew): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrganizationNew, newItem: OrganizationNew): Boolean {
            return oldItem == newItem
        }
    }
}
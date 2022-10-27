package com.example.ui.event.about.redesign.items

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.ItemEventDetailImageBlockBinding
import com.example.extensions.formatToEventDatesIntervalOnMain
import com.example.util.setImage
import com.xwray.groupie.databinding.BindableItem
import parseColor

class EventDetailImageItem(
    val name: String?,
    val address: String?,
    val dateFrom: String?,
    val dateTo: String?,
    val logo: String?,
    val backgroundColor: String?
) : BindableItem<ItemEventDetailImageBlockBinding>() {

    val date: String = dateFrom.formatToEventDatesIntervalOnMain(dateTo) ?: ""
    private var imageColor = ColorDrawable(Color.DKGRAY)

    init {
        if (!backgroundColor.isNullOrEmpty()){
            val color = backgroundColor.parseColor()?:Color.DKGRAY
            imageColor = ColorDrawable(color)
        }
    }

    override fun bind(viewBinding: ItemEventDetailImageBlockBinding, position: Int) {
        viewBinding.apply {
            tvTitle.text = name
            tvDate.text = date
            tvLocation.apply {
                isVisible = !address.isNullOrEmpty()
                text = address
            }

            ivLogo.apply {
                setImage(logo ?: imageColor)
            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is EventDetailImageItem) return false
        if (name != other.name) return false
        if (address != other.address) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_event_detail_image_block


}
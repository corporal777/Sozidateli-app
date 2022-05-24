package com.example.holders.redesign.blocks

import android.annotation.SuppressLint
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import coil.request.DefaultRequestOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.R
import com.example.data.models.EventFormat
import com.example.data.models.EventNew
import com.example.databinding.ItemEventDetailImageBlockBinding
import com.example.extensions.formatToEventDatesIntervalOnMain
import com.example.util.setImage
import com.xwray.groupie.databinding.BindableItem

class EventDetailImageBlock (
    private val eventData: EventNew?
) : BindableItem<ItemEventDetailImageBlockBinding>() {

    val backgroundColor: String? = eventData?.binds?.organization?.backgroundColor?.value
    val logo: String? = eventData?.image?.uri

    val date: String =
        eventData?.holdingDate?.from.formatToEventDatesIntervalOnMain(eventData?.holdingDate?.to)
            ?: ""

    @SuppressLint("CheckResult")
    override fun bind(viewBinding: ItemEventDetailImageBlockBinding, position: Int) {
        viewBinding.apply {

            tvDate.text = date
            tvLocation.text = eventData?.address?.getShortAddress()
            tvTitle.text = eventData?.name
            ivLogo.setImage(logo)

//            Glide.with(viewBinding.root.context)
//                .load(logo)
//                .apply(RequestOptions.placeholderOf(R.drawable.background_image_placeholder)
//                    .error(R.drawable.background_image_placeholder)
//                    .override(1700, 1500))
//                .into(ivLogo)

        }
    }

    override fun getLayout(): Int = R.layout.item_event_detail_image_block


}
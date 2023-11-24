package com.example.holders

import com.example.R
import com.example.databinding.ItemRatingBinding
import com.xwray.groupie.databinding.BindableItem
import kotlin.math.roundToInt

class RatingItem(
    private var rating: Int,
    private val onRatingChange: (rating: Int) -> Unit
) : BindableItem<ItemRatingBinding>() {

    private val disabled = rating > 0

    override fun bind(viewBinding: ItemRatingBinding, position: Int) {
        viewBinding.ratingBar.apply {
            rating = this@RatingItem.rating.toFloat()
            setIsIndicator(disabled)
            setOnRatingBarChangeListener { _, rating, _ ->
                val ratingInt = rating.roundToInt()
                this@RatingItem.rating = ratingInt
                onRatingChange(ratingInt)
            }
        }
    }

    override fun getLayout() = R.layout.item_rating
}
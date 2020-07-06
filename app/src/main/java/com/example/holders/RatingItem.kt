package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_rating.*
import kotlin.math.roundToInt

class RatingItem(
        private var rating: Int,
        private val onRatingChange: (rating: Int) -> Unit
) : Item() {

    private val disabled = rating > 0

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.ratingBar.apply {
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
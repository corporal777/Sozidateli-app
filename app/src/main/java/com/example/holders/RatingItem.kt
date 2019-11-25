package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_rating.*
import kotlin.math.roundToInt

class RatingItem(
        private val onRatingChange: (rating: Int) -> Unit
) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
                onRatingChange(rating.roundToInt())
            }
        }
    }

    override fun getLayout() = R.layout.item_rating
}
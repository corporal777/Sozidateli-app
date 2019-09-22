package com.example.ui.views

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.example.R
import kotlinx.android.synthetic.main.layout_rate.view.*

object EventRatingDialog {

    fun show(context: Context, onRateClick: (rating: Float) -> Unit): AlertDialog {
        val ratingView = LayoutInflater.from(context).inflate(R.layout.layout_rate, null)
        return AlertDialog.Builder(context)
                .setTitle(R.string.notifications_rate_event)
                .setView(ratingView)
                .setPositiveButton(R.string.rate) { _, _ -> onRateClick(ratingView.ratingBar.rating) }
                .setNegativeButton(R.string.cancel) { _, _ -> }
                .show()
    }
}
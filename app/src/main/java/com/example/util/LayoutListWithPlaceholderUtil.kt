package com.example.util

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.R
import com.example.ui.views.AutoPlaceholderRecyclerView

class LayoutListWithPlaceholderUtil(
        layout: View
) {

    private val recyclerView = layout.findViewById<AutoPlaceholderRecyclerView>(R.id.recyclerView)
    private val placeholder = layout.findViewById<ConstraintLayout>(R.id.placeholder)
    private val textView = layout.findViewById<TextView>(R.id.tvPlaceholderMessage)
    private val imageView = layout.findViewById<ImageView>(R.id.ivPlaceholderImage)

    init {
        recyclerView.apply {
            hideOnEmpty = true
            placeholderView = placeholder
            canShowPlaceholder = !doNotShowUntilDataLoad
        }
    }

    var doNotShowUntilDataLoad = false
        set(value) {
            field = value
            recyclerView.canShowPlaceholder = !value
        }

    var isDataLoad: Boolean = false
        set(value) {
            field = value
            recyclerView.canShowPlaceholder = value || !doNotShowUntilDataLoad
        }

    fun setDefault() {
        doNotShowUntilDataLoad = true
        textView.apply { text = context.getString(R.string.empty_list_placeholder_message) }
        imageView.apply { setImageResource(R.drawable.ic_neutral_face) }
    }

    fun setMessage(message: String?) {
        textView.apply {
            if (message == null) {
                text = ""
                visibility = GONE
            } else {
                text = message
                visibility = VISIBLE
            }
        }
    }

    fun setImage(@DrawableRes res: Int) {
        imageView.apply {
            setImageResource(res)
        }
    }
}
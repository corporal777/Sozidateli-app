package com.example.ui.views

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.example.R
import com.google.android.material.chip.Chip
import dp

class TagChip : Chip {

    private val textColor by lazy { ContextCompat.getColor(context, R.color.tag_text) }
    private val textColorSelected by lazy { ContextCompat.getColor(context, R.color.tag_selected_text) }

    private var checkedChangeListener: OnCheckedChangeListener? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        setChipStrokeColorResource(R.color.tag_border)
        chipStrokeWidth = 1f.dp
        checkedIcon = null
        super.setOnCheckedChangeListener { _, _ ->
            processCheckedState()
            checkedChangeListener?.onCheckedChanged(this, isChecked)
        }
        processCheckedState()
    }

    override fun setOnCheckedChangeListener(listener: OnCheckedChangeListener?) {
        this.checkedChangeListener = listener
    }

    private fun processCheckedState() {
        if (isChecked) {
            setTextColor(textColorSelected)
            setChipBackgroundColorResource(R.color.tag_selected_background)
        } else {
            setTextColor(textColor)
            setChipBackgroundColorResource(R.color.tag_background)
        }
    }
}
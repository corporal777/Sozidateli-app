package com.example.ui.views

import android.content.Context
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class BottomDialog(
        context: Context,
        contentView: View
) : BottomSheetDialog(context) {

    init {
        setContentView(contentView)
        val behavior = BottomSheetBehavior.from(contentView.parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }
}
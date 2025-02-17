package com.example.ui.base.bottomSheet

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

abstract class FullScreenBottomSheetFragment : BaseBSFragment(){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (dialog is BottomSheetDialog) dialog.setFullScreen()
        return dialog
    }

    //    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
//        val bottomSheet: FrameLayout =
//            dialog!!.findViewById(com.google.android.material.R.id.design_bottom_sheet)
//        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
//        showFullScreenBottomSheet(bottomSheet)
//    }
//
//    private fun showFullScreenBottomSheet(bottomSheet: FrameLayout) {
//        val layoutParams = bottomSheet.layoutParams
//        layoutParams.height = Resources.getSystem().displayMetrics.heightPixels - 50
//        bottomSheet.layoutParams = layoutParams
//    }

    private fun BottomSheetDialog.setFullScreen() {
        lifecycleScope.launchWhenStarted {
            val bottomSheetLayout =
                findViewById<ViewGroup>(com.google.android.material.R.id.design_bottom_sheet) ?: return@launchWhenStarted
            with(bottomSheetLayout) {
                updateLayoutParams {
                    height = ViewGroup.LayoutParams.MATCH_PARENT
                }
            }
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
    }
}
package com.example.util

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

class ScrollingChildBehavior<V : View>(context: Context, attrs: AttributeSet?) : BottomSheetBehavior<V>(context, attrs) {

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        return target.scrollY <= 0
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun <V : View> from(view: V): ScrollingChildBehavior<V> {
            val params = view.layoutParams
            if (params !is CoordinatorLayout.LayoutParams) {
                throw IllegalArgumentException("The view is not a child of CoordinatorLayout")
            } else {
                val behavior = params.behavior
                return behavior as? ScrollingChildBehavior<V>
                        ?: throw IllegalArgumentException("The view is not associated with BottomSheetBehavior")
            }
        }
    }
}
package com.example.util;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StickyFooterItemDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull final View view, @NonNull final RecyclerView parent,
                               @NonNull RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);
        int adapterItemCount = parent.getAdapter().getItemCount();
        if (adapterItemCount == RecyclerView.NO_POSITION || (adapterItemCount - 1) != position) {
            return;
        }
        outRect.top = calculateTopOffset(parent, view, adapterItemCount);
    }


    private int calculateTopOffset(RecyclerView parent, View footerView, int itemCount) {
        int topOffset = parent.getHeight() - visibleChildHeightWithFooter(parent, footerView, itemCount)
                - parent.getPaddingTop() - parent.getPaddingBottom();

        return topOffset < 0 ? 0 : topOffset;
    }

    private int visibleChildHeightWithFooter(RecyclerView parent, View footerView, int itemCount) {
        int totalHeight = 0;
        int onScreenItemCount = Math.min(parent.getChildCount(), itemCount);
        RecyclerView.LayoutManager lm = parent.getLayoutManager();
        for (int i = 0; i < onScreenItemCount - 1; i++) {
            View child = parent.getChildAt(i);
            int height = getViewHeight(child, lm);
            totalHeight += height;
        }
        if (footerView.getHeight() == 0) fixLayoutSize(footerView, parent);
        int footerHeight = getViewHeight(footerView, lm);
        return totalHeight + footerHeight;
    }

    private int getViewHeight(View view, RecyclerView.LayoutManager lm) {
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view
                .getLayoutParams();
        return lm.getDecoratedMeasuredHeight(view)
//                + lm.getTopDecorationHeight(view) + lm.getBottomDecorationHeight(view)
                + layoutParams.topMargin + layoutParams.bottomMargin;
    }

    private void fixLayoutSize(View view, ViewGroup parent) {
        // Check if the view has a layout parameter and if it does not create one for it
        if (view.getLayoutParams() == null) {
            view.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        // Create a width and height spec using the parent as an example:
        // For width we make sure that the item matches exactly what it measures from the parent.
        //  IE if layout says to match_parent it will be exactly parent.getWidth()
        int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
        // For the height we are going to create a spec that says it doesn't really care what is calculated,
        //  even if its larger than the screen
        int heightSpec = View.MeasureSpec
                .makeMeasureSpec(parent.getHeight(), View.MeasureSpec.UNSPECIFIED);

        // Get the child specs using the parent spec and the padding the parent has
        int childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                parent.getPaddingLeft() + parent.getPaddingRight(), view.getLayoutParams().width);
        int childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                parent.getPaddingTop() + parent.getPaddingBottom(), view.getLayoutParams().height);

        // Finally we measure the sizes with the actual view which does margin and padding changes to the sizes calculated
        view.measure(childWidth, childHeight);

        // And now we setup the layout for the view to ensure it has the correct sizes.
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    }
}
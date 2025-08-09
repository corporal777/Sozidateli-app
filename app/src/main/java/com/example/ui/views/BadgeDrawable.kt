package com.example.ui.views

import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import com.example.common.sp
import com.google.android.material.shape.MaterialShapeDrawable

class BadgeDrawable(
        var number: Int = 0,
        var maxNumber: Int = 99,
        @ColorInt var badgeBackgroundColor: Int = -0x10000,
        @ColorInt var badgeTextColor: Int = -0x1,
        var badgeTextSize: Float = 11f.sp,
        var badgeTextPadding: Float = 0f,
        var shouldDrawText: Boolean = true
) : Drawable() {

    private val shapeDrawable = MaterialShapeDrawable().apply {
        fillColor = ColorStateList.valueOf(badgeBackgroundColor)
    }

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        color = badgeTextColor
        textSize = badgeTextSize
    }

    init {
        updateBounds()
    }

    fun updateBadgeCoordinates(anchorView: View, customBadgeParent: ViewGroup?, setupBadgeRect: (Rect) -> Unit) {
        calculateBounds(anchorView, customBadgeParent, setupBadgeRect)
        updateBounds()
        invalidateSelf()
    }

    private fun hasNumber() = number > 0

    private fun calculateBounds(anchorView: View, customBadgeParent: ViewGroup?, setupBadgeRect: (Rect) -> Unit) {
        Rect().apply {
            anchorView.getDrawingRect(this)
            customBadgeParent?.offsetDescendantRectToMyCoords(anchorView, this)
            setupBadgeRect(this)
            bounds = this
        }
    }

    private fun updateBounds() {
        val bounds = bounds
        shapeDrawable.setBounds(
                bounds.left,
                bounds.top,
                (bounds.left + badgeTextSize * 2 + badgeTextPadding * 2).toInt(),
                (bounds.top + badgeTextSize * 2 + badgeTextPadding * 2).toInt()
        )

        shapeDrawable.setCornerSize(shapeDrawable.bounds.width() / 2f)
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        updateBounds()
    }

    override fun draw(canvas: Canvas) {
        val bounds = bounds
        if (!hasNumber() || bounds.isEmpty || alpha == 0 || !isVisible) {
            return
        }
        shapeDrawable.draw(canvas)
        if (shouldDrawText) drawText(canvas)
    }

    private fun drawText(canvas: Canvas) {
        val countText = getBadgeText()
        val badgeBounds = shapeDrawable.bounds

        val textBounds = Rect()
        textPaint.getTextBounds(countText, 0, countText.length, textBounds)

        canvas.drawText(
                countText,
                badgeBounds.left + badgeBounds.width() / 2f,
                badgeBounds.top + badgeBounds.height() / 2f + textBounds.height() / 2f,
                textPaint)
    }

    private fun getBadgeText(): String {
        return if (number <= maxNumber) number.toString() else "$maxNumber+"
    }

    override fun getIntrinsicHeight() = shapeDrawable.bounds.height()

    override fun getIntrinsicWidth() = shapeDrawable.bounds.width()

    override fun setAlpha(alpha: Int) {

    }

    override fun getOpacity() = PixelFormat.TRANSLUCENT

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }
}

fun View.addBadge(badge: BadgeDrawable, customBadgeParent: ViewGroup? = null, setupBadgeRect: (badgeWidth: Int, badgeHeight: Int, anchorRect: Rect) -> Unit) {
    val parent = customBadgeParent ?: this.parent as ViewGroup
    parent.apply {
        clipChildren = false
        clipToPadding = false

        badge.apply {
            updateBadgeCoordinates(this@addBadge, parent) {
                setupBadgeRect(intrinsicWidth, intrinsicHeight, it)
            }
            overlay.add(this)
        }
    }
}
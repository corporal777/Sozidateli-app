package com.example.ui.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.example.R
import uk.co.chrisjenx.calligraphy.CalligraphyUtils

class UserSubscribeButton : AppCompatButton {

    private val actionSubscribeTextColor by lazy { ContextCompat.getColor(context, R.color.profile_action_subscribe) }
    private val actionUnblockTextColor by lazy { ContextCompat.getColor(context, R.color.profile_action_ban) }

    private val actionSubscribeImage by lazy { ContextCompat.getDrawable(context, R.drawable.ic_star) }
    private val actionUnsubscribeImage by lazy { ContextCompat.getDrawable(context, R.drawable.ic_star_filled) }

    private val actionSubscribeText by lazy { resources.getString(R.string.subscribe) }
    private val actionUnsubscribeText by lazy { resources.getString(R.string.unsubscribe) }
    private val actionFavoriteText by lazy { resources.getString(R.string.add_to_favorites) }
    private val actionUnfavoriteText by lazy { resources.getString(R.string.remove_from_favorites) }
    private val actionUnblockText by lazy { resources.getString(R.string.unblock) }

    var action = Action.FAVORITE
        private set

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        obtainAttributes(attrs)
    }

    init {
        CalligraphyUtils.applyFontToTextView(context, this, "fonts/Roboto-Medium.ttf")
    }

    private fun obtainAttributes(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.UserSubscribeButton)
        val action = a.getInt(R.styleable.UserSubscribeButton_subscribeAction, 0)

        a.recycle()

        setAction(Action.values()[action])
    }

    fun setAction(action: Action) {
        when (action) {
            Action.SUBSCRIBE -> setActionSubscribe()
            Action.UNSUBSCRIBE -> setActionUnsubscribe()
            Action.FAVORITE -> setActionFavorite()
            Action.UNFAVORITE -> setActionUnfavorite()
            Action.UNBLOCK -> setActionUnblock()
        }
    }

    fun setActionSubscribe() {
        action = Action.SUBSCRIBE
        changeAction(actionSubscribeText, actionSubscribeTextColor, actionSubscribeImage)
    }

    fun setActionUnsubscribe() {
        action = Action.UNSUBSCRIBE
        changeAction(actionUnsubscribeText, actionSubscribeTextColor, actionUnsubscribeImage)
    }

    fun setActionFavorite() {
        action = Action.FAVORITE
        changeAction(actionFavoriteText, actionSubscribeTextColor, actionSubscribeImage)
    }

    fun setActionUnfavorite() {
        action = Action.UNFAVORITE
        changeAction(actionUnfavoriteText, actionSubscribeTextColor, actionUnsubscribeImage)
    }

    fun setActionUnblock() {
        action = Action.UNBLOCK
        changeAction(actionUnblockText, actionUnblockTextColor, null)
    }

    private fun changeAction(text: String, textColor: Int, drawable: Drawable?) {
        this.text = text
        this.setTextColor(textColor)
        this.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
    }

    override fun onSaveInstanceState(): Parcelable? {
        return bundleOf(
                STATE_SUPER to super.onSaveInstanceState(),
                STATE_ACTION to action
        )
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val bundle = state as? Bundle
        if (bundle != null) {
            super.onRestoreInstanceState(bundle.getParcelable(STATE_SUPER))
            val action = bundle.getSerializable(STATE_ACTION) as Action
            setAction(action)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    companion object {
        private const val STATE_SUPER = "UserSubscribeButton:super"
        private const val STATE_ACTION = "UserSubscribeButton:action"
    }

    enum class Action {
        SUBSCRIBE,
        UNSUBSCRIBE,
        FAVORITE,
        UNFAVORITE,
        UNBLOCK
    }
}
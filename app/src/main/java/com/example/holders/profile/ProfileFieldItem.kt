package com.example.holders.profile

import android.text.TextWatcher
import com.example.R
import com.example.data.models.ProfileField
import com.example.util.SimpleTextWatcher
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.field_profile.view.*

abstract class ProfileFieldItem(private val profileField: ProfileField) : ProfileBaseFieldItem(profileField) {

    protected open var maxLines: Int? = null

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        viewHolder.itemView.apply {

            profileField.data?.let {
                when (it) {
                    is String -> {
                        editText.setText(it)
                    }
                }
            }
            profileField.label?.let {
                tvFieldLabel.text = it
            }
            editText.inputType = getInputType()

            getTextChangeListener()?.let {
                editText.addTextChangedListener(it)
            }

            maxLines?.let {
                editText.maxLines = it
            }
        }
    }

    open fun getTextChangeListener(): TextWatcher? {
        return SimpleTextWatcher().setAfterTextChangeRunnable {
            profileField.data = it.toString()
        }
    }

    abstract fun getInputType(): Int


    override fun getLayout() = R.layout.field_profile
}
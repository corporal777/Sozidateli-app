package com.example.ui.support.items

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.MotionEvent
import android.view.View
import androidx.core.app.ActivityCompat
import com.example.app.R
import com.example.app.databinding.ItemSupportCenterHeaderBinding
import com.example.util.VoiceInput
import com.xwray.groupie.databinding.BindableItem
import java.util.*

class SupportHeaderItem(
    val context: Context,
    val onSearchClick: () -> Unit,
    val onMicrophoneClick: () -> Unit
) : BindableItem<ItemSupportCenterHeaderBinding>(-1001L) {

    override fun bind(viewBinding: ItemSupportCenterHeaderBinding, position: Int) {
        viewBinding.apply {
            etSearch.apply {
                setOnClickListener {
                    onSearchClick.invoke()
                }
            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is SupportHeaderItem) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_support_center_header
}
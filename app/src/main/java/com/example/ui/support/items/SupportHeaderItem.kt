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
import com.example.R
import com.example.databinding.ItemSupportCenterHeaderBinding
import com.example.util.VoiceInput
import com.xwray.groupie.databinding.BindableItem
import java.util.*

class SupportHeaderItem(
    val context: Context,
    val onSearchClick: () -> Unit,
    val onMicrophoneClick: () -> Unit
) : BindableItem<ItemSupportCenterHeaderBinding>(-1001L) {

    private val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
    private val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
    }

    override fun bind(viewBinding: ItemSupportCenterHeaderBinding, position: Int) {
        viewBinding.apply {
            etSearch.apply {
                setOnClickListener {
                    onSearchClick.invoke()
                }
            }
            btnMicrophone.apply {
//                VoiceInput(this, speechRecognizer, speechRecognizerIntent).apply {
//                    setOnBeginListening {
//
//                    }
//                    setOnFinishListening {
//                        etSearch.setText(it)
//                    }
//                }
            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is SupportHeaderItem) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_support_center_header
}
package com.example.util

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View

class VoiceInput(
    val view: View,
    val speechRecognizer: SpeechRecognizer,
    val intent: Intent
) {

    private var onBeginListening: OnBeginListening? = null
    private var onFinishListening: OnFinishListening? = null

    init {
        view.setOnClickListener {
            if (SpeechRecognizer.isRecognitionAvailable(view.context)) {
                if (speechRecognizer != null) speechRecognizer.startListening(intent)
            }
        }
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle?) {
                view.isEnabled = false
            }

            override fun onBeginningOfSpeech() {
                onBeginListening?.invoke()
            }

            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(i: Int) {
                view.isEnabled = true
                catchError(i)
            }

            override fun onResults(bundle: Bundle) {
                view.isEnabled = true
                val data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                onFinishListening?.invoke(data?.firstOrNull())
                speechRecognizer.stopListening()
            }

            override fun onPartialResults(bundle: Bundle?) {}
            override fun onEvent(i: Int, bundle: Bundle?) {}
        })
    }

    fun setOnBeginListening(onBeginListening: OnBeginListening) {
        this.onBeginListening = onBeginListening
    }

    fun setOnFinishListening(onFinishListening: OnFinishListening) {
        this.onFinishListening = onFinishListening
    }

    private fun catchError(i: Int) {
        var error = ""
        when (i) {
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> error = " network timeout"
            SpeechRecognizer.ERROR_NETWORK -> error = " network"
            SpeechRecognizer.ERROR_AUDIO -> error = " audio"
            SpeechRecognizer.ERROR_SERVER ->  error = " server"
            SpeechRecognizer.ERROR_CLIENT -> error = " client"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> error = " speech time out"
            SpeechRecognizer.ERROR_NO_MATCH -> error = " no match"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> error = " recogniser busy"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> error = " insufficient permissions"
        }
        Log.e("Error Voice - ", error)
    }
}

typealias OnBeginListening = () -> Unit
typealias OnFinishListening = (String?) -> Unit
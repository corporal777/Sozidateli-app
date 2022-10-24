package com.example.util.qr_generator


import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch



class QrCodeCreationException(cause : Throwable? = null, message: String? = null) :
    Exception(message, cause)

interface QrCodeGenerator {

    fun generateQrCode(data: QrData, options: QrOptions) : Bitmap

    suspend fun generateQrCodeSuspend(data: QrData, options: QrOptions) : Bitmap
}

fun QrCodeGenerator(
    context: Context,
    threadPolicy: ThreadPolicy = ThreadPolicy.SingleThread
) : QrCodeGenerator = QrCodeGeneratorImpl(context,threadPolicy)

enum class ThreadPolicy {

    SingleThread {
        override suspend operator fun invoke(
            width : Int, height : Int, block: (IntRange, IntRange) -> Unit
        ) {
            block((0 until width), (0 until height))
        }
    },

    DoubleThread {
        override suspend operator fun invoke(
            width : Int, height : Int, block: (IntRange, IntRange) -> Unit
        ) {
            coroutineScope {
                listOf(
                    (0 until width) to (0 until height / 2),
                    (0 until width) to (height / 2 until height),
                ).map {
                    launch(Dispatchers.Default) {
                        block(it.first, it.second)
                    }
                }
            }.joinAll()
        }
    },

    QuadThread {
        override suspend operator fun invoke(
            width : Int, height : Int, block: (IntRange, IntRange) -> Unit
        ) {
            coroutineScope {
                listOf(
                    (0 until width / 2) to (0 until height / 2),
                    (0 until width / 2) to (height / 2 until height),
                    (width / 2 until width) to (0 until height / 2),
                    (width / 2 until width) to (height / 2 until height)
                ).map {
                    launch(Dispatchers.Default) {
                        block(it.first, it.second)
                    }
                }
            }.joinAll()
        }
    };

    abstract suspend operator fun invoke(
        width : Int, height : Int, block : (IntRange, IntRange) -> Unit
    )
}
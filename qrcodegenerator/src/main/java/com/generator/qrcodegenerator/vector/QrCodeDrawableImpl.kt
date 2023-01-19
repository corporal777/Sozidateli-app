package com.generator.qrcodegenerator.vector

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.graphics.withTranslation
import com.generator.qrcodegenerator.QrData
import com.generator.qrcodegenerator.encoder.QrCodeMatrix
import com.generator.qrcodegenerator.encoder.neighbors
import com.generator.qrcodegenerator.encoder.toQrMatrix
import com.generator.qrcodegenerator.style.DrawableSource
import com.generator.qrcodegenerator.style.Neighbors
import com.generator.qrcodegenerator.style.QrLogoShape
import com.generator.qrcodegenerator.vector.style.QrVectorColor
import com.google.zxing.qrcode.encoder.Encoder
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToInt


fun QrCodeDrawable(
    context: Context,
    data: QrData,
    options: QrVectorOptions,
): Drawable {
    return QrCodeDrawableImpl(context, data, options)
}


internal class QrCodeDrawableImpl(
    private val context: Context,
    data: QrData,
    private val options: QrVectorOptions,
) : Drawable() {

    private var codeMatrix: QrCodeMatrix = Encoder.encode(
        data.encode(), options.errorCorrectionLevel.lvl, null
    )
        .matrix.toQrMatrix()

    private var modifiedMatrix: QrCodeMatrix = codeMatrix
        .copy()

    private var mColorFilter: ColorFilter? = null
    private var mAlpha = 255

    override fun setAlpha(alpha: Int) {
        mAlpha = alpha
        listOf(darkPixelPaint, lightPixelPaint, ballPaint, framePaint)
            .onEach {
                it.alpha = alpha
            }
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mColorFilter = colorFilter
        listOf(darkPixelPaint, lightPixelPaint, ballPaint, framePaint)
            .onEach {
                it.colorFilter = colorFilter
            }

    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("PixelFormat.TRANSLUCENT", "android.graphics.PixelFormat")
    )
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    private var size: Float = 0f

    private var pixelSize: Float = 0f
    private var ballPath: Path = Path()
    private var framePath: Path = Path()

    private var logo: Bitmap? = null

    private val darkPixelPath: Path = Path()
    private val lightPixelPath: Path = Path()

    private var darkPixelPaint: Paint = Paint()
    private var lightPixelPaint: Paint = Paint()
    private var framePaint: Paint = Paint()
    private var ballPaint: Paint = Paint()

    override fun setBounds(bounds: Rect) {
        super.setBounds(bounds)
        resize()
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        resize()
    }

    override fun draw(canvas: Canvas) {
            canvas.drawColor(Color.WHITE)
        if (pixelSize < Float.MIN_VALUE)
            return

        val (w, h) = with(bounds) { width() to height() }
        val (offsetX, offsetY) = with(options.offset) { listOf(x, y) }
            .map { it.coerceIn(-1f, 1f) + 1 }

        canvas.withTranslation(
            (w - size) / 2f * offsetX,
            (h - size) / 2f * offsetY
        ) {

            drawPath(darkPixelPath, darkPixelPaint)
            drawPath(lightPixelPath, lightPixelPaint)

            if (options.colors.ball !is QrVectorColor.Unspecified) {
                listOf(
                    2 to 2,
                    2 to codeMatrix.size - 5,
                    codeMatrix.size - 5 to 2
                ).forEach {
                    withTranslation(
                        it.first * pixelSize,
                        it.second * pixelSize
                    ) {
                        drawPath(ballPath, ballPaint)
                    }
                }
            }

            if (options.colors.frame !is QrVectorColor.Unspecified) {
                listOf(
                    0 to 0,
                    0 to codeMatrix.size - 7,
                    codeMatrix.size - 7 to 0
                ).forEach {
                    withTranslation(
                        it.first * pixelSize,
                        it.second * pixelSize
                    ) {
                        drawPath(framePath, framePaint)
                    }
                }
            }

            val nLogo = logo
            if (nLogo != null) {
                if (options.logo.shape is QrLogoShape.RoundCorners) {
                    val px = options.logo.shape.corner * 100
                    val roundedBm = getRoundedBitmap(nLogo, px * 2.5f)
                    val centreX = (canvas.width - roundedBm.width) / 2.35f
                    val centreY = (canvas.height - roundedBm.height) / 2.35f
                    drawBitmap(roundedBm, centreX, centreY, null)
                } else {
                    val roundedBm = getCircleBitmap(nLogo)
                    val centreX = (canvas.width - roundedBm.width) / 2.35f
                    val centreY = (canvas.height - roundedBm.height) / 2.35f
                    drawBitmap(roundedBm, centreX, centreY, null)
                }
            }

//            if (nLogo != null) {
//                val centreX = (bounds.width()) / 3.5f
//                //val centreX = (bounds.width()  - nLogo.width) / 2.5f + options.padding
//                val centreY = (bounds.height()) / 3.5f
//                //val centreY = (bounds.height() - nLogo.height) / 2.5f+ options.padding
//
//                val paint = Paint()
//                paint.setColor(Color.WHITE)
//                paint.style = Paint.Style.FILL
//
//                //drawBitmap(nLogo, centreX, centreY, null)
//
//                //val (x,y) = (w - nLogo.width) /3f to (h - nLogo.height) /3f
//                val (x, y) = (nLogo.width) / 3f to (nLogo.height) / 3f
//                //drawBitmap(nLogo, x,y, null)
//
//            }
        }
    }

    private fun resize() {
        size = minOf(bounds.width(), bounds.height()) *
                (1 - options.padding.coerceIn(0f, .5f))
        pixelSize = size / codeMatrix.size

        ballPath = options.shapes.ball.createPath(pixelSize * 3f, Neighbors.Empty)
        framePath = options.shapes.frame.createPath(pixelSize * 7f, Neighbors.Empty)

        val singleDarkPixelPath = with(options.shapes.darkPixel) {
            if (isDependOnNeighbors)
                null else createPath(
                pixelSize,
                Neighbors.Empty
            )
        }
        val singleLightPixelPath = with(options.shapes.lightPixel) {
            if (isDependOnNeighbors)
                null else createPath(pixelSize, Neighbors.Empty)
        }
        darkPixelPaint = options.colors.dark.createPaint(
            codeMatrix.size * pixelSize,
            codeMatrix.size * pixelSize,
        )
        lightPixelPaint = options.colors.light.createPaint(
            codeMatrix.size * pixelSize,
            codeMatrix.size * pixelSize
        )
        ballPaint = options.colors.ball.createPaint(
            pixelSize * 3f,
            pixelSize * 3f,
        )
        framePaint = options.colors.frame.createPaint(
            pixelSize * 7f,
            pixelSize * 7f,
        )

        colorFilter = mColorFilter
        alpha = mAlpha

        darkPixelPath.reset()
        lightPixelPath.reset()

        modifiedMatrix = codeMatrix.copy()
        logo = if (options.logo.drawable != DrawableSource.Empty) {
            val logoSize = size * options.logo.size
            val logoDrawable = runBlocking {
                options.logo.drawable.get(context)
            }

            val logoSizeInQrPixels = (logoSize * (1 + options.logo.padding.value) / pixelSize)
                .roundToInt()
            val start = (codeMatrix.size - logoSizeInQrPixels) / 2
            val end = (codeMatrix.size + logoSizeInQrPixels) / 2

            for (x in start until end) {
                for (y in start until end) {
                    kotlin.runCatching {
                        if (options.logo.shape.invoke(
                                (x - start),
                                (y - start),
                                logoSizeInQrPixels,
                                Neighbors.Empty
                            )
                        ) {
                            modifiedMatrix[x, y] = QrCodeMatrix.PixelType.Logo
                        }
                    }
                }
            }

            options.logo.scale.scale(
                logoDrawable, logoSize.roundToInt(), logoSize.roundToInt()
            )
        } else null


        for (x in 0 until codeMatrix.size) {
            for (y in 0 until codeMatrix.size) {

                when {
                    options.colors.frame is QrVectorColor.Unspecified &&
                            x == 0 && y == 0 ||
                            x == 0 && y == codeMatrix.size - 7 ||
                            x == codeMatrix.size - 7 && y == 0 -> darkPixelPath.addPath(
                        framePath,
                        x.toFloat() * pixelSize,
                        y.toFloat() * pixelSize
                    )

                    options.colors.ball is QrVectorColor.Unspecified &&
                            x == 2 && y == codeMatrix.size - 5 ||
                            x == codeMatrix.size - 5 && y == 2 ||
                            x == 2 && y == 2 -> {
                        darkPixelPath.addPath(
                            ballPath,
                            x.toFloat() * pixelSize,
                            y.toFloat() * pixelSize
                        )
                    }

                    x in 0..6 && y in 0..6 ||
                            x < 7 && y in codeMatrix.size - 7 until codeMatrix.size ||
                            x in codeMatrix.size - 7 until codeMatrix.size && y < 7 -> Unit

                    else -> when (modifiedMatrix[x, y]) {
                        QrCodeMatrix.PixelType.DarkPixel ->
                            darkPixelPath.addPath(
                                singleDarkPixelPath ?: options.shapes.darkPixel.createPath(
                                    pixelSize,
                                    codeMatrix.neighbors(x, y)
                                ),
                                x.toFloat() * pixelSize, y.toFloat() * pixelSize
                            )
                        QrCodeMatrix.PixelType.LightPixel ->
                            lightPixelPath.addPath(
                                singleLightPixelPath ?: options.shapes.lightPixel.createPath(
                                    pixelSize, codeMatrix.neighbors(x, y)
                                ),
                                x.toFloat() * pixelSize, y.toFloat() * pixelSize
                            )
                        else -> { }
                    }
                }
            }
        }

    }

    private fun getRoundedBitmap(bitmap: Bitmap, value: Float): Bitmap {
        val frameBm = getRoundedRect(bitmap, value + 5f)
        val imageBm = bitmapToCircular(bitmap, value)

        val output = Bitmap.createBitmap(
            frameBm.width,
            frameBm.width, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val paint = Paint()
        val frameCentreX = (canvas.width - frameBm.width) / 2f
        val frameCentreY = (canvas.height - frameBm.height) / 2f

        val imageCentreX = (canvas.width - imageBm.width) / 2f
        val imageCentreY = (canvas.height - imageBm.height) / 2f

        canvas.drawBitmap(frameBm, frameCentreX, frameCentreY, paint)
        canvas.drawBitmap(imageBm, imageCentreX, imageCentreY, paint)
        return output
    }

    private fun getCircleBitmap(bitmap: Bitmap): Bitmap {
        val frameBm = getCircle(bitmap)
        val imageBm = bitmapToCircular(bitmap, 80f * 2)

        val output = Bitmap.createBitmap(
            frameBm.width,
            frameBm.width, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val paint = Paint()
        val frameCentreX = (canvas.width - frameBm.width) / 2f
        val frameCentreY = (canvas.height - frameBm.height) / 2f

        val imageCentreX = (canvas.width - imageBm.width) / 2f
        val imageCentreY = (canvas.height - imageBm.height) / 2f

        canvas.drawBitmap(frameBm, frameCentreX, frameCentreY, paint)
        canvas.drawBitmap(imageBm, imageCentreX, imageCentreY, paint)
        return output
    }

    private fun bitmapToCircular(bitmap: Bitmap, pixels: Float): Bitmap {
        val output = Bitmap.createBitmap(
            bitmap.width,
            bitmap.width, Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(output)
        val paint = Paint()
        val rect = Rect(
            0, 0, bitmap.width,
            bitmap.width
        )

        val rectF = RectF(rect)
        paint.isAntiAlias = true
        canvas.drawRoundRect(rectF, pixels, pixels, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)


        return output
    }

    private fun getRoundedRect(bitmap: Bitmap, pixels: Float): Bitmap {
        val output = Bitmap.createBitmap(
            bitmap.width + 20,
            bitmap.width + 20, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val rect = Rect(
            0, 0, canvas.width,
            canvas.width
        )

        val rectF = RectF(rect)

        val paintStroke = Paint()
        paintStroke.style = Paint.Style.FILL
        paintStroke.color = Color.WHITE
        paintStroke.isAntiAlias = true
        canvas.drawPath(
            roundedRect(
                rectF.left,
                rectF.top,
                rectF.right,
                rectF.bottom,
                pixels,
                pixels,
                false
            ), paintStroke
        )

        return output
    }

    private fun getCircle(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(
            bitmap.width + 55,
            bitmap.width + 55, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val paintStroke = Paint()
        paintStroke.style = Paint.Style.FILL
        paintStroke.color = Color.WHITE
        canvas.drawCircle(canvas.width / 2f, canvas.height / 2f, bitmap.width / 2f, paintStroke)
        return output
    }

    fun roundedRect(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        x: Float,
        y: Float,
        conformToOriginalPost: Boolean
    ): Path {
        var rx = x
        var ry = y
        val path = Path()
        if (rx < 0) rx = 0f
        if (ry < 0) ry = 0f
        val width = right - left
        val height = bottom - top
        if (rx > width / 2) rx = width / 2
        if (ry > height / 2) ry = height / 2
        val widthMinusCorners = width - 2 * rx
        val heightMinusCorners = height - 2 * ry
        path.moveTo(right, top + ry)
        path.rQuadTo(0f, -ry, -rx, -ry) //top-right corner
        path.rLineTo(-widthMinusCorners, 0f)
        path.rQuadTo(-rx, 0f, -rx, ry) //top-left corner
        path.rLineTo(0f, heightMinusCorners)
        if (conformToOriginalPost) {
            path.rLineTo(0f, ry)
            path.rLineTo(width, 0f)
            path.rLineTo(0f, -ry)
        } else {
            path.rQuadTo(0f, ry, rx, ry) //bottom-left corner
            path.rLineTo(widthMinusCorners, 0f)
            path.rQuadTo(rx, 0f, rx, -ry) //bottom-right corner
        }
        path.rLineTo(0f, -heightMinusCorners)
        path.close() //Given close, last lineto can be removed.
        return path
    }

}

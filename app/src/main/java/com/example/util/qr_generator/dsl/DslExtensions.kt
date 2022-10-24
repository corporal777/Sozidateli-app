package com.example.util.qr_generator.dsl

import android.graphics.Canvas
import android.graphics.Paint
import com.example.util.qr_generator.style.*
import kotlin.math.roundToInt
import kotlin.reflect.KClass
import androidx.annotation.IntRange




inline fun QrColorsBuilderScope.draw(
    crossinline action : Canvas.() -> Unit
) : QrColor = QrCanvasColor { canvas -> action(canvas) }
    .let {
        val (width, height)=when(this){
            is InternalColorsBuilderScope -> builder.width to builder.height
        }
        it.toQrColor(width, height)
    }


inline fun QrBackgroundBuilderScope.draw(
    crossinline action : Canvas.() -> Unit
) : QrColor = QrCanvasColor { canvas -> action(canvas) }
    .let {
        val (width, height)=when(this){
            is InternalQrBackgroundBuilderScope -> builder.width to builder.height
        }
        it.toQrColor(width, height)
    }


inline fun <reified T : QrShapeModifier> QrElementsShapesBuilderScope.drawShape(
    noinline draw : (canvas : Canvas, drawPaint : Paint, erasePaint : Paint) -> Unit
): T = drawShape(T::class, draw)


inline fun <reified T : QrShapeModifier> QrLogoBuilderScope.drawShape(
    noinline draw : (canvas : Canvas, drawPaint : Paint, erasePaint : Paint) -> Unit
): T = drawShape(T::class, draw)


@Suppress("unchecked_cast")
fun <T : QrShapeModifier> QrElementsShapesBuilderScope.drawShape(
    clazz: KClass<T>,
    draw : (canvas : Canvas, drawPaint : Paint, erasePaint : Paint) -> Unit
) : T = QrCanvasShape(draw)
    .let {
        val (size, padding) = when (this) {
            is InternalQrElementsShapesBuilderScope ->
                minOf(builder.width, builder.height) to builder.padding
        }
        it.toTypedShapeModifier(clazz, size, padding)
    }

@Suppress("unchecked_cast")
fun <T : QrShapeModifier> QrLogoBuilderScope.drawShape(
    clazz: KClass<T>,
    draw : (canvas : Canvas, drawPaint : Paint, erasePaint : Paint) -> Unit
) : T = QrCanvasShape(draw)
    .let {
        val (size, padding) = when (this) {
            is InternalQrLogoBuilderScope ->
                if (width >= 0 && height >= 0 && codePadding >= 0)
                    minOf(width, height) to codePadding
                else throw IllegalStateException(
                    "use overrideSize inside QrLogoBuilderScope to create custom QrLogoShape for vector QR code"
                )
        }
        it.toTypedShapeModifier(clazz, size, padding)
    }


fun QrLogoBuilderScope.overrideSize(
    @IntRange(from = 0) codeWidth : Int,
    @IntRange(from = 0) codeHeight : Int,
    block : QrLogoBuilderScope.() -> Unit
) {
    when (this) {
        is InternalQrLogoBuilderScope -> InternalQrLogoBuilderScope(
            builder, codeWidth, codeHeight, codePadding
        ).apply(block)
    }
}

@Suppress("unchecked_cast")
private fun <T : QrShapeModifier> QrCanvasShape.toTypedShapeModifier(
    clazz: KClass<T>,
    size: Int,
    padding : Float,
) : T = when (clazz) {
    QrPixelShape::class -> toShapeModifier((size * (1 - padding) / 21).roundToInt())
        .asPixelShape()
    QrBallShape::class -> toShapeModifier((size * (1 - padding) / 7).roundToInt())
        .asBallShape()
    QrFrameShape::class -> toShapeModifier((size * (1 - padding) / 3).roundToInt())
        .asFrameShape()
    QrLogoShape::class -> toShapeModifier((size * (1 - padding) / 3).roundToInt())
        .asLogoShape()
    QrHighlightingShape::class -> toShapeModifier((size * (1 - padding) / 3).roundToInt())
        .asHighlightingShape()
    else -> throw IllegalStateException(
        "Only QrElementsShapes properties and QrLogoShape can be created via drawShape function"
    )
} as T

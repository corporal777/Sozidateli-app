package com.generator.qrcodegenerator

import androidx.annotation.FloatRange
import com.generator.qrcodegenerator.style.QrVectorColor
import com.generator.qrcodegenerator.style.QrVectorShapeModifier


interface IAnchorsHighlighting {
    val cornerEyes : HighlightingType
    val versionEyes : HighlightingType
    val timingLines : HighlightingType
    val alpha : Float
}

data class QrHighlighting(
    override val cornerEyes : HighlightingType = HighlightingType.None,
    override val versionEyes : HighlightingType = HighlightingType.None,
    override val timingLines : HighlightingType = HighlightingType.None,
    @FloatRange(from = 0.0, to = 1.0) override val alpha: Float = .75f
) : IAnchorsHighlighting



sealed interface HighlightingType {

    object None : HighlightingType

    object Default : HighlightingType

    class Styled(
        val shape : QrVectorShapeModifier? = null,
        val color : QrVectorColor? = null
    ) : HighlightingType
}
package com.generator.qrcodegenerator.dsl

import com.generator.qrcodegenerator.HighlightingType
import com.generator.qrcodegenerator.IAnchorsHighlighting

sealed interface QrHighlightingBuilderScope : IAnchorsHighlighting {
    override var cornerEyes: HighlightingType
    override var versionEyes: HighlightingType
    override var timingLines: HighlightingType
    override val alpha: Float
}
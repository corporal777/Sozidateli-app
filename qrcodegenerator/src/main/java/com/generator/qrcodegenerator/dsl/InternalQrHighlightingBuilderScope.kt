package com.generator.qrcodegenerator.dsl

import com.generator.qrcodegenerator.HighlightingType
import com.generator.qrcodegenerator.QrVectorOptions
import com.generator.qrcodegenerator.dsl.QrHighlightingBuilderScope

internal class InternalQrHighlightingBuilderScope(
    private val builder : QrVectorOptions.Builder
) : QrHighlightingBuilderScope {
    override var cornerEyes: HighlightingType
        get() = builder.highlighting.cornerEyes
        set(value) {
            builder.setAnchorsHighlighting(
                builder.highlighting.copy(
                    cornerEyes = value
                )
            )
        }

    override var versionEyes: HighlightingType
        get() = builder.highlighting.versionEyes
        set(value) {
            builder.setAnchorsHighlighting(
                builder.highlighting.copy(
                    versionEyes = value
                )
            )
        }

    override var timingLines: HighlightingType
        get() = builder.highlighting.timingLines
        set(value) {
            builder.setAnchorsHighlighting(
                builder.highlighting.copy(
                    timingLines = value
                )
            )
        }

    override var alpha: Float
        get() = builder.highlighting.alpha
        set(value) {
            builder.setAnchorsHighlighting(
                builder.highlighting.copy(
                    alpha = value
                )
            )
        }
}
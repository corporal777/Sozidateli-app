package com.generator.qrcodegenerator.style

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.annotation.FloatRange
import com.generator.qrcodegenerator.dsl.QrVectorLogoBuilderScope
import com.generator.qrcodegenerator.style.QrVectorShapeModifier

interface QrVectorLogoShape : QrVectorShapeModifier {

    
    object Default : QrVectorLogoShape, QrVectorShapeModifier by DefaultVectorShape

    
    object Circle : QrVectorLogoShape, QrVectorShapeModifier by CircleVectorShape(1f)

    
    data class RoundCorners(
        @FloatRange(from = 0.0, to = .5) val radius: Float
    ) : QrVectorLogoShape, QrVectorShapeModifier by RoundCornersVectorShape(radius, false)

    object Internal {
        fun getLogoShape(radius: Float, logoScope: QrVectorLogoBuilderScope): QrVectorLogoShape {
            return if (logoScope.bitmap == null) Circle else RoundCorners(radius)
        }
    }

}
package com.example.util.qr_generator.style

import com.example.util.qr_generator.style.Neighbors
import com.example.util.qr_generator.style.QrShapeModifier


operator fun QrShapeModifier.not() : QrShapeModifier =
    QrShapeModifier { i, j, elementSize, neighbors ->
        invoke(i, j, elementSize, neighbors).not()
    }


fun QrShapeModifier.or(other: QrShapeModifier) : QrShapeModifier =
    QrShapeModifier { i, j, elementSize, neighbors ->
        invoke(i, j, elementSize, neighbors) || other(i, j, elementSize, neighbors)
    }


fun QrShapeModifier.and(other : QrShapeModifier): QrShapeModifier =
    QrShapeModifier { i, j, elementSize, neighbors ->
        invoke(i, j, elementSize, neighbors) && other(i, j, elementSize, neighbors)
    }


internal operator fun QrShapeModifier.rem(rem : Int) : QrShapeModifier =
    QrShapeModifier { i, j, _, neighbors ->
        invoke(i % rem, j % rem, rem, neighbors)
    }


internal operator fun QrShapeModifier.rem(
    remRuntime : (elemSize : Int, neighbors : Neighbors) -> Int
) : QrShapeModifier =
    QrShapeModifier { i, j, elementSize, neighbors ->
        val rem = remRuntime(elementSize, neighbors)
        invoke(i % rem, j % rem, rem, neighbors)
    }

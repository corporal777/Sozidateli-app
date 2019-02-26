package com.example.util

import androidx.transition.ChangeBounds
import androidx.transition.ChangeImageTransform
import androidx.transition.ChangeTransform
import androidx.transition.TransitionSet

public class ImageOpenTransition : TransitionSet {
    constructor() : super() {
        setOrdering(ORDERING_TOGETHER)
        addTransition(ChangeBounds())
                .addTransition(ChangeTransform())
                .addTransition(ChangeImageTransform())
    }

}
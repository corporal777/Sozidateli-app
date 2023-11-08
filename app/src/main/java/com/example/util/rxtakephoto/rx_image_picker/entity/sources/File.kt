package com.example.util.rxtakephoto.rx_image_picker.entity.sources

import androidx.annotation.IdRes
import com.example.util.rxtakephoto.rx_image_picker.ui.camera.BasicCameraFragment
import com.example.util.rxtakephoto.rx_image_picker.ui.file.BasicFileFragment
import kotlin.reflect.KClass

@Retention
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
annotation class File(

    val componentClazz: KClass<*> = BasicFileFragment::class,

    val openAsFragment: Boolean = true,

    @IdRes val containerViewId: Int = 0
)

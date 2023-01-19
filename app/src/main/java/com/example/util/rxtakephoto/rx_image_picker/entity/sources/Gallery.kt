package com.example.util.rxtakephoto.rx_image_picker.entity.sources
import androidx.annotation.IdRes
import com.example.util.rxtakephoto.rx_image_picker.ui.gallery.BasicGalleryFragment
import kotlin.reflect.KClass

@Retention
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
annotation class Gallery(

    val componentClazz: KClass<*> = BasicGalleryFragment::class,

    val openAsFragment: Boolean = true,

    @IdRes val containerViewId: Int = 0
)
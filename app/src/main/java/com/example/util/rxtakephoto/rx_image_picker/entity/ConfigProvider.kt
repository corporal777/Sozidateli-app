package com.example.util.rxtakephoto.rx_image_picker.entity
import androidx.annotation.IdRes
import com.example.util.rxtakephoto.rx_image_picker.entity.sources.SourcesFrom
import com.example.util.rxtakephoto.rx_image_picker.ui.ICustomPickerConfiguration
import com.example.util.rxtakephoto.rx_image_picker.ui.ICustomPickerView
import kotlin.reflect.KClass

data class ConfigProvider(val componentClazz: KClass<*>,
                          val asFragment: Boolean,
                          val sourcesFrom: SourcesFrom,
                          @param:IdRes val containerViewId: Int,
                          val fragmentActivity: androidx.fragment.app.FragmentActivity,
                          val pickerView: ICustomPickerView,
                          val config: ICustomPickerConfiguration?)
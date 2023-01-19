package com.example.util.rxtakephoto.rx_image_picker.core

import com.example.util.rxtakephoto.rx_image_picker.ui.BasicImagePicker
import java.lang.reflect.Proxy

object RxImagePicker {

    @JvmStatic
    fun create(): BasicImagePicker {
        return create(BasicImagePicker::class.java)
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> create(classProviders: Class<T>): T {
        val proxyProviders = ProxyProviders()

        return Proxy.newProxyInstance(
            classProviders.classLoader,
            arrayOf<Class<*>>(classProviders),
            proxyProviders) as T
    }
}
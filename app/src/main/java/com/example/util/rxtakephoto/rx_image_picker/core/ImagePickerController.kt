package com.example.util.rxtakephoto.rx_image_picker.core

import android.app.Activity
import com.example.util.rxtakephoto.rx_image_picker.entity.ConfigProvider
import com.example.util.rxtakephoto.rx_image_picker.ui.ActivityPickerViewController
import com.example.util.rxtakephoto.rx_image_picker.ui.ICustomPickerConfiguration

class ImagePickerController(private val configProvider: ConfigProvider) {

    fun display() {
        configProvider.config?.onDisplay()

        if (!configProvider.asFragment)
            displayPickerViewAsActivity(configProvider.config)
        else
            displayPickerViewAsFragment(configProvider.config)
    }

    private fun displayPickerViewAsActivity(configuration: ICustomPickerConfiguration?) {
        val activityHolder = ActivityPickerViewController.instance
        activityHolder.setActivityClass(configProvider.componentClazz.java as Class<out Activity>)
        activityHolder.display(
            configProvider.fragmentActivity, configProvider.containerViewId, configuration
        )
    }

    private fun displayPickerViewAsFragment(configuration: ICustomPickerConfiguration?) {
        configProvider.pickerView.display(
            configProvider.fragmentActivity, configProvider.containerViewId, configuration
        )
    }
}
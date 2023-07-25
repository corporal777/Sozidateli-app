package com.example

import android.app.*
import android.content.Context
import android.os.Build
import com.example.di.AppComponent
import com.example.di.DaggerAppComponent
import com.shakebugs.shake.Shake
import com.vk.sdk.VKSdk
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import timber.log.Timber
import javax.inject.Inject

class App : Application(), HasActivityInjector, HasServiceInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var serviceInjector: DispatchingAndroidInjector<Service>

    @Inject
    internal lateinit var calligraphyConfig: CalligraphyConfig

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()

        //Shake bug init
        if (!BuildConfig.DEBUG) {
            Shake.getReportConfiguration().isScreenshotIncluded = false
            Shake.getReportConfiguration().isInvokeShakeOnShakeDeviceEvent = false
        }
        Shake.setCrashReportingEnabled(true)
        Shake.start(
            this,
            getString(R.string.shake_client_id),
            getString(R.string.shake_client_secret)
        )


        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        createNotificationChannels()

        appComponent = DaggerAppComponent.builder()
            .application(this)
            .build()
            .apply { inject(this@App) }

        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    )
                )
                .build()
        )
        VKSdk.initialize(this)
    }


    override fun activityInjector() = activityInjector

    override fun serviceInjector() = serviceInjector

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "default_channel"
        val channelName = getString(R.string.app_default_notification_channel_name)

        NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT).apply {
            notificationManager.createNotificationChannel(this)
        }
    }
}
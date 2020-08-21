package com.example

import android.app.*
import android.content.Context
import android.os.Build
import com.example.di.AppComponent
import com.example.di.DaggerAppComponent
import com.vk.sdk.VKSdk
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import timber.log.Timber
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
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

//        if (LeakCanary.isInAnalyzerProcess(this)) {
        // This process is dedicated to LeakCanary for heap analysis.
        // You should not init your app in this process.
//            return
//        }
//        LeakCanary.install(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        createNotificationChannels()

        appComponent = DaggerAppComponent.builder()
                .application(this)
                .build()
                .apply { inject(this@App) }

        CalligraphyConfig.initDefault(calligraphyConfig)
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
package com.example

import android.app.*
import android.content.Context
import android.os.Build
import com.example.di.AppComponent
import com.example.di.DaggerAppComponent
import com.vk.sdk.VKSdk
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import ru.ok.android.sdk.BuildConfig
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

        val config: YandexMetricaConfig = YandexMetricaConfig.newConfigBuilder("faf7bdc3-762b-4c6a-8813-a6a8fb26f448").build()
        YandexMetrica.activate(applicationContext, config)
        YandexMetrica.enableActivityAutoTracking(this)

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

        //CalligraphyConfig.initDefault(calligraphyConfig)
        ViewPump.init(ViewPump.builder()
                .addInterceptor(CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build())
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
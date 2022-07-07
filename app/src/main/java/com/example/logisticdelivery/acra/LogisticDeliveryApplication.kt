package com.example.logisticdelivery.acra

import android.app.Application
import com.example.logisticdelivery.R
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import org.acra.ACRA
import org.acra.ReportingInteractionMode
import org.acra.annotation.ReportsCrashes
import timber.log.Timber

@ReportsCrashes(reportSenderFactoryClasses = [ToLogisticsApiSender::class])
class LogisticDeliveryApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RxJavaPlugins.setErrorHandler(Timber::e)
        instance=this
        ACRA.init(this);
    }
    companion object {
        var instance: LogisticDeliveryApplication? = null
    }
}
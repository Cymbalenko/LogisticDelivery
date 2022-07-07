package com.example.logisticdelivery.acra

import android.content.Context
import android.util.Log
import com.example.logisticdelivery.model.repository.Repository
import com.example.logisticdelivery.ui.properties.AppProperties
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import org.acra.collector.CrashReportData
import org.acra.sender.ReportSender

class ToLogisticsApi : ReportSender {
    override fun send(context: Context, errorContent: CrashReportData) {
        Repository.autorization(AppProperties.login_api,AppProperties.password_api)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ token ->
                    AppProperties.token = token;
                    Repository.setCrashReport(errorContent.toString(),"Bearer "+ AppProperties.token)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ token ->

                                Log.d("MainViewModeltoken", "sss\n" + token)
                            }, { throwable ->
                                Log.e("MainViewModeltoken", "Error\n" + throwable.message.toString())
                            })
                },{

                }
                );

    }

}
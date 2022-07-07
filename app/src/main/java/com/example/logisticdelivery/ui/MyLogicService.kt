package com.example.logisticdelivery.ui

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import com.example.logisticdelivery.R
import com.example.logisticdelivery.ui.properties.AppProperties
import java.util.*
import kotlin.concurrent.timerTask

class MyLogicService : Service() {
    private var myTimerIsEnable=false
    private var myTimerIsErrorEnable=false
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    private fun showPush(title:String,text:String){
        val builder: Notification.Builder = Notification.Builder(this)
            .setSmallIcon(R.drawable.ic_baseline_local_pizza_24 )
            .setContentTitle(title)
            .setContentText(text)

        val notification: Notification = builder.build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }
    private fun initTimer(){
        /*Timer("myTimer", false).schedule(timerTask {
            //showMessage()
        }, 1000, 10000)*/
        myTimerIsEnable=true
        myTimerIsErrorEnable=true
        Timer("myTimerIsA", false).schedule(timerTask {


            if(!myTimerIsEnable){
                this.cancel()
            }

        }, 1000, 50000)


    }
    override fun startService(service: Intent?): ComponentName? {
        initTimer()
        return super.startService(service)
    }
}
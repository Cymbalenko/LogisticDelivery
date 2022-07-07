package com.example.logisticdelivery.acra

import android.content.Context
import org.acra.config.ACRAConfiguration
import org.acra.sender.ReportSender
import org.acra.sender.ReportSenderFactory

class ToLogisticsApiSender: ReportSenderFactory {
    override fun create(context: Context, config: ACRAConfiguration): ReportSender {
        TODO("Not yet implemented")
    }
}
package com.example.logisticdelivery.ui.properties

import com.example.logisticdelivery.ui.main.MainActivity
import java.util.*

object AppProperties {

    var isUpdateAvailable=true
    var activityApp:MainActivity? = null
    var log_redirect = "VISAVIS_HO"
    var log_redirect_user = "visavis"
    var log_redirect_key = ""
    var log_url = ""
    var isSendedDriverState = false
    var isSendedAcraReport = false
    var currentAppVersion = ".."
    var availableAppVersion = ""

    var login_api="111"
    var password_api="222"
    var selected_latitude=150.0
    var selected_longitude=150.0
    var token=""
    var selectDriverLogin=""
    var selectDriverId="0"
    var selectDriverPassword=""
    var selectDriverPhone=""
    var selectDeliveryOrder=""
    var selectDriverName=""
    var selectDriverType=""
    var selectDriverStatus=-1

    //// =========================================================
    var isGpsEnable=false
    var isInternetEnable=false
    var isGpsServiceEnable=false
    var isInternetServiceEnable=false
    var isNetworkToServerEnable=false
    var isShiftEnable=false
    var countOrdersToday=0
    var isLateOrder=false
    var isLateOrderDate:Long=0
    var isNewOrder=false
    var newOrderMessage=""
    var isReloadService=true



    const val APP_PREFERENCES = "mysettings"

    //// =========================================================
    var linkToUpdateApkFile = ""

    var isControlServiceStarted = false
    var isGpsServiceStarted = false
    var isLocationEnabled = false // полностью местоположение
    var isStartCheckingUpdate: Boolean? = false   // признак запуска проверки обновления
    var isStartUpdateOrdersToDelivery =
        false // признак запуска обновления списка ордеров на доставку
    lateinit var  lastTime_CheckAppUpdate: Date; //время последней провнрки обновления

    var lastTime_UpdateOrdersToDelivery //время последнего обновления ордеров для доставки
            : Date? = null
    var state_OnShift = true // пока всегда истина - нужно при старте приложения
    var counterControlTimer = 0
    var state_Sended = false
}
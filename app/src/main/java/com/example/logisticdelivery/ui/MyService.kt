package com.example.logisticdelivery.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.VectorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.preference.PreferenceManager
import com.example.logisticdelivery.R
import com.example.logisticdelivery.acra.LogisticDeliveryApplication
import com.example.logisticdelivery.model.api.responses.DriverGeolocation
import com.example.logisticdelivery.model.api.responses.responsesEnum.DeliveryOrderStatus
import com.example.logisticdelivery.model.api.responses.responsesEnum.DriverStatus
import com.example.logisticdelivery.model.repository.Repository
import com.example.logisticdelivery.model.repository.RoomRepository
import com.example.logisticdelivery.ui.actualorders.ActualOrdersFragment
import com.example.logisticdelivery.ui.main.MainActivity
import com.example.logisticdelivery.ui.orderhistory.OrderHistoryFragment
import com.example.logisticdelivery.ui.properties.AppProperties
import com.google.android.gms.location.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timerTask


class MyService() : Service() {
    private var mLocationManager: LocationManager? = null

    var mLocationListeners = arrayOf(LocationListener(LocationManager.GPS_PROVIDER,this), LocationListener(LocationManager.NETWORK_PROVIDER,this))

    //private lateinit var activity:MainActivity
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private val preferences = PreferenceManager.getDefaultSharedPreferences(LogisticDeliveryApplication.instance)
    private var myTimerIsEnable=false
    private var myTimerIsErrorEnable=false
    private var  mainrep: RoomRepository = RoomRepository
    private lateinit var mMyTimerTask: TimerTask
    override fun onCreate() {
        /*Check location*/
         Log.e("locationSDK_INT", "onCreate")

        initializeLocationManager()
        try {
            mLocationManager?.let{
                it.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL.toLong(), LOCATION_DISTANCE,
                    mLocationListeners[1])
            }
        } catch (ex: java.lang.SecurityException) {
            Log.i(TAG, "fail to request location update, ignore", ex)
        }
        try {
            mLocationManager?.let {
                it.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL.toLong(), LOCATION_DISTANCE,
                    mLocationListeners[0])}
        } catch (ex: java.lang.SecurityException) {
            Log.i(TAG, "fail to request location update, ignore", ex)
        }
        //super.onCreate()
        startForeground()
    }

    class LocationListener(provider: String,service:MyService) : android.location.LocationListener {
        internal var mLastLocation: Location
        val serviceMy=service
        init {
            Log.e(TAG, "LocationListener $provider")
            mLastLocation = Location(provider)
        }
        fun getCurrentDateTime(): Date {
            return Calendar.getInstance().time
        }
        fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
            val formatter = SimpleDateFormat(format, locale)
            return formatter.format(this)
        }
        override fun onLocationChanged(location: Location) {

            Log.e(TAG, "onLocationChanged: $location")
            mLastLocation.set(location)
            if (AppProperties.selectDriverId!="0"&&mLastLocation.latitude!=0.0&&mLastLocation.longitude!=0.0){
                Log.e("location", "test")
                if(Build.VERSION.SDK_INT>=26){
                    Log.e("location", "SDK_INT")
                    try {
                        var driverId = AppProperties.selectDriverId.toInt()
                        val geolocation:DriverGeolocation= DriverGeolocation(
                            "0",
                            mLastLocation.latitude,
                            mLastLocation.longitude,
                            DateTimeFormatter.ISO_INSTANT.format(Instant.now()).toString(),
                            DateTimeFormatter.ISO_INSTANT.format(Instant.now()).toString(),
                            AppProperties.selectDriverId.toInt(),
                            "test",
                            "test"
                        )
                        serviceMy.SendGPSDriver(geolocation)
                    }catch (e: NumberFormatException){

                    }

                }else{

                    Log.e("location", "SDK_INT else")
                    Log.e("location", Date().toLocaleString().toString())
                    val myDatef = getCurrentDateTime()
                    val myDate = myDatef.toString("yyyy-MM-dd")+"T"+myDatef.toString("hh:mm:ss")+".000Z"

                    Log.e("location", myDate)
                    myDate.let {
                        try {
                            var driverId = AppProperties.selectDriverId.toInt()
                            val geolocation:DriverGeolocation= DriverGeolocation(
                                "0",
                                mLastLocation.latitude,
                                mLastLocation.longitude,
                                it,
                                it,
                                driverId,
                                "test",
                                "test"
                            )
                            serviceMy.SendGPSDriver(geolocation)
                        }catch (e: NumberFormatException){

                        }
                    }

                }
            }
            Log.v("LastLocation", mLastLocation.latitude.toString() +"  " + mLastLocation.longitude.toString())
        }

        override fun onProviderDisabled(provider: String) {
            Log.e(TAG, "onProviderDisabled: $provider")
        }

        override fun onProviderEnabled(provider: String) {
            Log.e(TAG, "onProviderEnabled: $provider")
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            Log.e(TAG, "onStatusChanged: $provider")
        }
    }


    override fun onBind(arg0: Intent): IBinder? {
        return null
    }
    companion object {
        private val TAG = "BOOMBOOMTESTGPS"
        private val LOCATION_INTERVAL = 3000
        private val LOCATION_DISTANCE = 0f
    }
    private fun initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager")
        if (mLocationManager == null) {
            mLocationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
    }
    private fun checkLocation():Boolean{
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)||manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                getLocationUpdates()
                startLocationUpdates()
            return true
        }
        return false
    }
    private fun showPush(title: String, text: String, id: Int,activity:Int){

        var resultIntent = Intent(this, MainActivity::class.java)
        when(activity){
            1-> resultIntent = Intent(this, MainActivity::class.java)
            2-> resultIntent = Intent(this, ActualOrdersFragment::class.java)
        }
        val resultPendingIntent = PendingIntent.getActivity(
                this, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        val options = BitmapFactory.Options()
        val myLogo = (ResourcesCompat.getDrawable(this.resources, R.drawable.ic_baseline_local_pizza_24, null) as VectorDrawable).toBitmap()

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.ic_baseline_local_pizza_24)
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .setLargeIcon(myLogo)
                .setPriority(2)
                .setShowWhen(true)
                .setWhen(getCurrentDateTime().time)
            .setVibrate(
                    longArrayOf(100, 250)
            )
            .setContentIntent(resultPendingIntent)

        val notification: Notification = builder.build()

        if(id==1){
            val soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.error_message)
            builder.setSound(soundUri)
        }
        if(id==2){
            val soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.new_message)
            builder.setSound(soundUri)
        }
       // val rnds = (0..100).random()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id, notification)
    }
    private fun deletePush(id: Int){
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(id)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    private fun showPushNew(title: String, text: String, channelId: String, channelName: String, id: Int,activity:Int){
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val importance = NotificationManager.IMPORTANCE_HIGH
        var resultIntent = Intent(this, MainActivity::class.java)
        when(activity){
                1-> resultIntent = Intent(this, MainActivity::class.java)
                2-> resultIntent = Intent(this, ActualOrdersFragment::class.java)
        }

        val resultPendingIntent = PendingIntent.getActivity(
                this, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                    channelId, channelName, importance
            )
            if(id==1){
                val soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.error_message)
                var audioAttributes =   AudioAttributes.Builder()
                mChannel.setSound(soundUri,audioAttributes.build())
                notificationManager.createNotificationChannel(mChannel)
            }
            if(id==2){
                val soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.new_message)
                var audioAttributes =   AudioAttributes.Builder()
                mChannel.setSound(soundUri,audioAttributes.build())
                notificationManager.createNotificationChannel(mChannel)
            }

        }
        val myLogo = (ResourcesCompat.getDrawable(this.resources, R.drawable.ic_baseline_local_pizza_24, null) as VectorDrawable).toBitmap()
        val builder: Notification.Builder = Notification.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_baseline_local_pizza_24)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setLargeIcon(myLogo)
                .setWhen(getCurrentDateTime().time)
                .setShowWhen(true)
                .setVibrate(
                    longArrayOf(100, 250)
                )
                .setColor(R.color.purple_200)
                .setContentIntent(resultPendingIntent)
        val notification: Notification = builder.build()

         notificationManager.notify(id, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("location", "onStartCommand  timerTask")
        Log.e("locationSDK_INT", "onStartCommand")
        //startForegroundService(intent)
        /*Check location*/
         initTimer()
        checkLocation()
        startForeground()
        /*initializeLocationManager()
        try {
            mLocationManager?.let{
                it.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL.toLong(), LOCATION_DISTANCE,
                    mLocationListeners[1])
            }
        } catch (ex: java.lang.SecurityException) {
            Log.i(TAG, "fail to request location update, ignore", ex)
        }
        try {
            mLocationManager?.let {
                it.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL.toLong(), LOCATION_DISTANCE,
                    mLocationListeners[0])}
        } catch (ex: java.lang.SecurityException) {
            Log.i(TAG, "fail to request location update, ignore", ex)
        }*/

        super.onStartCommand(intent, flags, startId)
        return Service.START_REDELIVER_INTENT
    }


    private fun finish() {
        TODO("Not yet implemented")
    }
    private fun showMessage(title: String, text: String, id: Int,activity:Int){
        if (Build.VERSION.SDK_INT >= 26)
        {
            showPushNew(title, text, "channel-123", "LogisticsApplication", id, activity)
        }
        else{
            showPush(title, text, id, activity)
        }
    }
    private fun startForeground() {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("my_service", "My Background Service")
            } else {
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                ""
            }

        val notificationBuilder = NotificationCompat.Builder(this, channelId )
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.ic_baseline_local_pizza_24)
            .setPriority(2)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(101, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }
    private fun AutoMessage(){
       // if(this::activity.isInitialized ){
        if(!AppProperties.isInternetEnable&&preferences.getBoolean("internet",true)){
            showMessage(this.getString(R.string.title_warning), this.getString(R.string.network_error), 1,1)
        }else{
            deletePush(1)
        }
        if(!AppProperties.isGpsEnable&&preferences.getBoolean("gps",true)){
            showMessage(this.getString(R.string.title_warning), this.getString(R.string.gps_error), 2,1)
        }else{
            deletePush(2)
        }
        if(AppProperties.isNewOrder&&preferences.getBoolean("new_order",true)){
            showMessage(this.getString(R.string.new_order), AppProperties.newOrderMessage, 5,2)
            AppProperties.isNewOrder=false
        }
        if(AppProperties.isLateOrder){
            showMessage(this.getString(R.string.late_order), AppProperties.newOrderMessage, 10,2)
            AppProperties.isLateOrder=false
        }
    }
    private fun initTimer(){
        Log.e("location", "myTimer  timerTask")
        Timer("myTimer", false).schedule(timerTask {
            AppProperties.activityApp?.let {
            it.runOnUiThread {

                    if (checkLocation())
                        Log.e("location", "myTimer  cancel")
                        this.cancel()

            }
        } }, 2000, 10000)
        Timer("myTimerDriverStatus", false).schedule(timerTask {
            AppProperties.activityApp?.let {
            it.runOnUiThread {

                Log.e("myTimerDriverStatus", AppProperties.selectDriverStatus.toString())
                if (AppProperties.selectDriverStatus==4){
                    newDriverStatus()
                }
                if (AppProperties.isLateOrderDate>=0){
                    val calendarCurrentTime = Calendar.getInstance()
                    val calendarLateTime = Calendar.getInstance()
                    calendarLateTime.timeInMillis=AppProperties.isLateOrderDate
                    calendarCurrentTime.timeZone = TimeZone.getDefault()
                    calendarLateTime.timeZone = TimeZone.getDefault()
                    val myDateInt = TimeUnit.MILLISECONDS.toMinutes(calendarLateTime.timeInMillis - calendarCurrentTime.timeInMillis)
                    if (myDateInt<=5){
                        AppProperties.isLateOrder=true
                        AppProperties.isLateOrderDate=-1
                    }

                }
            }
        } }, 2000, 10000)

        myTimerIsEnable=true
        myTimerIsErrorEnable=true
        Log.e("location", "myTimerIs  timerTask")
        Timer("myTimerIs", false).schedule(timerTask {
            isGpsEnable()
            isServerApiEnable()
            newOrder()
            if(!myTimerIsEnable){
                Log.e("location", "myTimerIsEnable  cancel")
                this.cancel()
            }

        }, 1000, 5000)
        Log.e("location", "myTimerIsEnable  timerTask")
        Timer("myTimerIsError", false).schedule(timerTask {
            AutoMessage()
            if(!myTimerIsErrorEnable){
                Log.e("location", "myTimerIsError  cancel")
                this.cancel()
            }
        }, 3000, 30000)

    }
    private fun getLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest()
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 5000
        locationRequest.smallestDisplacement = 100f //170 m = 0.1 mile
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY //according to your app
        locationCallback = object : LocationCallback() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onLocationResult(locationResult: LocationResult?) {

                Log.e("location v", "onLocationResult")
                locationResult ?: return

                Log.e("location v", "onLocationResult OK")
                try {
                    if (locationResult.locations.isNotEmpty()) {
                        Log.e("location v", "onLocationResult isNotEmpty")
                        /*val location = locationResult.lastLocation
                        Log.e("location", location.toString())*/
                        val addresses: List<Address>?
                        val geoCoder = Geocoder(applicationContext, Locale.getDefault())
                        addresses = geoCoder.getFromLocation(
                                locationResult.lastLocation.latitude,
                                locationResult.lastLocation.longitude,
                                1
                        )
                        if (addresses != null && addresses.isNotEmpty()) {
                            Log.e("location v", "onLocationResult addresses.isNotEmpty")
                            Log.e("location", "$addresses[0]")
                            val address: String = addresses[0].getAddressLine(0)
                            val city: String = addresses[0].locality
                            val country: String = addresses[0].countryName
                            Log.e("location", "$address $city   $country")
                        }

                        Log.e("location", "sadadasdasd")
                        if (AppProperties.selectDriverId!="0"&&locationResult.lastLocation.latitude!=0.0&&locationResult.lastLocation.longitude!=0.0){
                            Log.e("location", "test")
                            if(Build.VERSION.SDK_INT>=26){
                                Log.e("location", "SDK_INT")
                                try {
                                    var driverId = AppProperties.selectDriverId.toInt()
                                    val geolocation:DriverGeolocation= DriverGeolocation(
                                            "0",
                                            locationResult.lastLocation.latitude,
                                            locationResult.lastLocation.longitude,
                                            DateTimeFormatter.ISO_INSTANT.format(Instant.now()).toString(),
                                            DateTimeFormatter.ISO_INSTANT.format(Instant.now()).toString(),
                                            AppProperties.selectDriverId.toInt(),
                                            "test",
                                            "test"
                                    )
                                    SendGPSDriver(geolocation)
                                }catch (e: NumberFormatException){

                                }

                            }else{

                                Log.e("location", "SDK_INT else")
                                Log.e("location", Date().toLocaleString().toString())
                                val myDatef = getCurrentDateTime()
                                val myDate = myDatef.toString("yyyy-MM-dd")+"T"+myDatef.toString("hh:mm:ss")+".000Z"

                                Log.e("location", myDate)
                                myDate.let {
                                    try {
                                        var driverId = AppProperties.selectDriverId.toInt()
                                        val geolocation:DriverGeolocation= DriverGeolocation(
                                                "0",
                                                locationResult.lastLocation.latitude,
                                                locationResult.lastLocation.longitude,
                                                it,
                                                it,
                                                driverId,
                                                "test",
                                                "test"
                                        )
                                        SendGPSDriver(geolocation)
                                    }catch (e: NumberFormatException){

                                    }
                                }

                            }
                        }
                    }
                }catch (e: Exception){

                }

            }
        }
    }
    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }
    override fun startForegroundService(service: Intent?): ComponentName? {
        Log.e("locationSDK_INT", "startForegroundService")
        Log.e("location", "startForegroundService  timerTask")
        initTimer()

        checkLocation()
        val NOTIFICATION_ID = 123123
        startForeground()
        initializeLocationManager()

        return super.startForegroundService(service)
    }
    // Stop location updates
    private fun stopLocationUpdates() {

        Log.e("location", "stopLocationUpdates  timerTask")
        if(this::fusedLocationClient.isInitialized ) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    override fun onDestroy() {

        Log.e("locationSDK_INT", "onDestroy")
        Log.e("location", "onDestroy")
        super.onDestroy()
        myTimerIsEnable=false
        myTimerIsErrorEnable=false
        stopLocationUpdates()
        stopForeground(true)
        if(AppProperties.isReloadService){
            AppProperties.activityApp?.let{
                it.startGpsService()
            }
        }else{
            AppProperties.isReloadService=true
        }
        if (mLocationManager != null) {
            for (i in mLocationListeners.indices) {
                try {
                    mLocationManager?.let{
                        it.removeUpdates(mLocationListeners[i])
                    }
                } catch (ex: Exception) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex)
                }

            }
        }
    }
    // Start location updates
    private fun startLocationUpdates() {
        Log.e("location", "startLocationUpdates  timerTask")
        if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            var REQUEST_CODE_PERMISSION_FINE_LOCATION = 0
            AppProperties.activityApp?.let{
                ActivityCompat.requestPermissions(
                        it,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_CODE_PERMISSION_FINE_LOCATION
                )
            }

            Log.e("location startLocatio", "$REQUEST_CODE_PERMISSION_FINE_LOCATION")
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null /* Looper */
        )
    }
    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }


    fun SendGPSDriver(geolocation: DriverGeolocation){
        if(AppProperties.selectDriverStatus!=0){
            return
        }
        try {
            val lat = (AppProperties.selected_latitude*1000).toInt()-(geolocation.latitude*1000).toInt()
            val lon = (AppProperties.selected_longitude*1000).toInt()-(geolocation.longitude*1000).toInt()
            Log.e("locationssssss-lat", lat.toString())
            Log.e("locationssssss-lon", lon.toString())
            if (abs(lat)<3&&abs(lon)<3){
                return
            }
            Log.e("locationssssss-AS", (AppProperties.selected_latitude*1000).toInt().toString())
            Log.e("locationssssss-GL", (geolocation.latitude*1000).toInt().toString())
            Log.e("locationssssss-AS2", (AppProperties.selected_longitude*1000).toInt().toString())
            Log.e("locationssssss-GL2", (geolocation.longitude*1000).toInt().toString())
            AppProperties.selected_latitude=geolocation.latitude
            AppProperties.selected_longitude=geolocation.longitude
        if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_NETWORK_STATE
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            var REQUEST_CODE_ACCESS_NETWORK_STATE = 0
            AppProperties.activityApp?.let {
                ActivityCompat.requestPermissions(
                        it,
                        arrayOf(android.Manifest.permission.ACCESS_NETWORK_STATE),
                        REQUEST_CODE_ACCESS_NETWORK_STATE
                )
            }

            Log.e("location", "$REQUEST_CODE_ACCESS_NETWORK_STATE")
        }
        Log.e("location", "$geolocation")
        Repository.autorization(AppProperties.login_api, AppProperties.password_api)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ token ->
                AppProperties.token = token
                var tokenApi = "Bearer " + token
                Log.d("location", "tokenApi\n" + tokenApi)
                Log.d("sending_gps", "tokenApi\n" + tokenApi)
                if(preferences.getBoolean("sending_gps",true)){
                    showMessage(this.getString(R.string.title_info), this.getString(R.string.sending_gps), 3,1)
                }
                Repository.sendDriverGeolocation(geolocation, tokenApi)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ result ->
                            Log.d("location", "sss\n" + result)
                        }, { throwable ->
                            Log.e("location", "Ok \n" + throwable.message.toString())
                        })
                Log.d("location", "Success\n" + token)
            }, { throwable ->
                Log.e("location", "Error\n" + throwable.message.toString())
            })


        }catch (e: Exception){

        }
    }
    private fun isGpsEnable(){
        /*val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            Log.e("location", "showAlertLocation")
            showAlertLocation()
        }*/
        try {
            val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            AppProperties.isGpsEnable = manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }catch (e: java.lang.Exception){

        }
    }
    private fun isServerApiEnable(){
        try {
            Repository.isEnabledApi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ token ->
                    AppProperties.isNetworkToServerEnable = true
                }, { throwable ->
                    AppProperties.isNetworkToServerEnable = false
                })
        }catch (e: java.lang.Exception){
            AppProperties.isNetworkToServerEnable=false
        }

    }
    fun newOrder(){
        Repository.autorization(AppProperties.login_api,AppProperties.password_api)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ token ->
                AppProperties.token=token
                AppProperties.selectDriverId.let{
                    Repository.getDeliveryOrderDriver(AppProperties.selectDriverId,"Bearer "+AppProperties.token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ token ->
                            token.deliveryOrderStatus= DeliveryOrderStatus.Ready
                            AppProperties.selectDriverId.let {
                                token.driverId=it.toInt()
                            }
                            mainrep.getDeliveryOrderByOrderId(token.receptNo)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ id ->
                                    if(id.deliveryOrderId==token.deliveryOrderId &&(
                                                id.deliveryOrderStatus==DeliveryOrderStatus.Ready
                                            )) {
                                        token.id = id.id
                                        mainrep.updateDeliveryOrders(token)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe({

                                            }, { exception ->
                                                println("error: ${exception.message}")
                                            })
                                        println("success. id = $id")
                                    }

                                }, { exception ->
                                    mainrep.insertDeliveryOrders(token)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({ id ->
                                            AppProperties.isNewOrder=true

                                            token.restaurantName?.let {

                                                showMessage(this.getString(R.string.new_order), token.restaurantName, 5,2)
                                                AppProperties.isNewOrder=false
                                            }

                                            println("success. id = $id")
                                        }, { exception ->
                                            println("error: ${exception.message}")
                                        })
                                    println("error: ${exception.message}")
                                })




                            Log.d("MainViewModeltoken", "sss\n" + token)
                        }, { throwable ->
                            Log.e("MainViewModeltoken", "Error\n" + throwable.message.toString())
                        })
                    Log.e("success. id = ",mainrep.getAllDrivers().toString())
                }
                Log.d("MainViewModeltoken", "Success\n" + token)
            }, { throwable ->
                Log.e("MainViewModeltoken", "Error\n" + throwable.message.toString())
            })



    }
    fun newDriverStatus():Int {
        var resStatus = -1
        Repository.autorization(AppProperties.login_api, AppProperties.password_api)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ token ->
                AppProperties.token = token
                AppProperties.selectDriverId.let {
                    Repository.getStatusDriver(
                        AppProperties.selectDriverId,
                        "Bearer " + AppProperties.token
                    )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ status ->

                            Log.e("myTimerDriverStatus", status.toString())
                            resStatus = status
                            if (resStatus == 0) {

                                AppProperties.isShiftEnable=true
                                showMessage(
                                    this.getString(R.string.substitution), this.getString(R.string.on_shift_yes),
                                    7,
                                    2
                                )
                            }else if(resStatus == 3){
                                AppProperties.isShiftEnable=false
                                showMessage(
                                    this.getString(R.string.substitution), this.getString(R.string.on_shift_no),
                                    7,
                                    2
                                )
                            }
                            if(resStatus==0||resStatus==3){
                                AppProperties.selectDriverStatus=resStatus
                                if(AppProperties.selectDriverId!="0"){
                                    var statusNewStr=""
                                    if (resStatus == 0) {
                                        statusNewStr="InWork"
                                    }else if(resStatus == 3){
                                        statusNewStr="NotActive"
                                    }
                                    mainrep.updateByDriverIdStatus(statusNewStr,AppProperties.selectDriverId)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({
                                        }, { exception ->
                                            println("error: ${exception.message}")
                                        })
                                }

                            }
                        })
                    Log.e("success. id = ", mainrep.getAllDrivers().toString())
                }
                Log.d("MainViewModeltoken", "Success\n" + token)
            }, { throwable ->
                Log.e("MainViewModeltoken", "Error\n" + throwable.message.toString())
            })

        return resStatus
    }


}
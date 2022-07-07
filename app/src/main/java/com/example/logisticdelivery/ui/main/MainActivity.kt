package com.example.logisticdelivery.ui.main

import android.Manifest
import android.app.ActivityManager
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings.Global.putString
import android.telephony.PhoneNumberFormattingTextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.example.logisticdelivery.R
import com.example.logisticdelivery.acra.LogisticDeliveryApplication
import com.example.logisticdelivery.ui.MyLogicService
import com.example.logisticdelivery.ui.MyService
import com.example.logisticdelivery.ui.home.common.HomeUiEvent
import com.example.logisticdelivery.ui.login.LoginActivity
import com.example.logisticdelivery.ui.main.common.MainUiEvent
import com.example.logisticdelivery.ui.properties.AppProperties
import com.example.logisticdelivery.util.Event
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.annotations.SerializedName
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val viewModel: SharedViewModel by viewModels()
    private val preferences = PreferenceManager.getDefaultSharedPreferences(
        LogisticDeliveryApplication.instance)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppProperties.activityApp=this
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_home, R.id.nav_actual_order, R.id.nav_history_order, R.id.nav_settings
        ), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        /*Check location*/
       // checkLocation()
        startGpsService()
        startCalcService()
        Handler().postDelayed({
            initHeader()
        }, 1000)
        //checkPermision()
        //initTimer()
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    fun checkPermision(){
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
           // REQUEST_CODE_PERMISSION_FINE_LOCATION = ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
            //shouldShowRequestPermissionRationale(this,android.Manifest.permission.ACCESS_FINE_LOCATION)

            ActivityCompat.requestPermissions(
                     this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_CODE_PERMISSION_FINE_LOCATION
            )
            Log.e("location s", "$REQUEST_CODE_PERMISSION_FINE_LOCATION")
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            val requestPermissionLauncher =
                    registerForActivityResult(ActivityResultContracts.RequestPermission()
                    ) { isGranted: Boolean ->
                        if (isGranted) {Log.e("location s isGranted", "$isGranted")
                            // Permission is granted. Continue the action or workflow in your
                            // app.
                        } else {Log.e("location s isGranted", "$REQUEST_CODE_PERMISSION_FINE_LOCATION")
                            // Explain to the user that the feature is unavailable because the
                            // features requires a permission that the user has denied. At the
                            // same time, respect the user's decision. Don't link to system
                            // settings in an effort to convince the user to change their
                            // decision.
                        }
                    }

        }
    }

    fun withEditText(view: View) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle(getString(R.string.change_phone))
        val dialogLayout = inflater.inflate(R.layout.prompt, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.input_text)
        editText.setText(AppProperties.selectDriverPhone)
        builder.setView(dialogLayout)
        builder.setPositiveButton(getString(R.string.Ok)) { dialogInterface, i ->
            val phone = editText.text.toString()
            if (phone.length!=10){
                Toast.makeText(applicationContext, getString(R.string.phone_error), Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(applicationContext, getString(R.string.succes), Toast.LENGTH_SHORT).show()
                AppProperties.selectDriverPhone=phone
                viewModel.changePhoneNumber(phone)
                initHeader()
            }
        }
        builder.show()
    }

    private fun initHeader( ){
        try {  Log.d("MainViewModeltoken", "Header")

            val navigation =  findViewById<NavigationView>(R.id.nav_view)
            val header: View = navigation.getHeaderView(0)
            Log.d("MainViewModeltoken", "Header")
            Log.d("MainViewModeltoken", "$navigation"  )
            Log.d("MainViewModeltoken", "$header"  )

            header.let {
                var buttonPhone:Button = it.findViewById(R.id.headerChangePhoneButton)
                buttonPhone.setOnClickListener {
                    withEditText(it)
                }
                Log.d("MainViewModeltoken", "Header")
                Log.d("MainViewModeltoken", "${AppProperties.selectDriverPhone}")
                Log.d("MainViewModeltoken", "${AppProperties.selectDriverType}")
                Log.d("MainViewModeltoken", "${AppProperties.selectDriverName}")
                if (AppProperties.selectDriverName != "") {
                    val driverName: TextView = it.findViewById(R.id.headerDriverNameTextView)
                    driverName.text = AppProperties.selectDriverName
                }
                if (AppProperties.selectDriverLogin != "") {
                    val driverLogin: TextView = it.findViewById(R.id.headerDriverStaffIdTextView)
                    driverLogin.text = AppProperties.selectDriverLogin
                }
                if (AppProperties.selectDriverPhone != "") {
                    val driverPhone: TextView = it.findViewById(R.id.headerDriverPhoneTextView)
                    val phone = AppProperties.selectDriverPhone
                    if(phone.length>7){
                        driverPhone.text = "(" + phone.substring(0,3)+") "+ phone.substring(3,5)+"-"+phone.substring(5,7)+"-"+ phone.substring(7)
                    } else{
                        driverPhone.text = phone
                    }
                }
                if (AppProperties.selectDriverType != "") {
                    val driverType: TextView = it.findViewById(R.id.headerDriverTypeTextView)
                    when(AppProperties.selectDriverType){
                        "Walking" -> {
                            driverType.text=getString(R.string.walking_driver)
                        }
                        "Moto" ->{
                            driverType.text=getString(R.string.moto_driver)
                        }
                        "Auto" ->{
                            driverType.text=getString(R.string.auto_driver)
                        }
                    }
                }

            }


        }catch (e: Exception){

        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.title){
            getString(R.string.change_courier) ->
                navigateToLoginActivity()
            getString(R.string.exit) -> {
                AppProperties.isReloadService=false
                if (isMyServiceRunning(MyService::class.java)) {
                    stopService(Intent(this, MyService::class.java))
                }
                if (isMyServiceRunning(MyLogicService::class.java)) {
                    stopService(Intent(this, MyLogicService::class.java))
                }
                this.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigateToLoginActivity() {

        val intent = Intent(LogisticDeliveryApplication.instance, LoginActivity::class.java)
        if (isMyServiceRunning(MyService::class.java)) {
            stopService(Intent(this, MyService::class.java))
        }
        if (isMyServiceRunning(MyLogicService::class.java)) {
            stopService(Intent(this, MyLogicService::class.java))
        }

        preferences.edit{putString("pref_driver_staffId","")}
        preferences.edit{putString("pref_driver_password","")}
        startActivity(intent)
        this.finish()
    }

     fun startGpsService() {
        if (!isMyServiceRunning(MyService::class.java)) {
            AppProperties.isGpsServiceEnable=true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                Log.e("locationSDK_INT", ">Build.VERSION_CODES.O")
                this.startForegroundService(Intent(this, MyService::class.java))
            }else{
                Log.e("locationSDK_INT", "<Build.VERSION_CODES.O>")
                startService(Intent(this, MyService::class.java))
            }
        }
    }

     fun startCalcService() {
        if (!isMyServiceRunning(MyLogicService::class.java)) {
            AppProperties.isInternetServiceEnable=true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                startService(Intent(this, MyLogicService::class.java))
            }else{
                startService(Intent(this, MyLogicService::class.java))
            }
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    private fun initSwift(){
        var gpsSwitch:SwitchCompat = findViewById(R.id.gps_switch)
        var internetSwitch:SwitchCompat = findViewById(R.id.internet_switch)
        var connectToServerSwitch:SwitchCompat = findViewById(R.id.connect_to_server_switch)
        var serviceOrdersSwitch:SwitchCompat = findViewById(R.id.service_orders_switch)
        var sendingGpsSwitch:SwitchCompat = findViewById(R.id.sending_gps_switch)
        var onShiftSwitch:SwitchCompat = findViewById(R.id.on_shift_switch)


        var gpsErrorTextView:TextView = findViewById(R.id.gps_error_text_view)
        var internetErrorTextView:TextView = findViewById(R.id.internet_error_text_view)
        var connectToServerErrorTextView:TextView = findViewById(R.id.connect_to_server_error_text_view)
        var serviceOrdersErrorTextView:TextView = findViewById(R.id.service_orders_error_text_view)
        var serviceGpsErrorTextView:TextView = findViewById(R.id.service_gps_error_text_view)
        var ordersTodayValueTextView:TextView = findViewById(R.id.orders_today_value_text_view)


        var buttonStartShift:Button = findViewById(R.id.button_start_shift)
        var buttonEndShift:Button = findViewById(R.id.button_start_shift)


        if(AppProperties.isGpsEnable){
            gpsSwitch.isChecked=true
            gpsErrorTextView.isVisible=false
        }else{
            gpsSwitch.isChecked=false
            gpsErrorTextView.isVisible=true
        }

        if(AppProperties.isInternetEnable){
            internetSwitch.isChecked=true
            internetErrorTextView.isVisible=false
        }else{
            internetSwitch.isChecked=false
            internetErrorTextView.isVisible=true
        }

        if(AppProperties.isNetworkToServerEnable){
            connectToServerSwitch.isChecked=true
            connectToServerErrorTextView.isVisible=false
        }else{
            connectToServerSwitch.isChecked=false
            connectToServerErrorTextView.isVisible=true
        }


        if(AppProperties.isInternetServiceEnable){
            serviceOrdersSwitch.isChecked=true
            serviceOrdersErrorTextView.isVisible=false
        }else{
            serviceOrdersSwitch.isChecked=false
            serviceOrdersErrorTextView.isVisible=true
        }


        if(AppProperties.isGpsServiceEnable){
            sendingGpsSwitch.isChecked=true
            serviceGpsErrorTextView.isVisible=false
        }else{
            sendingGpsSwitch.isChecked=false
            serviceGpsErrorTextView.isVisible=true
        }


        if(AppProperties.isShiftEnable){
            onShiftSwitch.isChecked=true
            buttonStartShift.visibility= View.INVISIBLE
            buttonEndShift.visibility= View.VISIBLE
        }else{
            onShiftSwitch.isChecked=false
            buttonStartShift.visibility= View.VISIBLE
            buttonEndShift.visibility= View.INVISIBLE
        }

        ordersTodayValueTextView.text=AppProperties.countOrdersToday.toString()
    }
}
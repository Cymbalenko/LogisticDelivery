package com.example.logisticdelivery.ui.home

import android.app.FragmentManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.logisticdelivery.R
import com.example.logisticdelivery.databinding.FragmentHomeBinding
import com.example.logisticdelivery.ui.actualorders.ActualOrdersFragment
import com.example.logisticdelivery.ui.base.BaseViewBindingFragment
import com.example.logisticdelivery.ui.home.common.HomeUiEvent
import com.example.logisticdelivery.ui.properties.AppProperties
import com.example.logisticdelivery.ui.settings.Setting
import com.example.logisticdelivery.util.Event
import com.google.android.material.navigation.NavigationView
import java.util.*


class HomeFragment : BaseViewBindingFragment<FragmentHomeBinding>() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            //Setting.btnUpdate = view?.findViewById(R.id.btn_refr) as Button
            Setting.btnUpdate?.setOnClickListener { v -> getDeliveryList(v) }
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonLoadOrder.setOnClickListener { v ->
            loadRestaurantList(v)
        }

        binding.onShiftSwitch.setOnClickListener{
            binding.onShiftSwitch.isChecked=AppProperties.isShiftEnable
        }




        homeViewModel.uiEvent.observe(viewLifecycleOwner) { event ->
            handleEvent(event)
        }
        homeViewModel.deliveryOrdersCount.observe(viewLifecycleOwner) { event ->
            binding.ordersTodayValueTextView.text=homeViewModel.deliveryOrdersCount.value.toString()
        }

        binding.buttonLoadOrder.setOnClickListener {
            //viewModel.loadDeliveryList()
            OpenMaps()
        }

        binding.buttonStartShift.setOnClickListener {
            homeViewModel.startSwift()
        }

        binding.buttonEndShift.setOnClickListener {
            withArrYourSure(it)
        }

        Log.d("AppProperties.isN", AppProperties.isNewOrder.toString())
        if( AppProperties.isNewOrder){
            Log.d("AppProperties.isN", "yes")
            val anim: Animation = AlphaAnimation(0.0f, 1.0f)
            anim.duration = 300 //You can manage the time of the blink with this parameter

            anim.startOffset = 20
            anim.repeatMode = Animation.REVERSE
            anim.repeatCount = Animation.INFINITE
            binding.newOrderInfoTextView .startAnimation(anim)
            binding.newOrderInfoTextView.visibility= VISIBLE
            binding.newOrderInfoTextView.setOnClickListener{
                navigateToActiveOrderFragment()
            }
        }else{
            Log.d("AppProperties.isN", "no")
            binding.newOrderInfoTextView.visibility= INVISIBLE
        }


        homeViewModel.InitTimer()
        initSwift()
        homeViewModel.LoadRestaurant()


    }
    private fun initHeader(viewMy: View){
        try {  Log.d("MainViewModeltoken", "Header")

                val navigation = viewMy.findViewById<NavigationView>(R.id.nav_view)
                val header: View = navigation.getHeaderView(0)
                Log.d("MainViewModeltoken", "Header")
                Log.d("MainViewModeltoken", "$navigation")
                Log.d("MainViewModeltoken", "$header")

                header.let {
                    Log.d("MainViewModeltoken", "Header")
                    Log.d("MainViewModeltoken", "${AppProperties.selectDriverPhone}")
                    Log.d("MainViewModeltoken", "${AppProperties.selectDriverType}")
                    Log.d("MainViewModeltoken", "${AppProperties.selectDriverName}")
                    if (AppProperties.selectDriverName != "") {
                        val driverName: TextView = it.findViewById(R.id.headerDriverNameTextView)
                        driverName.text = AppProperties.selectDriverName
                    }
                    if (AppProperties.selectDriverPhone != "") {
                        val driverPhone: TextView = it.findViewById(R.id.headerDriverPhoneTextView)
                        driverPhone.text = AppProperties.selectDriverPhone
                    }
                    if (AppProperties.selectDriverType != "") {
                        val driverType: TextView = it.findViewById(R.id.headerDriverTypeTextView)
                        driverType.text = AppProperties.selectDriverType
                    }

            }


        }catch (e: Exception){

        }
    }

    private fun handleEvent(event: Event<HomeUiEvent>) {
        event.getIfNotHandled()?.let { homeEvent ->
            when (homeEvent) {
                is HomeUiEvent.Loading -> {
                    binding.loadingShiftProgressBar.isVisible = homeEvent.isLoading
                    if (homeEvent.isLoading) {
                        binding.buttonStartShift.visibility = INVISIBLE
                        binding.buttonEndShift.visibility = INVISIBLE
                    } else {
                        binding.buttonStartShift.visibility = VISIBLE
                        binding.buttonEndShift.visibility = VISIBLE
                    }


                }
                is HomeUiEvent.LoginError -> {
                    Toast.makeText(requireContext(), homeEvent.errorStringId, Toast.LENGTH_LONG)
                            .show()
                }
                is HomeUiEvent.Shift -> {
                    if (homeEvent.isShift) {
                        binding.buttonStartShift.visibility = INVISIBLE
                        binding.buttonEndShift.visibility = VISIBLE
                    } else {
                        binding.buttonStartShift.visibility = VISIBLE
                        binding.buttonEndShift.visibility = INVISIBLE
                    }
                }
                is HomeUiEvent.Success -> {
                    Toast.makeText(requireContext(), "Succes", Toast.LENGTH_LONG)
                            .show()

                }
                is HomeUiEvent.Reload -> {
                    isOnline(this.requireContext())
                    initSwift()
                }
                is HomeUiEvent.InitHeader -> {

                }
            }
        }
    }
    fun getDeliveryList(view: View?) {

      //  val connectToServer = ConnectToServer()
      //  connectToServer.deliveryOrderList()
    }
    private fun withArrYourSure(view: View) {
         AppProperties.activityApp?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = layoutInflater
            builder.setTitle(getString(R.string.are_you_sure))
            val dialogLayout = inflater.inflate(R.layout.prompt, null)
            val editText  = dialogLayout.findViewById<EditText>(R.id.input_text)
            editText.visibility=INVISIBLE
            builder.setView(dialogLayout)
            builder.setPositiveButton(R.string.Yes) { dialogInterface, i ->
                homeViewModel.finishSwift()
            }
            builder.setNegativeButton(R.string.No) { dialogInterface, i ->
            }
            builder.show()
        }
    }

    private fun initSwift(){

        if(AppProperties.isGpsServiceEnable){
            binding.sendingGpsOkImageView.visibility= VISIBLE
            binding.sendingGpsErrorImageView.visibility= INVISIBLE
            binding.serviceGpsErrorTextView.visibility= INVISIBLE
        }else{
            binding.sendingGpsOkImageView.visibility= INVISIBLE
            binding.sendingGpsErrorImageView.visibility= VISIBLE
            binding.serviceGpsErrorTextView.visibility= VISIBLE
        }

        if(AppProperties.isGpsEnable){
            binding.gpsOkImageView.visibility= VISIBLE
            binding.gpsErrorImageView.visibility= INVISIBLE
            binding.gpsErrorTextView.visibility= INVISIBLE
        }else{
            binding.gpsOkImageView.visibility= INVISIBLE
            binding.gpsErrorImageView.visibility= VISIBLE
            binding.gpsErrorTextView.visibility= VISIBLE
        }

        if(AppProperties.isInternetEnable){
            binding.internetOkImageView.visibility= VISIBLE
            binding.internetErrorImageView.visibility= INVISIBLE
            binding.internetErrorTextView.visibility= INVISIBLE
        }else{
            binding.internetOkImageView.visibility= INVISIBLE
            binding.internetErrorImageView.visibility= VISIBLE
            binding.internetErrorTextView.visibility= VISIBLE
        }

        if(AppProperties.isNetworkToServerEnable){
            binding.connectToServerOkImageView.visibility= VISIBLE
            binding.connectToServerErrorImageView.visibility= INVISIBLE
            binding.connectToServerErrorTextView.visibility= INVISIBLE
        }else{
            binding.connectToServerOkImageView.visibility= INVISIBLE
            binding.connectToServerErrorImageView.visibility= VISIBLE
            binding.connectToServerErrorTextView.visibility= VISIBLE
        }

        if(AppProperties.isInternetServiceEnable){
            binding.serviceOrdersOkImageView.visibility= VISIBLE
            binding.serviceOrdersErrorImageView.visibility= INVISIBLE
            binding.serviceOrdersErrorTextView.visibility= INVISIBLE
        }else{
            binding.serviceOrdersOkImageView.visibility= INVISIBLE
            binding.serviceOrdersErrorImageView.visibility= VISIBLE
            binding.serviceOrdersErrorTextView.visibility= VISIBLE
        }
        if(AppProperties.selectDriverStatus==0){
            AppProperties.isShiftEnable=true
        }

        Log.e("myTimerDriverStatus", AppProperties.isShiftEnable.toString())
        Log.e("myTimerDriverStatus", AppProperties.selectDriverStatus.toString())
        if(AppProperties.isShiftEnable){
            binding.onShiftSwitch.isChecked=true
             binding.buttonStartShift.visibility= INVISIBLE
             binding.buttonEndShift.visibility=VISIBLE
            binding.buttonOnShift.visibility=INVISIBLE
        }else{
            binding.onShiftSwitch.isChecked=false
            if(AppProperties.selectDriverStatus==4){
                //binding.buttonEndShift.text= getString(R.string.On_confirmation)
                binding.buttonStartShift.visibility= INVISIBLE
                binding.buttonEndShift.visibility=INVISIBLE
                binding.buttonOnShift.visibility=VISIBLE
            }else{
                //binding.buttonEndShift.text= getString(R.string.end_shift)

                binding.buttonStartShift.visibility=VISIBLE
                binding.buttonOnShift.visibility=INVISIBLE
                binding.buttonEndShift.visibility=INVISIBLE
            }
        }

        binding.ordersTodayValueTextView.text=AppProperties.countOrdersToday.toString()
    }
    override fun initViewBinding(view: View): FragmentHomeBinding {
        return FragmentHomeBinding.bind(view)
    }
    fun loadRestaurantList(view: View?) {
        try {
            //homeViewModel.LoadRestaurant()
            homeViewModel.LoadRestaurantGps()
        }catch (e: Exception){
            Toast.makeText(this.context, e.message, Toast.LENGTH_SHORT).show()
        }
    }
    fun OpenMaps(){
        val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"))
        startActivity(intent)
    }
    fun isOnline(context: Context){
        var check=false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.let { manager->
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                val capabilities =
                    manager.getNetworkCapabilities(connectivityManager.activeNetwork)
                capabilities?.let {
                    if (it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                        check=true
                        AppProperties.isInternetEnable =  true
                    } else if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                        check=true
                        AppProperties.isInternetEnable = true
                    } else if (it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                        check=true
                        AppProperties.isInternetEnable = true
                    }else{
                        check=true
                        AppProperties.isInternetEnable = false
                    }
                }
            }else{
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                activeNetworkInfo?.let {
                    if (it.isConnected){
                        check=true
                        AppProperties.isInternetEnable = true
                    }else{
                        check=true
                        AppProperties.isInternetEnable = false
                    }
                }
            }

        }
        if (!check){
            AppProperties.isInternetEnable = false
        }
    }

    private fun navigateToActiveOrderFragment() {
        findNavController().navigate(
                R.id.action_nav_home_to_nav_actual_order
        )
    }
}
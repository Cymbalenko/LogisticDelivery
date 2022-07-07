package com.example.logisticdelivery.ui.actualorders

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.logisticdelivery.R
import com.example.logisticdelivery.acra.LogisticDeliveryApplication
import com.example.logisticdelivery.databinding.FragmentActualOrdersListBinding
import com.example.logisticdelivery.model.api.responses.DeliveryOrders
import com.example.logisticdelivery.model.api.responses.responsesEnum.DeliveryOrderStatus
import com.example.logisticdelivery.model.repository.RoomRepository
import com.example.logisticdelivery.ui.actualorders.adapter.ActualOrdersAdapter
import com.example.logisticdelivery.ui.base.BaseViewBindingFragment
import com.example.logisticdelivery.ui.login.common.LoginUiEvent
import com.example.logisticdelivery.ui.main.MainActivity
import com.example.logisticdelivery.ui.orderhistory.OrderHistoryFragment
import com.example.logisticdelivery.ui.properties.AppProperties
import com.example.logisticdelivery.util.LoadingState
import com.example.logisticdelivery.util.observeEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class ActualOrdersFragment : BaseViewBindingFragment<FragmentActualOrdersListBinding>() {

    companion object {
        fun newInstance() = ActualOrdersFragment()
    }

    private val viewModel: ActualOrdersViewModel by viewModels()
    private lateinit var progressBar: ProgressBar
    private val adapter = ActualOrdersAdapter { order,i->
       // var connectToServer:ConnectToServer = ConnectToServer()
        viewModel.deliveryOrders.value?.forEach(
            {
               // connectToServer.setDeliveryStatusToOrder(order.order_no,"0")
            }
        )
        if(i==1){
            viewModel.OnClickListenerDialPhone(order)
        }
        if (i==2){
        viewModel.sendNewStatusOrderDelivered(order,DeliveryOrderStatus.Delivery.value)
            //goToCreateEditFragment()
        }
        if (i==3){
            withArrYourSure(order)

            //viewModel.loadDeliveryList()
            //goToCreateEditFragment()
        }
        if (i==4){
            if (order.deliveryOrderStatus==DeliveryOrderStatus.Ready){
                order.externalNo?.let {
                    RoomRepository.isRestaurantGps(it).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ restGps ->
                                val from:String= AppProperties.selected_latitude.toString()+","+AppProperties.selected_longitude
                                val to:String= restGps.latitude.toString()+","+restGps.longitude
                                OpenMaps(from,to)
                            },{

                            })
                }
            }else if (order.deliveryOrderStatus==DeliveryOrderStatus.Delivery){
                val from:String= AppProperties.selected_latitude.toString()+","+AppProperties.selected_longitude
                order.deliveryOrderRoute?.let {
                    it.deliveryOrderGeolocation?.let {
                        val to:String=it.latitude.toString()+","+it.longitude
                        OpenMaps(from,to)
                    }
                }
            }


        }
        if (i==5){


        }
        if (i==6){
            withEditText()

        }
        //viewModel.deliveredOrder(order)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_actual_orders_list, container, false)
    }
    fun withEditText() {

        Log.e("withEditText", "myTimerIs  timerTask")
        this.context?.let {Log.e("withEditText", "myTimerIs  context")
            val builder = AlertDialog.Builder(it)
            val inflater = layoutInflater
            builder.setTitle(getString(R.string.change_phone))
            val dialogLayout = inflater.inflate(R.layout.fragment_modal_error_order, null)
            //val editText  = dialogLayout.findViewById<EditText>(R.id.input_text)
            //editText.setText(AppProperties.selectDriverPhone)
            builder.setView(dialogLayout)
            builder.setPositiveButton(getString(R.string.Ok)) { dialogInterface, i ->
                withArrYourSure()
            }
            builder.setNegativeButton(getString(R.string.Cancel)) { dialogInterface, i ->
               // val phone = editText.text.toString()
            }
            builder.show()
        }
    }
    private fun withArrYourSure() {
        AppProperties.activityApp?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = layoutInflater
            builder.setTitle(getString(R.string.are_you_sure))
            val dialogLayout = inflater.inflate(R.layout.prompt, null)
            val editText  = dialogLayout.findViewById<EditText>(R.id.input_text)
            editText.visibility=INVISIBLE
            builder.setView(dialogLayout)
            builder.setPositiveButton(R.string.Yes) { dialogInterface, i ->
                //homeViewModel.finishSwift() zapros
            }
            builder.setNegativeButton(R.string.No) { dialogInterface, i ->
            }
            builder.show()
        }
    }
    private fun withArrYourSure(order:DeliveryOrders) {
        AppProperties.activityApp?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = layoutInflater
            builder.setTitle(getString(R.string.are_you_sure))
            val dialogLayout = inflater.inflate(R.layout.prompt, null)
            val editText  = dialogLayout.findViewById<EditText>(R.id.input_text)
            editText.visibility=INVISIBLE
            builder.setView(dialogLayout)
            builder.setPositiveButton(R.string.Yes) { dialogInterface, i ->
                viewModel.sendNewStatusOrderDelivered(order,DeliveryOrderStatus.Delivered.value)
            }
            builder.setNegativeButton(R.string.No) { dialogInterface, i ->
            }
            builder.show()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        initViews()

        viewModel.deliveryOrders.observe(viewLifecycleOwner) { orders ->
            if(orders.size==0){
                binding.orderNotFoundActualTextView.visibility=VISIBLE
            }else{
                binding.orderNotFoundActualTextView.visibility=INVISIBLE
            }
            adapter.submitList(null)
            adapter.notifyDataSetChanged()
            adapter.submitList(orders)

            adapter.notifyDataSetChanged()
        }

        viewModel.showEvent.observeEvent(this) { actualOrdersUiEvent ->
            handleUiEvent(actualOrdersUiEvent)
        }
        viewModel.loadDeliveryList()
        /*binding.searchButton.setOnClickListener {
            //viewModel.loadDeliveryList()
            viewModel.autorization()
        }*/
        AppProperties.isNewOrder=false
    }
    private fun handleUiEvent(uiEvent: ActualOrdersUiEvent) {
        when (uiEvent) {
            is ActualOrdersUiEvent.DisplayLoadingState ->{
                displayLoadingState(uiEvent.loadingState)
            }
            is ActualOrdersUiEvent.Loading -> {
                view?.let {
                    val progressBar = it.findViewById<ProgressBar>(R.id.loading_progress_bar_order)
                    val deliveredRestaurantButton = it.findViewById<Button>(R.id.delivered_restaurant_Button)
                    val deliveredButton = it.findViewById<Button>(R.id.delivered_Button)

                    progressBar?.isVisible = uiEvent.isLoading
                    deliveredButton?.isInvisible = uiEvent.isLoading
                    deliveredRestaurantButton?.isInvisible = uiEvent.isLoading
                }
            }
            is ActualOrdersUiEvent.Error -> {
                Toast.makeText(requireContext(), uiEvent.errorStringId, Toast.LENGTH_LONG)
                        .show()
            }
            is ActualOrdersUiEvent.Success -> {
                Toast.makeText(requireContext(), this.getString(R.string.succes), Toast.LENGTH_LONG)
                        .show()
            }
            is ActualOrdersUiEvent.Order -> {
                Toast.makeText(requireContext(), this.getString(R.string.succes), Toast.LENGTH_LONG)
                        .show()
                view?.let {

                    val deliveredButton = it.findViewById<Button>(R.id.delivered_Button)
                    deliveredButton?.isVisible = uiEvent.isOrder
                }
            }
            is ActualOrdersUiEvent.Restaurant -> {
                Toast.makeText(requireContext(), this.getString(R.string.succes), Toast.LENGTH_LONG)
                        .show()
                view?.let {
                    val deliveredButton = it.findViewById<Button>(R.id.delivered_restaurant_Button)
                    deliveredButton?.isVisible = uiEvent.isRestaurant
                }
            }
            is ActualOrdersUiEvent.Update -> {
                Toast.makeText(requireContext(), "update", Toast.LENGTH_LONG)
                        .show()
            }
            is ActualOrdersUiEvent.Confirm -> {
                navigateToActiveOrderFragment()
            }
            is ActualOrdersUiEvent.IsOrders -> {
                if(uiEvent.IsOrders){
                    binding.orderNotFoundActualTextView.visibility=VISIBLE
                }else{
                    binding.orderNotFoundActualTextView.visibility=INVISIBLE
                }
            }
        }
    }
    private fun goToCreateEditFragment( ) {
       val frafment = OrderHistoryFragment.newInstance()
        parentFragmentManager.beginTransaction()
            .replace(R.id.drawer_layout,frafment)
            .addToBackStack(null)
            .commit()
    }
    private fun setUpRecyclerView() {
        val recyclerView = binding.recyclerViewActual
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        recyclerView.refreshDrawableState()
    }
    override fun initViewBinding(view: View): FragmentActualOrdersListBinding {
        return FragmentActualOrdersListBinding.bind(view)
    }
    private fun initViews() {
       // progressBar = binding.progress
    }
    private fun displayLoadingState(loadingState: LoadingState) {

        when (loadingState) {
            is LoadingState.Error -> {
                progressBar.isVisible = false
                Toast.makeText(LogisticDeliveryApplication.instance, loadingState.throwable.message, Toast.LENGTH_SHORT).show()
            }
            LoadingState.Loading -> {
                progressBar.isVisible = true
                Toast.makeText(LogisticDeliveryApplication.instance, "Loading", Toast.LENGTH_SHORT).show()
            }
            LoadingState.Success -> {
                progressBar.isVisible = false
                Toast.makeText(LogisticDeliveryApplication.instance, "Success", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun OpenMaps(from:String,to:String){
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("http://maps.google.com/maps?saddr=${from}&daddr=${to}"))
        startActivity(intent)
    }

    private fun navigateToActiveOrderFragment() {
        findNavController().navigate(
                R.id.action_nav_actual_order_to_nav_home
        )
    }
}
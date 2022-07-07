package com.example.logisticdelivery.ui.actualorders

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.logisticdelivery.acra.LogisticDeliveryApplication
import com.example.logisticdelivery.model.api.responses.DeliveryOrders
import com.example.logisticdelivery.model.api.responses.DriverGeolocation
import com.example.logisticdelivery.model.api.responses.responsesEnum.DeliveryOrderStatus
import com.example.logisticdelivery.model.repository.Repository
import com.example.logisticdelivery.model.repository.RoomRepository
import com.example.logisticdelivery.ui.base.BaseViewModel
import com.example.logisticdelivery.ui.login.common.LoginUiEvent
import com.example.logisticdelivery.ui.properties.AppProperties
import com.example.logisticdelivery.util.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

class ActualOrdersViewModel : BaseViewModel() {

    private val _deliveryOrders = RoomRepository.getNewDeliveryOrders(AppProperties.selectDriverId)
    //private val _deliveryOrders = MutableLiveData<List<DeliveryOrders>>()
    private val _showEvent = EventMutableLiveData<ActualOrdersUiEvent>()
    private var  mainrep:RoomRepository = RoomRepository
   // val deliveryOrders: LiveData<List<DeliveryOrders>> = _deliveryOrders
    var deliveryOrders: LiveData<List<DeliveryOrders>> = RoomRepository.getNewDeliveryOrders(AppProperties.selectDriverId)
    val showEvent: EventLiveData<ActualOrdersUiEvent> = _showEvent
    fun deliveredOrder(order: DeliveryOrders) {
        _showEvent.call(ActualOrdersUiEvent.DisplayLoadingState(LoadingState.Loading))
        disposeOnCleared(
            RoomRepository.updateDeliveredOrder(order),
            {
                _showEvent.call(ActualOrdersUiEvent.DisplayLoadingState(LoadingState.Success))
            }
        ) {
            _showEvent.call(ActualOrdersUiEvent.DisplayLoadingState(LoadingState.Error(it)))
        }
    }
    fun autorization(){
        Repository.autorization(AppProperties.login_api,AppProperties.password_api)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ token ->
                    AppProperties.token=token
                    Repository.getDeliveryOrder("2222","Bearer "+AppProperties.token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ token ->
                            mainrep.insertDeliveryOrders(token)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ id ->
                                    println("success. id = $id")
                                }, { exception ->
                                    println("error: ${exception.message}")
                                })
                            Log.d("MainViewModeltoken", "sss\n" + token)
                        }, { throwable ->
                            Log.e("MainViewModeltoken", "Error\n" + throwable.message.toString())
                        })
                    Log.d("MainViewModeltoken", "Success\n" + token)
                }, { throwable ->
                    Log.e("MainViewModeltoken", "Error\n" + throwable.message.toString())
                })

        Log.e("success. id = ",mainrep.getAllDrivers().toString())

    }
    fun   loadDeliveryList() {

         mainrep.getAllDeliveryOrdersSingle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.firstOrNull()?.let {

                }

            },{
            })
    }
    fun   clearDeliveryList() {
        deliveryOrders.value
    }


    fun sendNewStatusOrderDelivered(order:DeliveryOrders,statusId:String) {
        try {
            Log.e("location statusI String", statusId)
            _showEvent.value = Event(ActualOrdersUiEvent.Loading(true))
            Repository.autorization(AppProperties.login_api, AppProperties.password_api)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ token ->
                    AppProperties.token = token
                    Repository.sendDeliveryOrderStatus(
                        order.deliveryOrderId,
                        statusId,
                        "Bearer " + AppProperties.token
                    )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ token ->
                            if (Build.VERSION.SDK_INT >= 26) {
                                try {

                                    val myDate = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
                                        .toString()
                                    order.deliveryOrderRoute?.let {
                                        it.actualFinishTimeRoute = myDate
                                    }
                                    when (statusId){
                                        "5"->{
                                            order.deliveryOrderStatus  = DeliveryOrderStatus.Ready
                                        }
                                        "6"->{
                                            order.deliveryOrderStatus  = DeliveryOrderStatus.Delivery
                                        }
                                        "7"->{
                                            AppProperties.countOrdersToday+=1
                                            order.deliveryOrderStatus  = DeliveryOrderStatus.Delivered
                                        }
                                    }


                                    mainrep.updateDeliveredOrder(order)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({

                                            eventOrder(statusId+1)
                                        },{
                                            eventOrder(statusId+1)
                                        })

                                } catch (e: NumberFormatException) {
                                    eventOrder(statusId+1)
                                }

                            } else {
                                val myDatef = getCurrentDateTime()
                                val myDate =
                                    myDatef.toString("yyyy-MM-dd") + "T" + myDatef.toString("hh:mm:ss") + ".000Z"

                                order.deliveryOrderRoute?.let {
                                    it.actualFinishTimeRoute = myDate
                                }
                                when (statusId){
                                    "5"->{
                                        order.deliveryOrderStatus  = DeliveryOrderStatus.Ready
                                    }
                                    "6"->{
                                        order.deliveryOrderStatus  = DeliveryOrderStatus.Delivery
                                    }
                                    "7"->{
                                        order.deliveryOrderStatus  = DeliveryOrderStatus.Delivered
                                    }
                                }

                                mainrep.updateDeliveredOrder(order)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({

                                                eventOrder(statusId+1)
                                    },{
                                        eventOrder(statusId+1)
                                    })

                                Log.e("location", myDate)


                            }
                            Log.d("MainViewModeltoken", "sendDeliveryOrderStatussss\n" + token)
                        }, { throwable ->
                            eventOrder(statusId+1)
                            Log.e(
                                "MainViewModeltoken",
                                "sendDeliveryOrderStatusError\n" + throwable.message.toString()
                            )
                        })
                    Log.d("MainViewModeltoken", "sendDeliveryOrderStatusSuccess\n" + token)
                }, { throwable ->
                    eventOrder(statusId+1)
                    Log.e(
                        "MainViewModeltoken",
                        "sendDeliveryOrderStatusError\n" + throwable.message.toString()
                    )
                })
        }catch (e:Exception){
            eventOrder(statusId+1)
        }
       // connectToServer.deliveryOrderList()
    }

    fun degreesToRadians(degrees:Double):Double {
        return degrees * Math.PI / 180;
    }

    fun distanceInKmBetweenEarthCoordinates(lat1:Double, lon1:Double, lat2:Double, lon2:Double):Double {
        var earthRadiusKm = 6371;

        var dLat = degreesToRadians(lat2-lat1);
        var dLon = degreesToRadians(lon2-lon1);

        var lat1buf = degreesToRadians(lat1);
        var lat2buf = degreesToRadians(lat2);

        var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1buf) * Math.cos(lat2buf);
        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return earthRadiusKm * c;
    }

    fun sendNewStatusOrderRestaurant(order:DeliveryOrders,statusId:String) {
        try {
            _showEvent.value = Event(ActualOrdersUiEvent.Loading(true))
            Repository.autorization(AppProperties.login_api, AppProperties.password_api)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ token ->
                    AppProperties.token = token
                    Repository.sendDeliveryOrderStatus(
                        order.deliveryOrderId,
                        statusId,
                        "Bearer " + AppProperties.token
                    )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ token ->
                            if (Build.VERSION.SDK_INT >= 26) {
                                try {

                                    val myDate = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
                                        .toString()

                                    order.deliveryOrderRoute?.let {
                                        it.actualFinishTimeRoute = myDate
                                    }
                                    order.deliveryOrderStatus.value = statusId

                                    mainrep.updateDeliveredOrder(order)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({
                                            eventOrder(statusId+1)
                                        },{
                                            eventOrder(statusId+1)
                                        })

                                } catch (e: NumberFormatException) {
                                    eventOrder(statusId+1)
                                }

                            } else {
                                val myDatef = getCurrentDateTime()
                                val myDate =
                                    myDatef.toString("yyyy-MM-dd") + "T" + myDatef.toString("hh:mm:ss") + ".000Z"

                                order.deliveryOrderRoute?.let {
                                    it.actualFinishTimeRoute = myDate
                                }
                                order.deliveryOrderStatus.value = statusId
                                mainrep.updateDeliveredOrder(order)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({

                                                eventOrder(statusId+1)
                                    },{
                                        eventOrder(statusId+1)
                                    })

                                Log.e("location", myDate)


                            }
                            Log.d("MainViewModeltoken", "sendDeliveryOrderStatussss\n" + token)
                        }, { throwable ->
                            eventOrder(statusId+1)
                            Log.e(
                                "MainViewModeltoken",
                                "sendDeliveryOrderStatusError\n" + throwable.message.toString()
                            )
                        })
                    Log.d("MainViewModeltoken", "sendDeliveryOrderStatusSuccess\n" + token)
                }, { throwable ->
                    eventOrder(statusId+1)
                    Log.e(
                        "MainViewModeltoken",
                        "sendDeliveryOrderStatusError\n" + throwable.message.toString()
                    )
                })
        }catch (e:Exception){
            eventOrder(statusId+1)
        }
       // connectToServer.deliveryOrderList()
    }

    fun OnClickListenerDialPhone(order: DeliveryOrders){
        if (order!=null) {
            val phone_no = order.phone
            phone_no.let {

                val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone_no"))
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                LogisticDeliveryApplication.instance?.let{
                   it.startActivity(callIntent)
                }
            }
        }
    }
    fun isOrdersCount(){

    }
    fun eventOrder(statusId:String){
        _showEvent.value = Event(ActualOrdersUiEvent.Loading(false))
        if(statusId=="5"){
            _showEvent.value = Event(ActualOrdersUiEvent.Restaurant(true))
        }else if(statusId =="6"){
            _showEvent.value = Event(ActualOrdersUiEvent.Order(true))

            _showEvent.value = Event(ActualOrdersUiEvent.Confirm(true))
        }

    }
    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

}
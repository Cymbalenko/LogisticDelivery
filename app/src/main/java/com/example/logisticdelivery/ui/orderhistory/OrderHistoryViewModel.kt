package com.example.logisticdelivery.ui.orderhistory

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.logisticdelivery.acra.LogisticDeliveryApplication
import com.example.logisticdelivery.model.api.responses.DeliveryOrders
import com.example.logisticdelivery.model.repository.RoomRepository
import com.example.logisticdelivery.ui.base.BaseViewModel
import com.example.logisticdelivery.ui.properties.AppProperties
import com.example.logisticdelivery.util.EventLiveData
import com.example.logisticdelivery.util.EventMutableLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class OrderHistoryViewModel : BaseViewModel() {
    private var _deliveryOrders =  MutableLiveData<List<DeliveryOrders>>()
    private val _showEvent = EventMutableLiveData<OrdersHistoryUiEvent>()

    var deliveryOrders: LiveData<List<DeliveryOrders>> = _deliveryOrders
    val showEvent: EventLiveData<OrdersHistoryUiEvent> = _showEvent
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
    fun  getListOrders(date:String){
        //Log.d("location", "staart\n" + "'-"+date+" day'")
        //Log.d("location_deliveryOrders", _deliveryOrders.value?.size.toString())
        RoomRepository.getDeliveryOrdersHistoryDay(date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ recept ->
                    _deliveryOrders.value=recept
                    Log.e("location_deliveryOrder", "Error\n" + recept.size.toString())
                }, { throwable ->
                    Log.e("location_deliveryOrder", "Error\n" + throwable.message.toString())
                })


        //deliveryOrders =  buff
             /*.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                Log.d("location", "ediokkkkkkkt_text\n" + it.count())
                _deliveryOrders.value = it

            },{

                Log.d("location", "errrrr\n" + it.localizedMessage)
            })*/

    }
}
package com.example.logisticdelivery.ui.actualorders.adapter

import android.graphics.Color
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.logisticdelivery.R
import com.example.logisticdelivery.model.api.responses.DeliveryOrders
import com.example.logisticdelivery.model.repository.RoomRepository
import com.example.logisticdelivery.ui.properties.AppProperties
import com.google.android.gms.maps.MapView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs


class ActualOrdersAdapter(private val clickListener: (DeliveryOrders,Int) -> Unit
) : ListAdapter<DeliveryOrders, ActualOrdersAdapter.OrdersViewHolder>(OrdersDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val viewToClient = LayoutInflater.from(parent.context).inflate(R.layout.fragment_actual_orders_to_client_item, parent, false)
        return OrdersViewHolder(viewToClient, clickListener)
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        getItem(position)?.let { order ->
            holder.bind(order)
        }
    }

    class OrdersViewHolder(view: View, private val clickListener: (DeliveryOrders,Int) -> Unit) : RecyclerView.ViewHolder(view) {

        fun bind(orders: DeliveryOrders) {

            val buttonError = itemView.findViewById<ImageButton>(R.id.error_image_button)
            buttonError.setOnClickListener {
                Log.d("location", "buttonError")
                clickListener(orders,6)
            }
            if (orders.deliveryOrderStatus.value=="5")
            //if (orders.id==1)
            {
                // Initial Names
                val textViewTypeViewText = itemView.findViewById<TextView>(R.id.text_view_to_client_actual)
                val textViewTimeOrderText = itemView.findViewById<TextView>(R.id.text_view_time_order_text)
                val textViewPhoneText = itemView.findViewById<TextView>(R.id.text_view_phone_text)
                val textViewAdressClientText = itemView.findViewById<TextView>(R.id.text_view_adress_client_text)
                val textViewCommentClientText = itemView.findViewById<TextView>(R.id.text_view_comment_client_text)

                // Initial Values
                val textViewAdress = itemView.findViewById<TextView>(R.id.text_view_adress_actual)
                val textViewAdress2 = itemView.findViewById<TextView>(R.id.text_view_adress2_actual)
                val buttonPhone = itemView.findViewById<Button>(R.id.text_view_phone_actual)
                val buttonDelivered = itemView.findViewById<Button>(R.id.delivered_Button)
                val buttonDeliveredRestaurant = itemView.findViewById<Button>(R.id.delivered_restaurant_Button)
                val buttonMap = itemView.findViewById<ImageButton>(R.id.map_image_button)
                val textViewName = itemView.findViewById<TextView>(R.id.text_view_name_actual)
                val textViewOrderId = itemView.findViewById<TextView>(R.id.text_view_orderdId_actual)
                val textViewAPay = itemView.findViewById<TextView>(R.id.text_view_pay_actual)
                val textViewAmount = itemView.findViewById<TextView>(R.id.text_view_amount_actual)
                val textViewComment = itemView.findViewById<TextView>(R.id.text_view_comment_actual)
                val textViewOrderdt = itemView.findViewById<TextView>(R.id.text_view_orderdt_actual)

                //Names
                textViewTypeViewText.text=itemView.resources.getString(R.string.To_restaurant)
                textViewTypeViewText.setBackgroundColor(Color.argb(100,255,193,7))
                //textViewTimeOrderText.text=itemView.resources.getString(R.string.TimeRestaurant)
                textViewPhoneText.text=itemView.resources.getString(R.string.PhoneRestaurant)
                textViewAdressClientText.text = itemView.resources.getString(R.string.AdressRestaurant)
                textViewCommentClientText.visibility = GONE


                orders.externalNo?.let {
                    RoomRepository.isRestaurant(it)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ rest->

                            buttonPhone.text = rest.phone
                            buttonPhone.tag = rest.phone
                        },{
                        })
                }

                //Values
                textViewAdress.text = orders.restaurantName
                textViewName.text = orders.name
                textViewOrderId.text = orders.receptNo
                textViewAPay.text = orders.tenderType.name
                textViewAmount.text=orders.amount.toString()
                textViewComment.visibility = GONE
                textViewAdress2.visibility = GONE
                if(orders.approximateTimeOrderReadyRestaurant.toString().length>18){
                    val calendarCurrentTime = Calendar.getInstance()
                    val calendarOrder = Calendar.getInstance()
                    val year = orders.approximateTimeOrderReadyRestaurant.toString().substring(0,4).toInt()
                    val month = orders.approximateTimeOrderReadyRestaurant.toString().substring(5,7).toInt()-1
                    val day = orders.approximateTimeOrderReadyRestaurant.toString().substring(8,10).toInt()
                    val hour = orders.approximateTimeOrderReadyRestaurant.toString().substring(11,13).toInt()
                    val minute = orders.approximateTimeOrderReadyRestaurant.toString().substring(14,16).toInt()
                    val second = orders.approximateTimeOrderReadyRestaurant.toString().substring(17,19).toInt()
                    calendarOrder.set(year,month,day,hour,minute,second)
                    calendarCurrentTime.timeZone = TimeZone.getDefault()
                    calendarOrder.timeZone = TimeZone.getDefault()

                    val myDateInt = TimeUnit.MILLISECONDS.toMinutes(calendarOrder.timeInMillis - calendarCurrentTime.timeInMillis)
                    val myDate = myDateInt.toString()
                    if (AppProperties.selectDeliveryOrder!=""&&AppProperties.selectDeliveryOrder!=orders.deliveryOrderId.toString()){
                        AppProperties.selectDeliveryOrder=orders.deliveryOrderId
                        AppProperties.isLateOrderDate=calendarOrder.timeInMillis
                    }
                    //val myDate =orders.orderDateTime.toString().substring(11,16)
                    textViewOrderdt.text=myDate
                    if (myDateInt<=0){
                        textViewOrderdt.setTextColor(Color.RED)
                    }else{
                        textViewOrderdt.setTextColor(Color.GREEN)
                    }
                }
                buttonPhone.setOnClickListener {
                    clickListener(orders, 1)
                    Log.d("location", "buttonPhone")
                }
                buttonDelivered.visibility = GONE
                buttonDeliveredRestaurant.setOnClickListener {
                    Log.d("location", "buttonDeliveredRestaurant")
                    clickListener(orders,2)
                }
                buttonMap.setOnClickListener {
                    Log.d("location", "buttonDelivered")
                    clickListener(orders,4)
                }
                var intRes = 0
                orders.externalNo?.let {
                    RoomRepository.isRestaurantGps(it).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ restGps ->
                            Log.d("distanceInKm3", restGps.latitude.toString())
                            Log.d("distanceInKm4", restGps.longitude.toString())
                            val restGpslatitude = restGps.latitude
                            val restGpslongitude = restGps.longitude

                            if(AppProperties.selected_longitude!=0.0 && AppProperties.selected_latitude!=0.0){
                                if(restGpslatitude!=0.0 && restGpslongitude!=0.0){
                                    val resDec=abs(distanceInKmBetweenEarthCoordinates(AppProperties.selected_latitude,AppProperties.selected_longitude,restGpslatitude,restGpslongitude))*1000
                                    if(resDec>500){
                                        buttonDeliveredRestaurant.visibility = VISIBLE
                                        buttonDeliveredRestaurant.isEnabled = false
                                        buttonDeliveredRestaurant.text = resDec.toInt().toString()+"m"
                                        intRes=1
                                    }else{
                                        buttonDeliveredRestaurant.visibility = VISIBLE
                                        buttonDeliveredRestaurant.isEnabled = true
                                        intRes=2
                                    }
                                }
                            }
                        },{

                        })
                }
                Log.d("distanceInKm1", AppProperties.selected_longitude.toString())
                Log.d("distanceInKm2", AppProperties.selected_latitude.toString())

                if(intRes==0){
                    buttonDeliveredRestaurant.visibility = VISIBLE
                }


            }
            else if(orders.deliveryOrderStatus.value=="6"||orders.deliveryOrderStatus.value=="7")
            //else if(orders.id==2)
            {
                // Initial Names
                val textViewTypeViewText = itemView.findViewById<TextView>(R.id.text_view_to_client_actual)
                val textViewTimeOrderText = itemView.findViewById<TextView>(R.id.text_view_time_order_text)
                val textViewPhoneText = itemView.findViewById<TextView>(R.id.text_view_phone_text)
                val textViewAdressClientText = itemView.findViewById<TextView>(R.id.text_view_adress_client_text)
                val textViewCommentClientText = itemView.findViewById<TextView>(R.id.text_view_comment_client_text)

                // Initial Values
                val textViewAdress = itemView.findViewById<TextView>(R.id.text_view_adress_actual)
                val textViewAdress2 = itemView.findViewById<TextView>(R.id.text_view_adress2_actual)
                val buttonPhone = itemView.findViewById<Button>(R.id.text_view_phone_actual)
                val buttonDelivered = itemView.findViewById<Button>(R.id.delivered_Button)
                val buttonDeliveredRestaurant = itemView.findViewById<Button>(R.id.delivered_restaurant_Button)
                val textViewName = itemView.findViewById<TextView>(R.id.text_view_name_actual)
                val textViewOrderId = itemView.findViewById<TextView>(R.id.text_view_orderdId_actual)
                val textViewAPay = itemView.findViewById<TextView>(R.id.text_view_pay_actual)
                val textViewAmount = itemView.findViewById<TextView>(R.id.text_view_amount_actual)
                val textViewComment = itemView.findViewById<TextView>(R.id.text_view_comment_actual)
                val textViewOrderdt = itemView.findViewById<TextView>(R.id.text_view_orderdt_actual)
                val buttonMap = itemView.findViewById<ImageButton>(R.id.map_image_button)

                //Names
                textViewComment.visibility = VISIBLE
                textViewTypeViewText.text=itemView.resources.getString(R.string.To_client)
                textViewTypeViewText.setBackgroundColor(Color.argb(100,3,169,244))
                    //textViewTimeOrderText.text=itemView.resources.getString(R.string.TimeDelivery)
                textViewPhoneText.text=itemView.resources.getString(R.string.PhoneClient)
                textViewAdressClientText.text=itemView.resources.getString(R.string.AdressClient)


                //Values
                textViewComment.visibility = VISIBLE
                textViewAdress2.visibility = VISIBLE
                textViewAdress.text = orders.streetName
                textViewAdress2.text = orders.streetNo
                buttonPhone.text = orders.phone
                buttonPhone.tag = orders.phone
                textViewName.text = orders.name
                textViewOrderId.text = orders.receptNo
                textViewAPay.text = orders.tenderType.name
                textViewAmount.text=orders.amount.toString()
                textViewComment.text=orders.direction.toString()
                if(orders.orderDateTime.toString().length>18){
                    val calendarCurrentTime = Calendar.getInstance()
                    val calendarOrder = Calendar.getInstance()
                    val year = orders.orderDateTime.toString().substring(0,4).toInt()
                    val month = orders.orderDateTime.toString().substring(5,7).toInt()-1
                    val day = orders.orderDateTime.toString().substring(8,10).toInt()
                    val hour = orders.orderDateTime.toString().substring(11,13).toInt()
                    val minute = orders.orderDateTime.toString().substring(14,16).toInt()
                    val second = orders.orderDateTime.toString().substring(17,19).toInt()
                    calendarOrder.set(year,month,day,hour,minute,second)
                    calendarCurrentTime.timeZone = TimeZone.getDefault()
                    calendarOrder.timeZone = TimeZone.getDefault()
                    val myDateInt = TimeUnit.MILLISECONDS.toMinutes(calendarOrder.timeInMillis - calendarCurrentTime.timeInMillis)
                    val myDate = myDateInt.toString()
                    if (AppProperties.selectDeliveryOrder!=""&&AppProperties.selectDeliveryOrder!=orders.deliveryOrderId.toString()){
                        AppProperties.selectDeliveryOrder=orders.deliveryOrderId
                        AppProperties.isLateOrderDate=calendarOrder.timeInMillis
                    }

                    //val myDate =orders.orderDateTime.toString().substring(11,16)
                    textViewOrderdt.text=myDate
                    if (myDateInt<=0){
                        textViewOrderdt.setTextColor(Color.RED)
                    }else{
                        textViewOrderdt.setTextColor(Color.GREEN)
                    }
                }
                buttonPhone.setOnClickListener {
                    Log.d("location", "buttonPhone")
                    clickListener(orders,1)
                }
                buttonDelivered.setOnClickListener {
                    Log.d("location", "buttonDelivered")
                    clickListener(orders,3)
                }
                buttonMap.setOnClickListener {
                    Log.d("location", "buttonDelivered")
                    clickListener(orders,4)
                }
                var intRes = 0
                Log.d("distanceInKm1", AppProperties.selected_longitude.toString())
                Log.d("distanceInKm2", AppProperties.selected_latitude.toString())
                Log.d("distanceInKm3", orders?.deliveryOrderRoute?.deliveryOrderGeolocation?.longitude.toString())
                Log.d("distanceInKm4", orders?.deliveryOrderRoute?.deliveryOrderGeolocation?.latitude.toString())
                orders.let { order->
                    order.deliveryOrderRoute.let { orderRoute->
                        orderRoute?.deliveryOrderGeolocation.let { geo->
                            geo?.latitude.let { lat->
                                geo?.longitude.let { lon->
                                    lat?.let { latD->
                                        lon?.let { lonD->
                                            if(latD>0 && lonD>0){
                                                if(AppProperties.selected_longitude!=0.0 && AppProperties.selected_latitude!=0.0){
                                                    val resDec=abs(distanceInKmBetweenEarthCoordinates(latD,lonD,AppProperties.selected_latitude,AppProperties.selected_longitude))*1000
                                                    if(resDec>500){
                                                        buttonDelivered.visibility = VISIBLE
                                                        buttonDelivered.isEnabled = false
                                                        buttonDelivered.text = resDec.toInt().toString()+"m"
                                                        intRes=1
                                                    }else{
                                                        buttonDelivered.visibility = VISIBLE
                                                        buttonDelivered.isEnabled = true
                                                        intRes=2
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if(intRes==0){
                    buttonDelivered.visibility = VISIBLE
                }
                buttonDeliveredRestaurant.visibility = INVISIBLE

            }


        }
        fun degreesToRadians(degrees:Double):Double {
            return degrees * Math.PI / 180
        }

        fun distanceInKmBetweenEarthCoordinates(lat1:Double, lon1:Double, lat2:Double, lon2:Double):Double {
            var earthRadiusKm = 6371

            var dLat = degreesToRadians(lat2-lat1)
            var dLon = degreesToRadians(lon2-lon1)

            var lat1buf = degreesToRadians(lat1)
            var lat2buf = degreesToRadians(lat2)

            var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                    Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1buf) * Math.cos(lat2buf)
            var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a))
            val res=earthRadiusKm * c
            Log.d("distanceInKmB", res.toString())
            return res
        }

    }

    class OrdersDiffUtil : DiffUtil.ItemCallback<DeliveryOrders>() {
        override fun areItemsTheSame(oldItem: DeliveryOrders, newItem: DeliveryOrders): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DeliveryOrders, newItem: DeliveryOrders): Boolean {
            return oldItem == newItem
        }
    }
}
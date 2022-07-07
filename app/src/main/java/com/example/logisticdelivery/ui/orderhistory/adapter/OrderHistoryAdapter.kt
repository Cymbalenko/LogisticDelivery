package com.example.logisticdelivery.ui.orderhistory.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.logisticdelivery.R
import com.example.logisticdelivery.model.api.responses.DeliveryOrders

class OrderHistoryAdapter (private val clickListener: (DeliveryOrders,Int) -> Unit) : ListAdapter<DeliveryOrders, OrderHistoryAdapter.OrdersViewHolder>(OrdersDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_orders_history_item, parent, false)
        return OrdersViewHolder(view,clickListener)
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        getItem(position)?.let { order ->
            holder.bind(order)
        }
    }

    class OrdersViewHolder(view: View, private val clickListener: (DeliveryOrders,Int) -> Unit) : RecyclerView.ViewHolder(view) {


        fun bind(orders: DeliveryOrders) {
            val textViewTimeOrderText = itemView.findViewById<TextView>(R.id.text_view_time_order_text_history)
            val textViewPhoneText = itemView.findViewById<TextView>(R.id.text_view_phone_text_history)
            val textViewAdressClientText = itemView.findViewById<TextView>(R.id.text_view_adress_client_text_history)
            val textViewCommentClientText = itemView.findViewById<TextView>(R.id.text_view_comment_client_text_history)

            // Initial Values
            val textViewAdress = itemView.findViewById<TextView>(R.id.text_view_adress_history)
            val buttonPhone = itemView.findViewById<Button>(R.id.text_view_phone_history)
            val textViewName = itemView.findViewById<TextView>(R.id.text_view_name_history)
            val textViewOrderId = itemView.findViewById<TextView>(R.id.text_view_orderdId_history)
            val textViewAPay = itemView.findViewById<TextView>(R.id.text_view_pay_history)
            val textViewAmount = itemView.findViewById<TextView>(R.id.text_view_amount_history)
            val textViewComment = itemView.findViewById<TextView>(R.id.text_view_comment_history)
            val textViewOrderdt = itemView.findViewById<TextView>(R.id.text_view_orderdt_history)

            //Names
            textViewTimeOrderText.text=itemView.resources.getString(R.string.time_delivered)
            textViewPhoneText.text=itemView.resources.getString(R.string.PhoneClient)
            textViewAdressClientText.text=itemView.resources.getString(R.string.AdressClient)
            textViewCommentClientText.text = orders.externalNo

            //Values
            textViewAdress.text = orders.streetName
            buttonPhone.text = orders.phone
            buttonPhone.tag = orders.phone
            textViewName.text = orders.name
            textViewOrderId.text = orders.receptNo
            textViewAPay.text = orders.tenderType.name
            textViewAmount.text=orders.amount.toString()
            textViewComment.text = orders.restaurantName
            orders.deliveryOrderRoute?.let {
                if(it.actualFinishTimeRoute.toString().length>15){
                    val myDate =it.actualFinishTimeRoute.toString().substring(11,16)
                    textViewOrderdt.text=myDate
                }
            }

            buttonPhone.setOnClickListener { clickListener(orders,1)}
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
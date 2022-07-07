package com.example.logisticdelivery.ui.orderhistory

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.logisticdelivery.R
import com.example.logisticdelivery.acra.LogisticDeliveryApplication
import com.example.logisticdelivery.databinding.FragmentOrdersHistoryListBinding
import com.example.logisticdelivery.ui.base.BaseViewBindingFragment
import com.example.logisticdelivery.ui.orderhistory.adapter.OrderHistoryAdapter
import com.example.logisticdelivery.util.LoadingState
import com.example.logisticdelivery.util.observeEvent
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class OrderHistoryFragment : BaseViewBindingFragment<FragmentOrdersHistoryListBinding>() {

    companion object {
        fun newInstance() = OrderHistoryFragment ()
    }

    private val viewModel: OrderHistoryViewModel by viewModels()
    private val adapter = OrderHistoryAdapter { order, i ->

        if(i==1){
            viewModel.OnClickListenerDialPhone(order)
        }else if (i==2){

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_orders_history_list, container, false)
    }

    private fun openDobPicker(day_tv: TextView, month_tv: TextView, year_tv: TextView) {
        val c = Calendar.getInstance()
       // val year = c.get(Calendar.YEAR)
       // val month = c.get(Calendar.MONTH)
       // val day = c.get(Calendar.DAY_OF_MONTH)
        val year = year_tv.text.toString().toInt()
        val month = month_tv.text.toString().toInt()
        val day = day_tv.text.toString().toInt()

        Log.d("locationsss", "day${day}-month${month}-year${year}- ")
        val dpd = DatePickerDialog(
            this.requireContext(),
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                var day="0"
                var mounth="0"
                var years="0"
                if(dayOfMonth<10){
                    day="0"+dayOfMonth
                }else{
                    day=dayOfMonth.toString()
                }
                if((monthOfYear+1)<10){
                    mounth="0"+(monthOfYear+1)
                }else{
                    mounth=(monthOfYear+1).toString()
                }
                if(year<10){
                    years="0"+year
                }else{
                    years=year.toString()
                }

                day_tv.text = day
                month_tv.text = mounth
                year_tv.text = years
                //val edit_text = daysBetween(dayOfMonth, monthOfYear ,year )

                val edit_text ="${years}-${mounth}-${day}"
                Log.d("location", "edit_text\n" + edit_text)
                Toast.makeText(view.context,edit_text.toString(),Toast.LENGTH_SHORT).show()
                //Toast.makeText(view.context,"$dayOfMonth -  $monthOfYear  - $year",Toast.LENGTH_SHORT).show()
                viewModel.getListOrders(edit_text.toString())
                //viewModel.getListOrders()
            },
            year,
                (month-1),
            day
        )

        //restrict calendar to show future dates
        dpd.datePicker.maxDate = Date().time
        //If you want to limit the minimum date then do this
        //dpd.datePicker.minDate = PASSED_MIN_DATE_HERE



        dpd.show()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView(view)
        viewModel.deliveryOrders.observe(viewLifecycleOwner) { orders ->
            if(orders.size==0){
                binding.orderNotFoundHistoryTextView.visibility= View.VISIBLE
            }else{
                binding.orderNotFoundHistoryTextView.visibility= View.INVISIBLE
            }
            adapter.submitList(orders)
            adapter.notifyDataSetChanged()
        }
        viewModel.showEvent.observeEvent(this) { order ->
            handleUiEvent(order)
        }
        view.let {
            val calendarDayText = it.findViewById<TextView>(R.id.calendarDayTextView)
            val calendarMonthText = it.findViewById<TextView>(R.id.calendarMonthTextView)
            val calendarYearText = it.findViewById<TextView>(R.id.calendarYearTextView)
            val buttonSearch = it.findViewById<Button>(R.id.search_history_button)
            val myDatef = getCurrentDateTime()
            calendarDayText.text=myDatef.toString("dd")
            calendarMonthText.text=myDatef.toString("MM")
            calendarYearText.text=myDatef.toString("yyyy")
            buttonSearch.setOnClickListener { openDobPicker(
                calendarDayText,
                calendarMonthText,
                calendarYearText
            )}
        }


    }

    fun daysBetween(day:Int,month:Int,year:Int): Long {

        //Make sure we don't change the parameter passed
        val newStart = Calendar.getInstance()
        newStart.set(year,month , day,0,0,0)
        val newEnd = Calendar.getInstance()
        //newEnd.timeInMillis = endDate.timeInMillis
        val end = newEnd.timeInMillis
        val start = newStart.timeInMillis
        return TimeUnit.MILLISECONDS.toDays(Math.abs(end - start))
    }

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }
    fun getCurrentDateTimeCalendar(): Calendar {
        return Calendar.getInstance()
    }
    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }
    private fun handleUiEvent(uiEvent: OrdersHistoryUiEvent) {
        when (uiEvent) {
            is OrdersHistoryUiEvent.DisplayLoadingState -> displayLoadingState(uiEvent.loadingState)
        }
    }
    private fun setUpRecyclerView(view: View) {
        val recyclerView = binding.recyclerViewHistory
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }
    override fun initViewBinding(view: View): FragmentOrdersHistoryListBinding {
        return FragmentOrdersHistoryListBinding.bind(view)
    }
    private fun displayLoadingState(loadingState: LoadingState) {

        when (loadingState) {
            is LoadingState.Error -> {
                Toast.makeText(
                    LogisticDeliveryApplication.instance,
                    loadingState.throwable.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            LoadingState.Loading -> {
                Toast.makeText(LogisticDeliveryApplication.instance, "Loading", Toast.LENGTH_SHORT)
                    .show()
            }
            LoadingState.Success -> {
                Toast.makeText(LogisticDeliveryApplication.instance, "Success", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}
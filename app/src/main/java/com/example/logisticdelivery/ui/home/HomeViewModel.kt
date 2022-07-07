package com.example.logisticdelivery.ui.home

import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.logisticdelivery.acra.LogisticDeliveryApplication
import com.example.logisticdelivery.model.api.responses.responsesEnum.DeliveryOrderStatus
import com.example.logisticdelivery.model.api.responses.responsesEnum.DriverStatus
import com.example.logisticdelivery.model.repository.Repository
import com.example.logisticdelivery.model.repository.RoomRepository
import com.example.logisticdelivery.ui.base.BaseViewModel
import com.example.logisticdelivery.ui.home.common.HomeUiEvent
import com.example.logisticdelivery.ui.login.common.LoginUiEvent
import com.example.logisticdelivery.ui.properties.AppProperties
import com.example.logisticdelivery.util.Event
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import kotlin.concurrent.timerTask

class HomeViewModel : BaseViewModel() {
    private var  mainrep:RoomRepository = RoomRepository
    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    var deliveryOrdersCount: LiveData<Int> = RoomRepository.getHistoryDeliveryOrdersCount(AppProperties.selectDriverId)
    val text: LiveData<String> = _text
     var timer=object: CountDownTimer(1,1){
         override fun onTick(millisUntilFinished: Long) {
             TODO("Not yet implemented")
         }

         override fun onFinish() {
             TODO("Not yet implemented")
         }
     }
    private val _uiEvent = MutableLiveData<Event<HomeUiEvent>>()
    val uiEvent: LiveData<Event<HomeUiEvent>> = _uiEvent

    fun autorization(){
        Repository.autorization(AppProperties.login_api,AppProperties.password_api)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ token ->
                AppProperties.token=token
                Log.d("MainViewModeltoken", "Success\n" + token)
            }, { throwable ->
                Log.e("MainViewModeltoken", "Error\n" + throwable.message.toString())
            })
        Repository.getDeliveryOrder("2222","Bearer "+AppProperties.token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ token ->
                token.deliveryOrderStatus=DeliveryOrderStatus.Ready
                AppProperties.selectDriverId.let {
                    token.driverId=it.toInt()
                }
                mainrep.insertDeliveryOrders(token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ id ->
                        AppProperties.isNewOrder=true
                        println("success. id = $id")
                    }, { exception ->
                        println("error: ${exception.message}")
                    })
                Log.d("MainViewModeltoken", "sss\n" + token)
            }, { throwable ->
                Log.e("MainViewModeltoken", "Error\n" + throwable.message.toString())
            })
        Log.e("success. id = ",mainrep.getAllDrivers().toString())

    }
    fun InitTimer(){
        try {
            val mainHandler = Handler(Looper.getMainLooper())
            mainHandler.post(object : Runnable {
                override fun run() {
                    _uiEvent.value = Event(HomeUiEvent.Reload(true))
                    Log.d("MainViewModeltoken", "timer")
                    mainHandler.postDelayed(this, 5000)
                }
            })


        }catch (e:Exception){
            Log.d("MainViewModeltoken", "timerException")
        }

    }
    fun LoadRestaurant(){

        Repository.autorization(AppProperties.login_api, AppProperties.password_api)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ token ->
                    AppProperties.token=token
                    Log.d("MainViewModeltoken", "Success\n" + token)
                }, { throwable ->
                    Log.e("MainViewModeltoken", "Error\n" + throwable.message.toString())
                })
        Repository.getRestauranList ("Bearer "+ AppProperties.token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ restList ->
                    restList.forEach { rest ->
                        RoomRepository.isRestaurant(rest.externalNo)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ it->
                                    Log.d("MainViewModeltoken", "Success\n" + it)
                                        rest.id=it.id
                                        RoomRepository.updateRestaurant(rest)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe({

                                                }, { exception ->
                                                    println("error: ${exception.message}")
                                                })


                                }, { exception ->
                                    RoomRepository.initRestaurant(rest)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe({

                                            }, { exception ->
                                                println("error: ${exception.message}")
                                            })
                                    println("error: ${exception.message}")
                                })


                    }
                    LoadRestaurantGps()
                }, { throwable ->
                    Log.e("MainViewModeltoken", "Error\n" + throwable.message.toString())
                })
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
                                    mainrep.updateDeliveryOrders(token)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({

                                        }, { exception ->
                                            println("error: ${exception.message}")
                                        })
                                    println("success. id = $id")
                                }, { exception ->
                                    mainrep.insertDeliveryOrders(token)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({ id ->
                                            AppProperties.isNewOrder=true
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
    fun LoadRestaurantGps(){
        try {
            Repository.autorization(AppProperties.login_api, AppProperties.password_api)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ token ->
                        AppProperties.token = token
                        RoomRepository.getRestaurantsApi()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ restaurants ->
                                    restaurants.forEach { rest->
                                        if (rest.restaurantGeolocationId > 0) {
                                            Repository.getRestaurantGeolocation(rest.restaurantGeolocationId, "Bearer " + AppProperties.token)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe { restGeo ->
                                                        Log.d("MainViewModeltoken", "restGeo\n" + restGeo)
                                                        RoomRepository.isRestaurantGps(restGeo.externalNo)
                                                                .subscribeOn(Schedulers.io())
                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                .subscribe({ gps ->
                                                                    restGeo.id = gps.id
                                                                    RoomRepository.updateRestaurantGps(restGeo)
                                                                            .subscribeOn(Schedulers.io())
                                                                            .observeOn(AndroidSchedulers.mainThread())
                                                                            .subscribe({
                                                                            }, { exception ->
                                                                                println("error: ${exception.message}")
                                                                            })
                                                                }, { exception ->
                                                                    RoomRepository.initRestaurantGps(restGeo)
                                                                            .subscribeOn(Schedulers.io())
                                                                            .observeOn(AndroidSchedulers.mainThread())
                                                                            .subscribe({

                                                                            }, { excep ->
                                                                                println("error: ${excep.message}")
                                                                            })
                                                                    println("error: ${exception.message}")
                                                                })
                                                    }
                                        }
                                    }
                                }, { exception ->
                                    Log.e("MainViewModeltoken", "Error\n" + exception.message.toString())
                                })
                        Log.d("MainViewModeltoken", "Success\n" + token)
                    }, { throwable ->
                        Log.e("MainViewModeltoken", "Error\n" + throwable.message.toString())
                    })


        }catch (e:Exception){

        }
    }
    fun startSwift(){
        _uiEvent.value = Event(HomeUiEvent.Loading(true))
        Repository.autorization(AppProperties.login_api, AppProperties.password_api)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ token ->
                AppProperties.token=token
                if(AppProperties.selectDriverId!="0"){
                    Log.e("MainViewModeltoken",  AppProperties.selectDriverId )
                    Repository.setDriverStatus(AppProperties.selectDriverId,"4", "Bearer "+AppProperties.token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ res ->
                            AppProperties.selectDriverStatus = 4
                            _uiEvent.value = Event(HomeUiEvent.Loading(false))
                            _uiEvent.value = Event(HomeUiEvent.Reload(true))
                            _uiEvent.value = Event(HomeUiEvent.Shift(true)) 
                            Log.d("MainViewModeltoken", "Success\n" + token)
                        }, { throwable ->
                            _uiEvent.value = Event(HomeUiEvent.Loading(false))
                            AppProperties.isShiftEnable=false
                            Log.e("MainViewModeltoken", "Error555\n" + throwable.message.toString())
                        })
                }else{

                    _uiEvent.value = Event(HomeUiEvent.Loading(false))
                }
                Log.d("MainViewModeltoken", "Success5555\n" + token)
            }, { throwable ->
                _uiEvent.value = Event(HomeUiEvent.Loading(false))
                Log.e("MainViewModeltoken", "Errorthrowable\n" + throwable.message.toString())
            })
    }
    fun finishSwift(){
        _uiEvent.value = Event(HomeUiEvent.Loading(true))
        Repository.autorization(AppProperties.login_api, AppProperties.password_api)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ token ->
                AppProperties.token=token
                if(AppProperties.selectDriverId!="0"){
                    Repository.setDriverStatus(AppProperties.selectDriverId,"3", "Bearer "+AppProperties.token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ res ->
                            AppProperties.isShiftEnable=false
                            AppProperties.selectDriverStatus=3
                            AppProperties.countOrdersToday=0
                            Log.d("MainViewModeltoken", "Success\n" + token)
                            _uiEvent.value = Event(HomeUiEvent.Reload(true))
                            _uiEvent.value = Event(HomeUiEvent.Loading(false))
                            _uiEvent.value = Event(HomeUiEvent.Shift(false))
                        }, { throwable ->
                            _uiEvent.value = Event(HomeUiEvent.Loading(false))
                            AppProperties.isShiftEnable=true
                            Log.e("MainViewModeltoken", "Error\n" + throwable.message.toString())
                        })
                }else{
                    _uiEvent.value = Event(HomeUiEvent.Loading(false))
                }
                Log.d("MainViewModeltoken", "Success\n" + token)
            }, { throwable ->
                _uiEvent.value = Event(HomeUiEvent.Loading(false))
                Log.e("MainViewModeltoken", "Error\n" + throwable.message.toString())
            })
    }
}